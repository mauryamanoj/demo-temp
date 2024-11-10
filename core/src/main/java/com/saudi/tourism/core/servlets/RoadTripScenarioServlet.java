package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
 * This is used to get all currency data via an external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v1/currency?currency=SAR . Sample URL :
 * http://localhost:4502/bin/api/v1/currency?currency=EUR,GBP,CAD,PLN,LKR,INR .
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "RoadTrip Data Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/roadtrip" })
@Slf4j
public class RoadTripScenarioServlet extends SlingAllMethodsServlet {

  /**
   * The RoadTrip Data service.
   */
  @Reference
  private transient RoadTripScenariosService roadTripScenariosService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String limitParam = request.getParameter("limit");
    int limit = 0;
    try {
      if (!StringUtils.isBlank(limitParam)) {
        limit = Integer.valueOf(limitParam);
      }
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          roadTripScenariosService.getRoadTripScenariosFromApi(limit));

      LOGGER.error("Success :{}, {}", Constants.CURRENCY_DATA_SERVLET, response);

    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.CURRENCY_DATA_SERVLET,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

}
