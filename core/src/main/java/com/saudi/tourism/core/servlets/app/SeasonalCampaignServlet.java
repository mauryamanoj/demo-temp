package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.app.common.AppPageRequestResultModel;
import com.saudi.tourism.core.models.app.location.AppSeasonCampaignPageModel;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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
import java.util.Objects;

import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;
import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * Servlet that retrieves the information for a location.
 * http://localhost:4502/bin/api/v1/app/seasonal-campaign
 */
@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Seasonal campaign Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/seasonal-campaign"})
@Slf4j
public class SeasonalCampaignServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String localePath = null;
    if (StringUtils.isNotEmpty(request.getParameter("locale"))) {
      localePath =
          ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
              + request.getParameter("locale");
    }

    if (StringUtils.isNotEmpty(localePath)) {
      AppPageRequestResultModel model = null;
      ResourceResolver resourceResolver = request.getResourceResolver();
      Node node =
          Objects.requireNonNull(resourceResolver.getResource(localePath)).adaptTo(Node.class);
      if (node != null) {
        String path = searchRecursivelyPropPres(node);
        if (path != null) {
          model = getPageInformation(resourceResolver, path);
        }
      }
      if (model == null) {
        CommonUtils.writeJSON(response, StatusEnum.BAD_REQUEST.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                "seasonal campaign page was not found"));
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(model));
      }
    } else {
      //no locale
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Undefined locale"));
    }
  }

  /**
   * Retrieves the path of first SEASONAL CAMPAIGN page.
   *
   * @param node root node for the locale
   * @return path
   */
  private String searchRecursivelyPropPres(Node node) {
    String path = null;
    try {
      NodeIterator rootNode = node.getNodes();
      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty(NN_TEMPLATE) && currentSubNode
            .hasProperty(Constants.APP_LOCATION_TYPE)) {
          String template = currentSubNode.getProperty(NN_TEMPLATE).getString();
          if (Constants.APP_SEASONAL_TEMPLATE.equals(template)) {
            return currentSubNode.getPath();
          }
        }
        path = searchRecursivelyPropPres(currentSubNode);
        if (path != null) {
          return path;
        }
      }
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the locations for the app. " + rpe.getMessage());
    }
    return path;
  }

  /**
   * Get the location page information.
   *
   * @param resourceResolver resourceResolver
   * @param path             path
   * @return model
   */
  private AppSeasonCampaignPageModel getPageInformation(ResourceResolver resourceResolver,
      String path) {
    Resource resource = resourceResolver.getResource(path);
    if (Objects.nonNull(resource)) {
      final AppSeasonCampaignPageModel model = resource.adaptTo(AppSeasonCampaignPageModel.class);
      if (Objects.nonNull(model)) {
        return model;
      }
    }
    LOGGER.error("Couldn't adapt app location page resource to model {}", resource);
    return null;
  }
}
