package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.app.page.LocationPageInfo;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
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
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * Servlet that retrieves the list of location pages.
 * http://localhost:4502/bin/api/v1/app/locations./en
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Locations Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v1/app/locations"})
@Slf4j
public class LocationsServlet extends SlingAllMethodsServlet {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    String resourcePath = CommonUtils.getGetAppPath(request.getRequestPathInfo().getSuffix());

    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
          .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);

        List<LocationPageInfo> listOfPages = searchRecursivelyPropPres(node, null, saudiTourismConfig);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), listOfPages);
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
            "No information for the location pages"));
      }
    } catch (Exception e) {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(),
          "No information for the location pages"));
    }

  }

  /**
   * Retrieves the list of location pages.
   *
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @param saudiTourismConfigs saudiTourismConfigs
   * @return list of pages
   */
  @Generated
  public static List<LocationPageInfo> searchRecursivelyPropPres(Node node,
                                                                 List<LocationPageInfo> searchResults,
                                                                 SaudiTourismConfigs saudiTourismConfigs) {

    if (searchResults == null) {
      searchResults = new ArrayList<>();
    }

    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        String template = null;
        if (currentSubNode.hasProperty(NN_TEMPLATE)) {
          template = currentSubNode.getProperty(NN_TEMPLATE).getString();
        }
        if (Constants.APP_LOCATION_TEMPLATE.equals(template)) {
          LocationPageInfo currentPage = new LocationPageInfo();
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
          String[] excludedPaths = saudiTourismConfigs.getExcludingPaths();
          if (null != excludedPaths && excludedPaths.length >= 1) {
            boolean result = Arrays.stream(excludedPaths).anyMatch(currentPage.getPage()::equals);
            if (!result) {
              searchResults.add(currentPage);
            }
          } else {
            searchResults.add(currentPage);
          }

        }
        searchRecursivelyPropPres(currentSubNode, searchResults, saudiTourismConfigs);

      }
      return searchResults;
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the locations for the app. " + rpe.getMessage());
    }
    return searchResults;
  }

}
