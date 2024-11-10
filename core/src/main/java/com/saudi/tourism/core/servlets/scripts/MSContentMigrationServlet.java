package com.saudi.tourism.core.servlets.scripts;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Objects;

/**
 * MSM Content Migration Servlet.
 * Script will add below mentioned content (deprecated):
 * 1. Add Node cq:LiveSyncConfig at language root level
 * 2. Add/Update property jcr:mixinTypes=cq:LiveRelationship to all nodes
 * 3. Add property cq:isCancelledForChildren(Boolean)=true for Localized content to escape on
 * component
 * 4. Add cq:propertyInheritanceCancelled(String[])=propertyNames and
 * jcr:minInTypes = cq:PropertyLiveSyncCancelled to cancel property inheritance on content node
 * <p>
 * Note: Above mentioned steps can be managed by LiveRelationShipManager API.
 * <p>
 * http://localhost:4502/bin/script/api/v1/msm-content-migration?source=&destination=
 * params:
 * 1. source=/content/sauditourism/en
 * 2. destination=/content/sauditourism/ar
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "MSM Content Migration Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
        + "/bin/script/api/v1/msm-content-migration"})
@Slf4j
public class MSContentMigrationServlet extends SlingAllMethodsServlet {

  /**
   * RollOut Manager.
   */
  @Reference
  private transient RolloutManager rolloutManager;

  /**
   * LiveRelationShipManager.
   */
  @Reference
  private transient LiveRelationshipManager liveRelationshipManager;

  /**
   * The SlingRepository repository.
   */
  @Reference
  private transient SlingRepository repository;

  /**
   * bindRepository.
   *
   * @param repository SlingRepository
   */
  @Generated public void bindRepository(SlingRepository repository) {
    this.repository = repository;
  }

  @Override protected void doGet(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response) throws ServletException, IOException {
    LOGGER.debug("Invoking MSM Content Migration Servlet");
    final ResourceResolver resourceResolver = request.getResourceResolver();
    final Session session = resourceResolver.adaptTo(Session.class);
    final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    final PrintWriter out = response.getWriter();
    final String rollOutConfigPath = "/libs/msm/wcm/rolloutconfigs/default";
    final RolloutConfigManager rolloutConfigManager =
        resourceResolver.adaptTo(RolloutConfigManager.class);

    final String source = request.getParameter("source");
    final String destination = request.getParameter("destination");
    boolean rollOut = Boolean.parseBoolean(request.getParameter("rollout"));
    String relationshipAction = request.getParameter("relationship");
    boolean enableRelationship = true;
    if (StringUtils.isNotBlank(relationshipAction) && relationshipAction.equals("disable")) {
      enableRelationship = false;
    }

    if (StringUtils.isNoneBlank(destination, source) && Objects.nonNull(pageManager) && Objects
        .nonNull(liveRelationshipManager) && Objects.nonNull(rolloutManager) && Objects
        .nonNull(rolloutConfigManager)) {
      try {
        Page sourcePage = pageManager.getPage(source);
        Page destPage = pageManager.getContainingPage(destination);
        RolloutConfig rolloutConfig = rolloutConfigManager.getRolloutConfig(rollOutConfigPath);
        if (StringUtils.isEmpty(relationshipAction)) {
          visitRecursively(destPage, resourceResolver, rollOut, session);
        }
        manageLiveRelationShip(rolloutConfig, resourceResolver, sourcePage, destPage, rollOut,
            enableRelationship, relationshipAction);
        out.println("Pages Rolled out successfully!");
        if (Objects.nonNull(session)) {
          session.save();
        }
        resourceResolver.commit();
        out.flush();
      } catch (Exception ex) {
        out.println("Error Occurred in processing RollOut. Check logs." + ex.getMessage());
        LOGGER.error("Exception Occurred in MSContentMigration. " + ex.getMessage(), ex);
      }
    } else {
      out.println("Required Parameters {source / root} are missing.");
      LOGGER.debug("Required Parameters {source / root} are missing.");
    }
  }

  /**
   * Manage Relationship.
   * if relationship param is unavailable: setup msm live copies
   * if relationship != null and relationship == disable : cancel(suspend) live relationship
   * if relationship != null and relationship == enable : reEnable live relationship
   *
   * @param rolloutConfig      rollOutConfig
   * @param resourceResolver   resourceResolver
   * @param sourcePage         sourcePage
   * @param destPage           destPage
   * @param rollOut            rollout
   * @param enableRelationship enableLiveRelationship if true else cancelLiveRelationship
   * @param relationship       relationship
   * @throws WCMException WCMException
   */
  private void manageLiveRelationShip(final RolloutConfig rolloutConfig,
      final ResourceResolver resourceResolver, final Page sourcePage, final Page destPage,
      final boolean rollOut, final boolean enableRelationship, final String relationship)
      throws WCMException {
    LiveRelationship liveRelationship;
    if (StringUtils.isEmpty(relationship)) {
      liveRelationship = liveRelationshipManager
          .establishRelationship(sourcePage, destPage, true, true, rolloutConfig);
    } else {
      liveRelationship =
          liveRelationshipManager.getLiveRelationship(destPage.getContentResource(), false);
      if (enableRelationship && Objects.nonNull(liveRelationship)) {
        liveRelationshipManager.reenableRelationship(resourceResolver, liveRelationship, true);
      } else if (Objects.nonNull(liveRelationship)) {
        liveRelationshipManager.cancelRelationship(resourceResolver, liveRelationship, true, true);
      }
    }
    if (Objects.nonNull(liveRelationship) && rollOut) {
      rolloutManager.rollout(resourceResolver, liveRelationship, true);
    }
  }

  /**
   * Visit Recursively and update pages.
   *
   * @param page             page
   * @param resourceResolver resourceResolver
   * @param rollOut          rollOut
   * @param session          session
   * @throws WCMException WCMException
   */
  private void visitRecursively(final Page page, final ResourceResolver resourceResolver,
      final boolean rollOut, final Session session) throws WCMException {
    if (Objects.nonNull(page) && Objects.nonNull(page.getContentResource())) {
      LiveRelationship liveRelationship =
          liveRelationshipManager.getLiveRelationship(page.getContentResource(), false);
      if (Objects.nonNull(liveRelationship) && rollOut) {
        try {
          session.refresh(true);
          rolloutManager.rollout(resourceResolver, liveRelationship, true);
        } catch (RepositoryException ex) {
          LOGGER.error("Exception while rolling out pages" + ex.getMessage(), ex);
        }
      }
      Iterator<Page> childIterator = page.listChildren();
      while (childIterator.hasNext()) {
        Page childPage = childIterator.next();
        visitRecursively(childPage, resourceResolver, rollOut, session);
      }
    }
  }
}
