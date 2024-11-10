package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.app.page.PageInfo;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * Servlet that retrieves the list of content pages.
 * http://localhost:4502/bin/api/v1/app/loyalty/pages./en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Loyalty Pages Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/loyalty/pages"})
@Slf4j
public class LoyaltyPagesServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    String resourcePath = CommonUtils.getGetAppPath(request.getRequestPathInfo().getSuffix());

    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
          .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);
        List<PageInfo> listOfPages = searchRecursivelyPropPres(node, null);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), listOfPages);
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                "No information for the content pages"));
      }
    } catch (Exception e) {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "No information for the content pages"));
    }

  }

  /**
   * Retrieves the list of pages.
   *
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @return list of pages
   */
  @Generated public static List<PageInfo> searchRecursivelyPropPres(Node node,
      List<PageInfo> searchResults) {

    if (searchResults == null) {
      searchResults = new ArrayList<>();
    }

    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty(NN_TEMPLATE) && currentSubNode.getProperty(NN_TEMPLATE)
            .getString().equals(Constants.APP_LOYALTY_CONTENT_TEMPLATE)) {

          PageInfo currentPage = new PageInfo();
          currentPage.setPage(currentSubNode.getPath()
              .replace(Constants.FORWARD_SLASH_CHARACTER + JCR_CONTENT, Constants.BLANK));

          if (currentSubNode.hasProperty(Constants.APP_PUBLISH_DATE)) {
            currentPage.setPublishDate(
                currentSubNode.getProperty(Constants.APP_PUBLISH_DATE).getValue().toString());
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
