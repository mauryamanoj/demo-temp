package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.WeatherConstants;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.saudi.tourism.core.servlets.WeatherServlet.DESCRIPTION;

/**
 * This is used to get all weather data via an external site ( http://api.openweathermap.org/ )
 * Sample URL : http://localhost:4502/bin/api/v1/weather?city=london& .
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + WeatherServlet.SIMPLE_WEATHER_API,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + WeatherServlet.EXTENDED_WEATHER_API})
@Slf4j
public class WeatherServlet extends BaseAllMethodsServlet {

  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Weather API (Servlet)";

  /**
   * URL path for simple weather request.
   */
  public static final String SIMPLE_WEATHER_API = "/bin/api/v1/weather";

  /**
   * URL path for extended weather request.
   */
  public static final String EXTENDED_WEATHER_API = "/bin/api/v1/weather-extended";

  /**
   * The Weather Data service.
   */
  @Reference
  private transient WeatherService weatherService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    try {
      final Convert<WeatherRequestParams> parameters =
          new Convert<>(request, WeatherRequestParams.class);
      final WeatherRequestParams weatherRequestParams = parameters.getRequestData();

      // Check necessary parameters
      final List<String> cities = weatherRequestParams.getCity();
      // noCitiesRequest - means city ids weren't provided - workaround for latitude / longitude
      // requests. Don't know if it's used somewhere, left here just for the backwards compatibility
      // When no city id parameters were received, latitude / longitude request parameters will be
      // used to produce the response.
      final boolean noCitiesRequest = CollectionUtils.isEmpty(cities);
      if (noCitiesRequest && StringUtils
          .isAnyBlank(weatherRequestParams.getLatitude(), weatherRequestParams.getLongitude())) {
        throw new IllegalArgumentException(
            "Neither city nor coordinates were provided for the weather request");
      }

      final RequestPathInfo requestPathInfo = request.getRequestPathInfo();
      // Check if the request was for simple weather response
      if (SIMPLE_WEATHER_API.equals(requestPathInfo.getResourcePath())) {
        if (noCitiesRequest || cities.size() == 1) {
          // Simple weather request for one city
          final SimpleWeatherMinMax oneCallSimpleWeatherResponse =
              weatherService.getSimpleWeatherSingleCity(weatherRequestParams);
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(oneCallSimpleWeatherResponse));

        } else {
          // Simple weather request for a list of cities
          final List<SimpleWeatherModel> groupCallSimpleWeather =
              weatherService.getSimpleWeatherGroupOfCities(weatherRequestParams);
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(groupCallSimpleWeather));
        }

        return;
      }

      // Extended weather request - only for one city
      final ExtendedWeatherModel result;
      final String type =
          Optional.ofNullable(requestPathInfo.getExtension()).orElse(WeatherConstants.CURRENT);
      switch (type) {
        case WeatherConstants.HISTORY:
          // History is not implemented - answer error
          throw new IllegalStateException("History weather request hasn't been implemented yet.");

        case WeatherConstants.CURRENT:
        default:
          result = weatherService.getExtendedWeather(weatherRequestParams);
      }

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), result);

    } catch (Exception e) {
      outError(request, response, StatusEnum.INTERNAL_SERVER_ERROR, e, MESSAGE_ERROR_IN,
          DESCRIPTION);
    }
  }
}
