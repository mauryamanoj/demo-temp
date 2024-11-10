package com.sta.core.solr.replication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.day.cq.replication.ContentBuilder;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationContent;
import com.day.cq.replication.ReplicationContentFactory;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Solr content builder to create replication content containing a JSON array
 * of URLs for cdn to purge through the cdn Transport Handler. This class
 * takes the internal resource path and converts it to external URLs as well as
 * adding vanity URLs and pages that may Sling include the activated resource.
 */
@Component(immediate = true,
           service = ContentBuilder.class, property = {"name=solr-index"})
public class SolrContentBuilder implements ContentBuilder {

  /**
   * The Resolver factory.
   */
  @Reference
  private ResourceResolverFactory resolverFactory;

  /**
   * The name of the replication agent.
   */
  private static final String NAME = "solr-index";

  /**
   * The serialization type as it will display in the replication
   * agent edit dialog selection field.
   */
  private static final String TITLE = "CDN Purge Agent";

  /**
   * {@inheritDoc}
   */
  @Override public ReplicationContent create(Session session, ReplicationAction action,
      ReplicationContentFactory factory) throws ReplicationException {
    return create(session, action, factory, null);
  }

  /**
   * Create the replication content containing the public facing URLs for
   * cdn to purge.
   */
  @Override public ReplicationContent create(Session session, ReplicationAction action,
      ReplicationContentFactory factory, Map<String, Object> parameters)
      throws ReplicationException {

    final String path = action.getPath();
    final ReplicationLog log = action.getLog();

    if (StringUtils.isNotBlank(path)) {
      try (ResourceResolver resolver = getResourceResolver(session)) {
        PageManager pageManager = resolver.adaptTo(PageManager.class);

        if (pageManager != null) {
          JsonArray jsonArray = new JsonArray();
          Page purgedPage = pageManager.getPage(path);

          /*
           * Get the external URL if the resource is a page. Otherwise, use the
           * provided resource path.
           */
          if (purgedPage != null) {

            jsonArray.add(path);
            log.info("Page link added: " + path);

            /*
             * Add page's vanity URL if it exists.
             */
            final String vanityUrl = purgedPage.getVanityUrl();

            if (StringUtils.isNotBlank(vanityUrl)) {
              jsonArray.add(vanityUrl);
              log.info("Vanity URL added: " + vanityUrl);
            }

            /*
             * Get containing pages that includes the resource.
             */
            // Run project specific query

          } else {
            jsonArray.add(path);
            log.info("Resource path added: " + path);
          }

          return createContent(factory, path);
        }
      } catch (LoginException e) {
        log.error("Could not retrieve Page Manager", e);
      }
    }

    return ReplicationContent.VOID;
  }

  /**
   * Create the replication content containing.
   *
   * @param factory   Factory to create replication content
   * @param path path
   * @return replication content
   * @throws ReplicationException if an error occurs
   */
  private ReplicationContent createContent(final ReplicationContentFactory factory,
      final String path) throws ReplicationException {

    Path tempFile;

    try {
      tempFile = Files.createTempFile("slr_agent", ".tmp");
    } catch (IOException e) {
      throw new ReplicationException("Could not create temporary file", e);
    }

    try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
      writer.write(path);
      writer.flush();

      return factory.create("text/plain", tempFile.toFile(), true);
    } catch (IOException e) {
      throw new ReplicationException("Could not write to temporary file", e);
    }
  }

  /**
   * Get resource resolver from session.
   * @param session user session
   * @return resource resolver
   * @throws LoginException exception
   */
  private ResourceResolver getResourceResolver(Session session) throws LoginException {
    HashMap<String, Object> sessionMap = new HashMap<>();
    sessionMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
    return resolverFactory.getResourceResolver(sessionMap);
  }

  /**
   * {@inheritDoc}
   *
   * @return {@value #NAME}
   */
  @Override public String getName() {
    return NAME;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@value #TITLE}
   */
  @Override public String getTitle() {
    return TITLE;
  }
}
