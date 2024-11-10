package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanFilter;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SSIDTripPlanService;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.WeatherConstants;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static com.saudi.tourism.core.servlets.nativeapp.SSIDTripPlanFilterServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.nativeapp.SSIDTripPlanFilterServlet.SERVLET_PATH;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This servlet returns all possible filters for Trip Plans.
 */
@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
               SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH})
@Slf4j
public class SSIDTripPlanFilterServlet extends BaseAllMethodsServlet {

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "Trip Plan Filters API v3 (Servlet)";

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v3/trip-plan-filter";

  /**
   * The Event service.
   */
  @Reference
  private transient SSIDTripPlanService tripPlanService;

  /**
   * Cities service to get all the info about filter cities.
   */
  @Reference
  private transient RegionCityService citiesService;

  /**
   * Weather service to request cities' weather.
   */
  @Reference
  private transient WeatherService weatherService;

  /**
   * SlingSettings service to get run modes.
   */
  @Reference
  private transient SlingSettingsService settingsService;

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request,
      @NotNull SlingHttpServletResponse response) throws IOException {

    try {
      checkNecessaryParameters(request, response, Constants.PN_LOCALE);

      final String locale = request.getParameter(Constants.PN_LOCALE);
      if (StringUtils.isBlank(locale)) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      final TripPlanFilter filters = tripPlanService.getTripPlanFilter(locale);
      final List<RegionCityExtended> filterCities = filters.getCities();
      filterCities.clear();

      final List<String> filterCityIds = filters.getCityIds();
      if (CollectionUtils.isNotEmpty(filterCityIds)) {
        // Update extended cities data (it's not stored in cache)
        filterCityIds.stream()
            .map(filterCityId -> citiesService.getRegionCityExtById(filterCityId, locale))
            .map(extCity -> {
              DynamicMediaUtils
                  .prepareDMImages(extCity.getImage(), TripPlanConstants.CROP_CITY_CARD_DSK,
                      TripPlanConstants.CROP_CITY_CARD_MOB,
                      DynamicMediaUtils.isCnServer(settingsService));
              return extCity;
            }).forEach(filterCities::add);

        // Update weather data for those cities if the param weather=true was received
        if (StringUtils.equals(Constants.STR_TRUE, request.getParameter(Constants.CONST_WEATHER))) {
          final WeatherRequestParams weatherRequestParams = new WeatherRequestParams(filterCityIds);
          weatherRequestParams.setUnits(request.getParameter(WeatherConstants.PARAM_UNITS));
          weatherRequestParams.setLocale(locale);
          filters.setWeather(weatherService.getSimpleWeatherGroupOfCities(weatherRequestParams));
        }
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(filters));
      } else {
        // Answer error if cities list is empty (request from FE)
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            MessageFormat.format("Could not find cities for trip planner for language {0}", locale));
      }
    } catch (Exception e) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageFormat.format(MESSAGE_ERROR_IN, DESCRIPTION));
    }
  }
}
