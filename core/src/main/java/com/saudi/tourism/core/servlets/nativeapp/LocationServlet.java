package com.saudi.tourism.core.servlets.nativeapp;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.common.AppPageRequestResultModel;
import com.saudi.tourism.core.models.app.cruise.AppCruiseModel;
import com.saudi.tourism.core.models.app.location.AppLocationPageModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that retrieves the information for a location.
 * http://localhost:4502/bin/api/v1/app/location./content/sauditourism/app/en/location-page
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Location Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/api/v2/app/location"})
@Slf4j
public class LocationServlet extends SlingAllMethodsServlet {

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String path = request.getRequestPathInfo().getSuffix();
    if (!StringUtils.isEmpty(path)) {
      final AppPageRequestResultModel model = getPageInformation(request, path);
      if (model == null) {
        // FIXME: temporary until mobile app can handle 404 on universal link
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NO_CONTENT.getValue(),
            "No location information for the page");
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(model));
      }
    }
  }

  /**
   * Get the location page information.
   *
   * @param request request
   * @param path path
   * @return model
   */
  private AppPageRequestResultModel getPageInformation(SlingHttpServletRequest request,
      String path) {

    final String resourceJcrContentPath =
        path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT;
    final ResourceResolver resourceResolver = request.getResourceResolver();
    Resource resource = resourceResolver.getResource(resourceJcrContentPath);
    if (resource == null) {
      LOGGER.error("Couldn't get resource by path [{}], user: [{}]", resourceJcrContentPath,
          resourceResolver.getUserID());
      return null;
    }

    if (resource.isResourceType(Constants.RT_APP_CRUISE_PAGE)) {
      final AppCruiseModel model = resource.adaptTo(AppCruiseModel.class);
      if (model == null) {
        LOGGER.error("Couldn't adapt cruise resource to model {}", resource);
      }
      return model;

    } else if (resource.isResourceType(Constants.APP_LOCATION_RESOURCE_TYPE)) {
      final AppLocationPageModel model = resource.adaptTo(AppLocationPageModel.class);
      if (model == null) {
        LOGGER.error("Couldn't adapt app location page resource to model {}", resource);
        return null;
      }

      return model;

    } else {
      LOGGER.error("Resource is of unknown resource type to process [{}], null will be returned",
          resource);
      return null;
    }
  }
}
