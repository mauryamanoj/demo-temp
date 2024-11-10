package com.sta.core.solr.servlet;

import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.services.SolrSearchService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.sta.core.MmCoreException;
import com.sta.core.solr.utils.SolrUtils;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

/**
 * This servlet acts as a bulk update to index content pages and assets to the
 * configured Solr server.
 * <p>
 * index dir:
 * http://localhost:4502/bin/api/v1/solr/push/pages./content/sauditourism/en?action=index
 * <p>
 * delete page:
 * http://localhost:4502/bin/api/v1/solr/push/pages./content/sauditourism/app/en/do?action=delete
 */
@Slf4j
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Solr rebuild servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/api/v1/solr/push/pages"})
public class IndexContentToSolr extends SlingAllMethodsServlet {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The resource resolver factory.
   */
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  /**
   * solrSearchService.
   */
  @Reference
  private transient SolrSearchService solrSearchService;

  /**
   * Sling Repo.
   */
  @Reference
  private SlingRepository repository;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String path = request.getRequestPathInfo().getSuffix();
    String action = request.getParameter("action");
    String recursive = request.getParameter("recursive");

    try {
      if (StringUtils.isEmpty(path) || StringUtils.isEmpty(action)) {
        throw new ServletException("suffix path or action param is empty");
      }

      ResourceResolver resolver = request.getResourceResolver();
      if (CommonUtils.validateBasicAuth(request, repository)) {
        if (action.equalsIgnoreCase("index") || action.equalsIgnoreCase("indexall")) {
          boolean force = action.equalsIgnoreCase("indexall");
          indexPage(resolver.resolve(path), force);
          response.getWriter().write("<h3>Successfully indexed content pages to Solr server </h3>");
        } else if (action.equalsIgnoreCase("delete")) {
          removePagesFromIndex(path, resolver, recursive);
          response.getWriter().write("<h3>Successfully deleted from Solr server </h3>");
        } else if (action.equalsIgnoreCase("deleteall")) {
          deletePageWithChild(resolver.resolve(path));
          response.getWriter().write("<h3>Successfully deleted from Solr server </h3>");
        } else if (action.equalsIgnoreCase("clear")) {
          solrSearchService.deleteAllFromSolr(path);
          response.getWriter().write("<h3>Successfully all deleted from Solr server </h3>");
        } else {
          response.getWriter().write("<h3>Unknown action value</h3>");
        }
      } else {
        LOGGER.error("Index Content Solr not Authorized");
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), "Not Authorized"));
      }
    } catch (Exception e) {
      LOGGER.error("solr/push/pages./ error:" + e.getMessage(), e);
      response.getWriter().write("Error occured, Please check logs");
    }
  }

  /**
   * Remove pages from index.
   * @param path      the path
   * @param resolver  the resolver
   * @param recursive the recursive
   * @throws MmCoreException the exception
   */
  private void removePagesFromIndex(final String path, final ResourceResolver resolver,
      final String recursive) throws MmCoreException {
    solrSearchService.deletePageFromSolr(path);

    Resource resource = resolver.getResource(path);
    if ("true".equals(recursive) && Objects.nonNull(resource) && resource.hasChildren()) {
      for (Resource child : resource.getChildren()) {
        if (child.getResourceType().equals(NT_PAGE)) {
          solrSearchService.deletePageFromSolr(child.getPath());
        }
      }
    }
  }

  /**
   * Send page to SOLR.
   * @param resource current resource
   * @param force    whether to index all content
   * @throws Exception Exception
   */
  private void indexPage(Resource resource, boolean force) throws Exception {
    if (SolrUtils.allowedSolrPath(resource.getPath())) {
      if (SolrUtils.allowedSolrResource(resource) && isPublishedPage(resource, force)) {
        SolrArticle solrArticle = solrSearchService.createPageMetadataObject(resource);
        if (solrArticle != null) {
          solrSearchService.indexPageToSolr(solrArticle);
        }
      }
      for (Resource child : resource.getChildren()) {
        indexPage(child, force);
      }
    } else if (SolrUtils.allowedSolrRecursiveIndex(resource.getPath())) {

      for (Resource child : resource.getChildren()) {
        indexPage(child, force);
      }

    }
  }



  /**
   * Deletes page and subpages from SOLR.
   * @param resource current resource
   * @throws Exception IOException
   */
  private void deletePageWithChild(Resource resource) throws Exception {
    if (resource.getResourceType().equals(NT_PAGE)) {
      solrSearchService.deletePageFromSolr(resource.getPath());
      for (Resource child : resource.getChildren()) {
        deletePageWithChild(child);
      }
    }
  }

  /**
   * Is Published page. For publisher replicationStatus will be null so check if
   * force=true(indexall) && page has jcr contentResource
   * @param resource current resource
   * @param force    true if param is indexall:publish else false for author
   * @return true if page is active else false
   */
  private boolean isPublishedPage(Resource resource, boolean force) {
    Page page = resource.adaptTo(Page.class);
    if (Objects.nonNull(page)) {
      ReplicationStatus replicationStatus = page.adaptTo(ReplicationStatus.class);
      return (Objects.nonNull(replicationStatus) && replicationStatus.isActivated());
    }
    return false;
  }
}
