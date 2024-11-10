package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
 * RoadTripViewOnServelet to return the map Image on basis of scenarioid.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "RoadTrip View On Map Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/roadtrip/viewMap"})
@Slf4j

public class RoadTripViewOnMapServlet extends SlingAllMethodsServlet {
  /**
   * The RoadTrip Data service.
   */
  @Reference
  private transient RoadTripScenariosService roadTripScenariosService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String scenarioId = request.getParameter("scenario_id");
    String language = request.getParameter("language");
    String pathStyle = request.getParameter("pathStyle");
    String pinStyle = request.getParameter("pinStyle");
    String day = request.getParameter("day");
    String size = request.getParameter("size");
    int dayParam = 1;
    if (StringUtils.isEmpty(language)) {
      language = "en";
    }
    if (StringUtils.isEmpty(pathStyle)) {
      pathStyle = "color:0x0000ff";
    }
    if (StringUtils.isEmpty(pinStyle)) {
      pinStyle = "size:mid|color:0xff0000";
    }
    if (!StringUtils.isEmpty(day)) {
      dayParam = Integer.valueOf(day);
    }
    if (StringUtils.isEmpty(size)) {
      size = "640x640";
    }
    if (!StringUtils.isBlank(scenarioId)) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          roadTripScenariosService.
            getRoadTripViewOnMapUrl(scenarioId, language, pathStyle, pinStyle, dayParam, size).
            toString());
    } else {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          "UserId not found");
    }
  }
}
