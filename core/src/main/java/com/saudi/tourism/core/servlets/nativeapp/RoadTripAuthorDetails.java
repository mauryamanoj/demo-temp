package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/**
 * Road Trip Get Author Details Servlet .
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "RoadTrip Get Author Details Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v2/roadtrip/authorDetails"})
public class RoadTripAuthorDetails extends SlingAllMethodsServlet {
  /**
   * roadTripScenariosService.
   */
  @Reference
  private RoadTripScenariosService roadTripScenariosService;


  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String userId = request.getParameter("user_id");
    String locale = request.getParameter("locale");
    try {
      if (StringUtils.isEmpty(locale)) {
        locale = "en";
      }
      if (StringUtils.isNotBlank(userId)) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(
            roadTripScenariosService.getRoadTripAuthorDetails(userId, locale)).toString());
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            "Please pass the UserId");
      }
    } catch (IOException | JSONException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
