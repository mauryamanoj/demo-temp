package com.saudi.tourism.core.servlets.thingstodo.v1;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.RestHelper;
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

import static com.saudi.tourism.core.servlets.thingstodo.v1.ThingsToDoCFServlet.SERVLET_PATH;

/** Servlet to fetch events. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Things ToDo Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class ThingsToDoCFServlet extends SlingAllMethodsServlet {

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/things-to-do";


  /**
   * Things ToDo service.
   */
  @Reference
  private ThingsToDoCFService thingsToDoCFService;

  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var fetchThingsToDoRequest =
        new Convert<>(request, FetchThingsToDoRequest.class).getRequestData();

    try {

      if (StringUtils.isBlank(fetchThingsToDoRequest.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      final var model = thingsToDoCFService.getFilteredThingsToDo(fetchThingsToDoRequest);
      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error to fetch Things To Do from CF", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
