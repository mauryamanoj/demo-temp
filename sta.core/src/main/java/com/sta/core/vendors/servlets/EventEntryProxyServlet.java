package com.sta.core.vendors.servlets;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.sta.core.vendors.service.EventEntryProxyService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Event entry processing proxy servlet.
 * This servlet should act like a preprocessing proxy and pass requests to author.
 */
@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL
                   + EventEntryProxyServlet.DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_PUT,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_DELETE,
               SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/entry/event"})
@Slf4j
public class EventEntryProxyServlet extends BaseAllMethodsServlet {

  /**
   * Service description.
   */
  static final String DESCRIPTION = "Event entry proxy servlet";

  /**
   * Event proxy service.
   */
  @Reference
  private EventEntryProxyService proxyService;

  /**
   * Save event entry.
   * @param request request
   * @param response response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doPut(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      ResponseMessage responseMessage = proxyService.createUpdateEvent(request);
      CommonUtils.writeJSON(response, responseMessage.getStatusCode(),
          responseMessage.getMessage());
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Get event entry.
   * @param request request
   * @param response response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doPost(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      ResponseMessage responseMessage = proxyService.getEvent(request);
      CommonUtils
          .writeJSON(response, responseMessage.getStatusCode(), responseMessage.getMessage());
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

  /**
   * Delete event entry.
   * @param request request.
   * @param response response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doDelete(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      ResponseMessage responseMessage = proxyService.deleteEvent(request);
      CommonUtils
          .writeJSON(response, responseMessage.getStatusCode(), responseMessage.getMessage());
    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }

}
