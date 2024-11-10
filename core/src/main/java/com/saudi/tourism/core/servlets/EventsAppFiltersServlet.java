package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ObjectResponse;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.services.EventService;
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
 * The type Get all locale app events filters.
 */
@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Event App Filters Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/catalog"})
@Slf4j
public class EventsAppFiltersServlet extends SlingAllMethodsServlet {
  /**
   * The Event service.
   */
  @Reference
  private transient EventService eventService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {

      String locale = request.getParameter(Constants.LOCALE);
      if (StringUtils.isBlank(locale)) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE));
        return;
      }
      EventAppFilterModel filters = eventService.getEventAppFilters(locale);
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ObjectResponse<>(MessageType.SUCCESS.getType(), filters));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: " + Constants.EVENT_DETAIL_RES_TYPE, e);
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }
}
