package com.saudi.tourism.core.servlets;

import com.google.gson.Gson;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * The servlet is used to update site map configs.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Update SiteMap Config Servlet "
        + "Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
            + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/updateSiteMapConfigServlet"})
@Slf4j
public class UpdateSiteMapConfigServlet extends
    org.apache.sling.api.servlets.SlingAllMethodsServlet {
  private static final long serialVersionUID = 2598426539166789515L;
  /**
   * The Gson gson.
   */
  private transient Gson gson = new Gson();
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
  @Generated
  public void bindRepository(SlingRepository repository) {
    this.repository = repository;
  }


  @Override
  @Generated
  protected void doGet(SlingHttpServletRequest request,
      SlingHttpServletResponse response) {
    try {
      List<String> listOfConfigNodes = updateConfigPageNodes(request);
      response.getWriter().print(gson.toJson(listOfConfigNodes));
      response.getWriter().print(gson.toJson(listOfConfigNodes.size()) + " content nodes have been "
          + "successfully updated !!!");
    } catch (Exception e) {
      LOGGER.error("Error while executing UpdateSiteMapConfigServlet " + e.getMessage(), e);

    }
  }


  /**
   * updateConfigPageNodes.
   * @param request SlingHttpServletRequest
   * @return List<String> .
   * @throws RepositoryException .
   * @throws PersistenceException .
   */
  @Generated
  private List<String> updateConfigPageNodes(SlingHttpServletRequest request)
      throws RepositoryException, PersistenceException {
    String stmt = "SELECT * FROM [cq:PageContent] AS s WHERE "
        + "ISDESCENDANTNODE(s,'/content/sauditourism')  AND "
        + "[sling:resourceType] = 'sauditourism/components/structure/configuration-page' "
        + "OR [sling:resourceType] = 'sauditourism/components/structure/reference-page'";
    Session session = request.getResourceResolver().adaptTo(Session.class);
    Query query = session.getWorkspace().getQueryManager().createQuery(stmt, Query.JCR_SQL2);
    QueryResult results = query.execute();
    List<String> listOfConfigNodes = new ArrayList<>();
    NodeIterator nodeIterator = results.getNodes();
    while (nodeIterator.hasNext()) {
      Node contentNode = (Node) nodeIterator.next();
      if (contentNode != null) {
        contentNode.setProperty("hideInSiteMap", "true");
        session.save();
        listOfConfigNodes.add(contentNode.getPath());
      }
    }
    request.getResourceResolver().commit();
    return listOfConfigNodes;
  }
}
