package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.nativeApp.page.PageInfo;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * Servlet that retrieves the list of content pages for native app.
 * http://localhost:4502/bin/api/v2/app/pages./en
 */
@Component(service = Servlet.class,
      property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Pages Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v2/app/pages"})
@Slf4j
public class PagesServlet extends SlingAllMethodsServlet {

  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(PagesServlet.class);

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {

    String resourcePath = CommonUtils.getNativeGetAppPath(request.getRequestPathInfo().getSuffix());

    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
            .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);
        List<PageInfo> listOfPages = searchRecursivelyPropPres(node, null);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(listOfPages));
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
              "No information for the content pages");
      }
    } catch (Exception e) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            "No information for the content pages");
    }

  }

  /**
   * Retrieves the list of pages.
   *
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @return list of pages
   */
  @Generated
  public static List<PageInfo> searchRecursivelyPropPres(Node node,
                                                         List<PageInfo> searchResults) {

    if (searchResults == null) {
      searchResults = new ArrayList<>();
    }

    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty(NN_TEMPLATE) && currentSubNode.getProperty(NN_TEMPLATE)
              .getString().equals(Constants.APP_CONTENT_TEMPLATE)) {

          PageInfo currentPage = new PageInfo();
          currentPage.setPage(currentSubNode.getPath()
                .replace(Constants.FORWARD_SLASH_CHARACTER + JCR_CONTENT, Constants.BLANK));

          if (currentSubNode.hasProperty(Constants.APP_PUBLISH_DATE)) {
            currentPage.setPublishDate(
                  currentSubNode.getProperty(Constants.APP_PUBLISH_DATE).getValue().toString());
          }

          if (currentSubNode.hasProperty(Constants.APP_LOCATION_TYPE)) {
            currentPage.setType(
                currentSubNode.getProperty(Constants.APP_LOCATION_TYPE).getValue().toString());
          }

          if (currentSubNode.hasProperty(JCR_TITLE)) {
            currentPage.setTitle(
                  currentSubNode.getProperty(JCR_TITLE).getValue().toString());
          }
          if (currentSubNode.hasProperty(JCR_DESCRIPTION)) {
            LOGGER.info("JcrDescription for page Info Enter");
            currentPage.setDescription(
                 currentSubNode.getProperty(JCR_DESCRIPTION).getValue().toString());
            LOGGER.info("JcrDescription for page Info currentPage Description" + currentPage.getDescription());
          }
          if (currentSubNode.hasProperty(Constants.PREVIEW_FEATURED_IMAGE)) {
            LOGGER.info("PREVIEW IMAGE for page Info Enter");
            currentPage.setPreviewImage(
                 currentSubNode.getProperty(Constants.PREVIEW_FEATURED_IMAGE).getValue().toString());
            LOGGER.info("PREVIEW IMAGE for page Info currentPage PREVIEW IMAGE" + currentPage.getPreviewImage());
          }

          if (currentSubNode.hasProperty(Constants.FEATURED_IMAGE)) {
            LOGGER.info("FEATURED_IMAGE IMAGE for page Info Enter");
            currentPage.setFeaturedImage(
                  currentSubNode.getProperty(Constants.FEATURED_IMAGE).getValue().toString());
            LOGGER.info("FEATURED_IMAGE for page Info currentPage PREVIEW IMAGE" + currentPage.getFeaturedImage());
          }
          if (currentSubNode.hasProperty(Constants.FEATURED)) {
            currentPage.setFeatured(
                  currentSubNode.getProperty(Constants.FEATURED).getValue().getBoolean());
          }
          searchResults.add(currentPage);
        }
        searchRecursivelyPropPres(currentSubNode, searchResults);
      }
      return searchResults;
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the content for the app. " + rpe.getMessage());
    }
    return searchResults;
  }

}
