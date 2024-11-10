package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
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
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.saudi.tourism.core.servlets.EventsServlet.SERVLET_PATH;

/**
 * The type Get all events for a locale.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Events Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + SERVLET_PATH
})
@Slf4j
public class EventsServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/events";
  /**
   * The Event service.
   */
  @Reference
  private transient EventService eventService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    EventsRequestParams eventsRequestParams =
        new Convert<>(request, EventsRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(eventsRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE));
        return;
      }

      EventListModel model = eventService.getFilteredEvents(eventsRequestParams);
      if (null != model && StringUtils.isNotBlank(eventsRequestParams.getSortCity())) {
        model.setData(sortByCity(model.getData(), eventsRequestParams.getSortCity()));
      }
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), model);

    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.EVENT_DETAIL_RES_TYPE,
          e.getLocalizedMessage(), e);
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
              MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

  /**
   * Returns the data sorted by city.
   * @param data data.
   * @param sortCity sortCity.
   * @return sorted data.
   */
  private List<EventDetail> sortByCity(List<EventDetail> data,
      String sortCity) {
    List<EventDetail> cityList = new ArrayList<>();
    List<EventDetail> nonCityList = new ArrayList<>();
    for (EventDetail event : data) {
      if (StringUtils.isNotBlank(event.getCityId())
          && event.getCityId().equals(sortCity)) {
        cityList.add(event);
      } else {
        nonCityList.add(event);
      }
    }
    cityList.addAll(nonCityList);
    return cityList;
  }

}
