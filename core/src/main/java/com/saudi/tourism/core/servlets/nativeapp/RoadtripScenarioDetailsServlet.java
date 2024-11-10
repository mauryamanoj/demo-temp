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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Road trip Scenario Details by Id.
 */

@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "RoadTrip Scenario Details Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v2/roadtrip/details"})
public class RoadtripScenarioDetailsServlet extends SlingAllMethodsServlet {

  /**
   * roadTripScenariosService.
   */
  @Reference
  private RoadTripScenariosService roadTripScenariosService;

  /**
   * DoGEt method .
   *
   * @param request  request .
   * @param response response.
   * @throws ServletException ServletException .
   * @throws IOException      IOException .
   */
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String locale = request.getParameter("locale");
    String scenarioId = request.getParameter("scenarioId");
    if (StringUtils.isEmpty(locale)) {
      locale = "en";
    }

    if (StringUtils.isNotBlank(scenarioId)) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(
          roadTripScenariosService.getRoadTripScenarioDetailByIdNativeApp(locale, scenarioId)).toString());
    } else {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          "scenarioId not found");
    }
  }
}
