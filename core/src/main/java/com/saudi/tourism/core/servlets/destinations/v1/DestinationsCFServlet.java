package com.saudi.tourism.core.servlets.destinations.v1;

import com.saudi.tourism.core.services.destinations.v1.DestinationsCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;

import static com.saudi.tourism.core.servlets.destinations.v1.DestinationsCFServlet.SERVLET_PATH;
import static com.saudi.tourism.core.utils.PrimConstants.LOCALE;

/** Servlet to fetch Destinations. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Destinations Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class DestinationsCFServlet extends SlingAllMethodsServlet {

  /** Possible paths for this servlet. */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/destinations";

  /** The Destinations service. */
  @Reference private transient DestinationsCFService destinationsService;

  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      if (request.getLocale() == null) {
        return;
      }
      String locale = request.getParameter(LOCALE);
      if (locale == null) {
        locale = request.getLocale().toString();
      }
      final var model = destinationsService.returnAllDestination(locale);

      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error to fetch destination from CF", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
