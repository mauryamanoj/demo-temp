package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * The type Get all locale events filters.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Event Filters Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/events/filters"})
@Slf4j public class EventsFiltersServlet extends SlingSafeMethodsServlet {
  /**
   * The Event service.
   */
  @Reference private transient EventService eventService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    EventsRequestParams eventsRequestParameters =
        new Convert<EventsRequestParams>(request, EventsRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(eventsRequestParameters.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }
      CommonUtils.writeNewJSONFormatExclude(response, StatusEnum.SUCCESS.getValue(),
          eventService.getDynamicEventsFilters(eventsRequestParameters.getLocale()));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", Constants.EVENT_FILTER_SERVLET, e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }

  }
}
