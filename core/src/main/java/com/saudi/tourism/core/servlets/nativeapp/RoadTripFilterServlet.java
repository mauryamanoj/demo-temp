package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.RoadTripFilterService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.MessageType;
import lombok.extern.slf4j.Slf4j;
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
 * This is used to get all road trip filters from admin page authoring.
 * Sample URL :
 * http://localhost:4502/bin/api/v2/app/roadTripFilter.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + Constants.ROADTRIP_FILTER_SERVLET,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/app/roadTripFilter"})
@Slf4j
public class RoadTripFilterServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;

  /**
   * The RoadTrip Filter service.
   */
  @Reference
  private transient RoadTripFilterService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String locale = "en";
      if (null != request.getRequestParameter("locale")) {
        locale = request.getRequestParameter("locale").toString();
      }
      CommonUtils.writeNewJSONFormatExclude(response, StatusEnum.SUCCESS.getValue(),
          service.getRoadTripFilters(locale));
      LOGGER.error("Success :{}, {}", Constants.ROADTRIP_FILTER_SERVLET, response);
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.ROADTRIP_FILTER_SERVLET,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

}

