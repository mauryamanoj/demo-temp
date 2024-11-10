package com.saudi.tourism.core.servlets.attractions.v1;

import com.drew.lang.annotations.NotNull;
import com.saudi.tourism.core.services.attractions.v1.AttractionsCFService;
import com.saudi.tourism.core.services.attractions.v1.FetchAttractionsRequest;

import com.saudi.tourism.core.services.attractions.v1.FetchAttractionsResponse;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.RestHelper;
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

import static com.saudi.tourism.core.servlets.attractions.v1.AttractionCFServlet.SERVLET_PATH;

/** Servlet to fetch Attractions. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Attractions Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class AttractionCFServlet extends SlingAllMethodsServlet {

  /** Possible paths for this servlet. */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/attractions";

  /** The Attractions service. */
  @Reference private transient AttractionsCFService attractionsService;

  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var fetchAttractionsRequest =
        new Convert<>(request, FetchAttractionsRequest.class).getRequestData();

    try {
      LOGGER.info("User session used:: {}",request.getResourceResolver().getUserID());

      if (StringUtils.isBlank(fetchAttractionsRequest.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }
      final FetchAttractionsResponse attractionResponse =
          attractionsService.getFilteredAttractions(fetchAttractionsRequest);

      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(attractionResponse));

    } catch (Exception e) {
      LOGGER.error("Error fetching attractions from CF", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
