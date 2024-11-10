package com.saudi.tourism.core.servlets.geo.v1;

import com.saudi.tourism.core.services.geo.v1.CountriesService;
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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.saudi.tourism.core.utils.Constants.PN_LOCALE;

/** Servlet exposing eVisa config. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Countries Config Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/geo/countries"
    })
@Slf4j
public class CountriesServlet extends SlingAllMethodsServlet {

  /** Countries OSGI service. */
  @Reference
  private transient CountriesService countriesService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {

      if (request.getRequestParameter(PN_LOCALE) == null) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      final String locale = request.getRequestParameter(PN_LOCALE).getString();

      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper()
              .writeValueAsString(countriesService.fetchListOfCountries(locale)));
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", "Countries Config", e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
