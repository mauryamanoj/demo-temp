package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.beans.nativeapp.CalendarResponse;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.v2.CalendarAppService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;


/**
 * The type Get all events for a locale.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Calendar Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v2/calendar"
})
@Slf4j
public class CalendarServlet extends SlingSafeMethodsServlet {

  /**
   * The Event service.
   */
  @Reference
  private transient EventService eventService;

  /**
   * The Event service.
   */
  @Reference
  private transient CalendarAppService calendarAppService;

  @Override protected void doGet(@NotNull  SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    EventsRequestParams eventsRequestParams =
        new Convert<>(request, EventsRequestParams.class).getRequestData();

    try {
      String locale = eventsRequestParams.getLocale();
      if (StringUtils.isBlank(eventsRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
        return;
      }

      EventListModel model =
          eventService.getAllEvents(eventsRequestParams.getLocale(), eventsRequestParams.isAllExpired());
      CalendarResponse calendarRes = calendarAppService.getCalendarResponse(model, locale);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(calendarRes));

    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.EVENT_DETAIL_RES_TYPE,
          e.getLocalizedMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(), "Undefined locale");
    }
  }

}
