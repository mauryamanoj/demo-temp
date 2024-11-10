package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.WeatherConstants;
import com.saudi.tourism.core.services.weather.model.WeatherModelConverter;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.input.WeatherResponse;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Service that acts like a proxy to openweathermap's daily forecast service.
 */
@Slf4j
@Component(service = WeatherService.class,
           immediate = true)
@SuppressWarnings("java:S1192")
public class WeatherServiceImpl implements WeatherService {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * Cities service.
   */
  @Reference
  private RegionCityService cityService;

  /**
   * InMemory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * I18n Resource Bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Override
  public SimpleWeatherMinMax getSimpleWeatherSingleCity(
      @NotNull final WeatherRequestParams weatherRequestParams) {
    final RegionCityExtended cityExtended = getSingleCityFromWeatherRequest(weatherRequestParams);
    final String cityId = cityExtended.getId();
    final String latitude = cityExtended.getLatitude();
    final String longitude = cityExtended.getLongitude();
    final String units = getMeasurementUnit(weatherRequestParams.getUnits());
    final String locale =
        StringUtils.defaultIfBlank(weatherRequestParams.getLocale(), Constants.DEFAULT_LOCALE);

    final String localizedCityName = cityExtended.getName();

    // Check cached extended (one call) result for this city
    // Cache key format: `weather:extended-metric@24.687731,46.721851`
    final String oneCallWeatherCacheKey = StringUtils
        .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_EXTENDED, Constants.DASH,
            units, "@", latitude, Constants.COMMA, longitude);
    final ExtendedWeatherModel cachedOneCallWeather =
        (ExtendedWeatherModel) memCache.get(oneCallWeatherCacheKey);
    if (cachedOneCallWeather != null) {
      LOGGER.trace("Found extended weather in cache, key: {}", oneCallWeatherCacheKey);
      return (SimpleWeatherMinMax) localizeData(new SimpleWeatherMinMax(cachedOneCallWeather),
          localizedCityName, locale);
    }

    // Check cached simple weather result for this city
    // Cache key format: `weather:simple-metric@24.687731,46.721851`
    final String simpleWeatherCacheKey = StringUtils
        .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_SIMPLE, Constants.DASH,
            units, "@", latitude, Constants.COMMA, longitude);
    final SimpleWeatherModel cachedSimpleWeather =
        (SimpleWeatherModel) memCache.get(simpleWeatherCacheKey);
    if (cachedSimpleWeather instanceof SimpleWeatherMinMax) {
      // Found cached - just return it
      LOGGER.trace("Found simple weather in cache, key: {}", simpleWeatherCacheKey);
      return (SimpleWeatherMinMax) localizeData(cachedSimpleWeather, localizedCityName, locale);
    }

    // There is no cached object or the cached object doesn't contain min / max temperature -
    // execute new one call API request for this city
    final ExtendedWeatherModel oneCallResult =
        requestExtendedWeather(latitude, longitude, units, cityId, cityExtended.getWeatherCityId());
    if (oneCallResult == null) {
      throw new IllegalStateException(
          "Could not request weather data. cityId: " + cityId + ", latitude: " + latitude
              + ", longitude: " + longitude);
    }
    // Store into cache
    memCache.add(oneCallWeatherCacheKey, oneCallResult, NumberConstants.ONE_HOUR_IN_MILLISECONDS);
    // Store also into simple weather cache key
    final SimpleWeatherMinMax simpleWeatherResult = new SimpleWeatherMinMax(oneCallResult);
    memCache
        .add(simpleWeatherCacheKey, simpleWeatherResult, NumberConstants.ONE_HOUR_IN_MILLISECONDS);

    return (SimpleWeatherMinMax) localizeData(simpleWeatherResult, localizedCityName, locale);
  }

  @Override
  public List<SimpleWeatherModel> getSimpleWeatherGroupOfCities(
      final WeatherRequestParams weatherRequestParams) {
    // Check request params - if the cities list wasn't provided, try to get by coordinates and
    // return that single city in a list, or produce error
    final List<String> cities = weatherRequestParams.getCity();
    if (CollectionUtils.isEmpty(cities)) {
      final SimpleWeatherMinMax result = getSimpleWeatherSingleCity(weatherRequestParams);
      if (result != null) {
        return Collections.singletonList(result);
      }
    }

    // List of weather instances that were found in cache and were received using group request
    final List<SimpleWeatherModel> resultList = new LinkedList<>();

    final String locale =
        StringUtils.defaultIfBlank(weatherRequestParams.getLocale(), Constants.DEFAULT_LOCALE);
    final String units = getMeasurementUnit(weatherRequestParams.getUnits());

    // Map: weather city id -> city id
    final Map<String, String> toRequest = new HashMap<>();
    for (String requestedCity : cities) {
      final String cityId = AppUtils.stringToID(requestedCity);
      final RegionCityExtended city = cityService.getRegionCityExtById(cityId, locale);
      if (city == null) {
        LOGGER.error("Couldn't find requested city by id: {}", cityId);
        // Process other cities
        continue;
      }

      final String latitude = city.getLatitude();
      final String longitude = city.getLongitude();
      final String localizedCityName = city.getName();

      // Check cached simple weather result for this city
      // Cache key format: `weather:simple-metric@24.687731,46.721851`
      final String simpleWeatherCacheKey = StringUtils
          .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_SIMPLE, Constants.DASH,
              units, "@", latitude, Constants.COMMA, longitude);
      final SimpleWeatherModel cachedSimpleWeather =
          (SimpleWeatherModel) memCache.get(simpleWeatherCacheKey);

      if (cachedSimpleWeather != null) {
        LOGGER.trace("Found simple weather in cache, key: {}", simpleWeatherCacheKey);
        resultList.add(localizeData(cachedSimpleWeather, localizedCityName, locale));

      } else {
        // Cached simple weather response is not found - check the extended one
        // Cache key format: `weather:extended-metric@24.687731,46.721851`
        final String oneCallWeatherCacheKey = StringUtils
            .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_EXTENDED,
                Constants.DASH, units, "@", latitude, Constants.COMMA, longitude);
        final ExtendedWeatherModel cachedOneCallWeather =
            (ExtendedWeatherModel) memCache.get(oneCallWeatherCacheKey);
        if (cachedOneCallWeather != null) {
          LOGGER.trace("Found extended weather in cache, key: {}", oneCallWeatherCacheKey);
          resultList.add(
              localizeData(new SimpleWeatherModel(cachedOneCallWeather), localizedCityName,
                  locale));

        } else {
          // Couldn't find in cache - it needs to request a new one
          toRequest.put(city.getWeatherCityId(), city.getId());
        }
      }
    }

    // All items were found in cache - just return them
    if (toRequest.isEmpty()) {
      return resultList;
    }

    // Found some cities that need to be requested from OpenWeatherMap
    final List<SimpleWeatherModel> apiResult = requestSimpleWeatherGroup(toRequest.keySet(), units);

    // Update our city id, translation and store objects to cache
    for (SimpleWeatherModel simpleWeather : apiResult) {
      final String cityId = toRequest.get(simpleWeather.getWeatherCityId());
      simpleWeather.setCityId(cityId);

      final RegionCityExtended city = cityService.getRegionCityExtById(cityId, locale);

      final String simpleWeatherCacheKey = StringUtils
          .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_SIMPLE, Constants.DASH,
              units, "@", city.getLatitude(), Constants.COMMA, city.getLongitude());
      memCache.add(simpleWeatherCacheKey, simpleWeather);

      // Localize and add to result
      resultList.add(localizeData(simpleWeather, city.getName(), locale));
    }

    return resultList;
  }

  @Override
  public ExtendedWeatherModel getExtendedWeather(final WeatherRequestParams weatherRequestParams) {
    final RegionCityExtended cityExtended = getSingleCityFromWeatherRequest(weatherRequestParams);
    final String cityId = cityExtended.getId();
    final String latitude = cityExtended.getLatitude();
    final String longitude = cityExtended.getLongitude();
    final String units = getMeasurementUnit(weatherRequestParams.getUnits());
    final String locale =
        StringUtils.defaultIfBlank(weatherRequestParams.getLocale(), Constants.DEFAULT_LOCALE);

    final String localizedCityName = cityExtended.getName();

    // Check cached extended (one call) result for this city
    // Cache key format: `weather:extended-metric@24.687731,46.721851`
    final String oneCallWeatherCacheKey = StringUtils
        .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_EXTENDED, Constants.DASH,
            units, "@", latitude, Constants.COMMA, longitude);
    final ExtendedWeatherModel cachedOneCallWeather =
        (ExtendedWeatherModel) memCache.get(oneCallWeatherCacheKey);
    if (cachedOneCallWeather != null) {
      LOGGER.trace("Found extended weather in cache, key: {}", oneCallWeatherCacheKey);
      return localizeData(cachedOneCallWeather, localizedCityName, locale);
    }

    // Else - request new data and update caches
    final ExtendedWeatherModel oneCallResult =
        requestExtendedWeather(latitude, longitude, units, cityId, cityExtended.getWeatherCityId());
    if (oneCallResult == null) {
      throw new IllegalStateException(
          "Could not request weather data. cityId: " + cityId + ", latitude: " + latitude
              + ", longitude: " + longitude);
    }
    // Store into cache
    memCache.add(oneCallWeatherCacheKey, oneCallResult, NumberConstants.ONE_HOUR_IN_MILLISECONDS);
    // Store also into simple weather cache key
    final String simpleWeatherCacheKey = StringUtils
        .join(WeatherConstants.KEY_PREFIX_WEATHER, WeatherConstants.PREFIX_SIMPLE, Constants.DASH,
            units, "@", latitude, Constants.COMMA, longitude);
    final SimpleWeatherMinMax simpleWeatherResult = new SimpleWeatherMinMax(oneCallResult);
    memCache
        .add(simpleWeatherCacheKey, simpleWeatherResult, NumberConstants.ONE_HOUR_IN_MILLISECONDS);

    return localizeData(oneCallResult, localizedCityName, locale);
  }

  /**
   * Returns only one city that corresponds to the weather request parameters or produces error
   * if parameters match multiple cities.
   *
   * @param weatherRequestParams weather request parameters instance
   * @return found extended city or {@code null} if not found
   */
  private RegionCityExtended getSingleCityFromWeatherRequest(
      final WeatherRequestParams weatherRequestParams) {
    final List<String> cities = weatherRequestParams.getCity();
    final String locale =
        StringUtils.defaultIfBlank(weatherRequestParams.getLocale(), Constants.DEFAULT_LOCALE);

    final RegionCityExtended cityExtended;
    if (CollectionUtils.isEmpty(cities)) {
      // Requested by coordinates
      if (StringUtils
          .isAnyBlank(weatherRequestParams.getLatitude(), weatherRequestParams.getLongitude())) {
        throw new IllegalArgumentException(
            "Neither city nor coordinates were provided for the weather request");
      }

      cityExtended = cityService
          .searchRegionCityByCoords(locale, weatherRequestParams.getLatitude(),
              weatherRequestParams.getLongitude());

    } else {
      cityExtended = cityService.getRegionCityExtById(AppUtils.stringToID(cities.get(0)), locale);
    }

    if (cityExtended == null) {
      final String errorMessage =
          "Couldn't find city for the request " + weatherRequestParams.toString();
      LOGGER.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }

    return cityExtended;
  }

  /**
   * Request extended (one call api) weather for the city.
   *
   * @param latitude      city latitude coordinate
   * @param longitude     city longitude coordinate
   * @param units         units system (metric / imperial / standard)
   * @param cityId        our id of the city
   * @param weatherCityId OpenWeatherMap city id for group requests
   * @return received ExtendedWeatherModel instance
   */
  private ExtendedWeatherModel requestExtendedWeather(final String latitude, final String longitude,
      final String units, final String cityId, final String weatherCityId) {

    final String url = getOpenWeatherOneCallUrl(latitude, longitude, units);

    ResponseMessage openWeatherMapResponse;
    try {
      openWeatherMapResponse = RestHelper.executeMethodGet(url, StringUtils.EMPTY, false);
    } catch (IOException e) {
      LOGGER.error("Weather request to openweathermap.org caused an exception: latitude = {}, "
          + "longitude = {}, units = {}", latitude, longitude, units, e);
      return null;
    }

    if (HttpStatus.SC_OK != openWeatherMapResponse.getStatusCode()) {
      LOGGER.error("Weather request to openweathermap.org was unsuccessful: "
              + "latitude = {}, longitude = {}, units = {}, response = {}", latitude, longitude,
          units,
          openWeatherMapResponse);
      return null;
    }

    final WeatherResponse weatherResponse;
    try {
      weatherResponse = RestHelper.getObjectMapper()
          .readValue(openWeatherMapResponse.getMessage(), WeatherResponse.class);
    } catch (IOException e) {
      final String errorMessage = "Couldn't process openweathermap.org response";
      LOGGER.error(errorMessage, e);
      throw new IllegalStateException(errorMessage, e);
    }
    final ExtendedWeatherModel result =
        Optional.ofNullable(weatherResponse).map(WeatherModelConverter.EXTENDED_WEATHER_CONVERTER)
            .orElseGet(ExtendedWeatherModel::new);
    result.setCityId(cityId);
    result.setWeatherCityId(weatherCityId);

    return result;
  }

  /**
   * This function requests a group of simple weather models from OpenWeatherMap API.
   *
   * @param weatherCityIds ids to request weather
   * @param units          units param
   * @return list of simple weather instances
   */
  @NotNull
  private List<SimpleWeatherModel> requestSimpleWeatherGroup(
      final Collection<String> weatherCityIds, final String units) {
    final String url = getWeatherGroupCallUrl(weatherCityIds, units);

    ResponseMessage openWeatherMapResponse;
    try {
      openWeatherMapResponse = RestHelper.executeMethodGet(url, StringUtils.EMPTY, false);
    } catch (IOException e) {
      LOGGER.error(
          "Weather group request to openweathermap.org caused an exception: id = {}, units = {}",
          weatherCityIds, units, e);
      throw new IllegalStateException("OpenWeatherMap connection error");
    }

    if (HttpStatus.SC_OK != openWeatherMapResponse.getStatusCode()) {
      LOGGER.error(
          "Weather group request to openweathermap.org was unsuccessful: id = {}, units = {}, "
              + "response = {}", weatherCityIds, units, openWeatherMapResponse);
      throw new IllegalArgumentException(
          "Unsuccessful response while requesting group data for ids:" + weatherCityIds);
    }

    return Optional.ofNullable(openWeatherMapResponse.getMessage())
        .map(WeatherModelConverter.GRP_SIMPLE_WEATHER_JSON_CONVERTER)
        .orElse(Collections.emptyList());
  }

  /**
   * Localize API response.
   *
   * @param language          the current locale
   * @param localizedCityName localized city name to update the model
   * @param data              extended weather data
   * @return localized data.
   */
  @Nullable
  private SimpleWeatherModel localizeData(@Nullable final SimpleWeatherModel data,
      @NotNull final String localizedCityName, @NotNull final String language) {
    if (data == null) {
      return null;
    }

    // Clone to avoid mutation of original cached value
    final SimpleWeatherModel result = data.getCloned();
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));

    result.setCity(localizedCityName);
    result.setWeather(CommonUtils.getI18nString(i18n, result.getWeather()));

    return result;
  }

  /**
   * Localize API response.
   *
   * @param data              extended weather data
   * @param localizedCityName localized city name to update
   * @param language          the current locale
   * @return localized data.
   */
  @Nullable
  private ExtendedWeatherModel localizeData(@Nullable final ExtendedWeatherModel data,
      @NotNull final String localizedCityName, @NotNull final String language) {
    if (data == null) {
      return null;
    }

    // Create a deep copy to avoid mutation of original in-cache value.
    final ExtendedWeatherModel result = data.getCloned();
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));

    result.setCity(localizedCityName);
    result.getToday().setWeather(CommonUtils.getI18nString(i18n, result.getToday().getWeather()));
    result.getToday().getHourly()
        .forEach(hourly -> hourly.setWeather(CommonUtils.getI18nString(i18n, hourly.getWeather())));
    result.getDailyList()
        .forEach(daily -> daily.setWeather(CommonUtils.getI18nString(i18n, daily.getWeather())));

    return result;
  }

  /**
   * This method is used to construct the URL for OpenWeatherMap one call API.
   * <p/>
   * The One Call API provides the following weather data for any geographical coordinates:
   * <ul>
   * <li>Current weather</li>
   * <li>Minute forecast for 1 hour</li>
   * <li>Hourly forecast for 48 hours</li>
   * <li>Daily forecast for 7 days</li>
   * <li>Government weather alerts</li>
   * <li>Historical weather data for the previous 5 days</li>
   * </ul>
   *
   * @param latitude  latitude
   * @param longitude longitude
   * @param units     unit for requesting data in proper format (metric / imperial / standard)
   * @return api url
   * @see <a href="https://openweathermap.org/api/one-call-api">One Call API Docs</a>
   */
  String getOpenWeatherOneCallUrl(String latitude, String longitude, final String units) {
    final URIBuilder uriBuilder;
    try {
      uriBuilder = new URIBuilder(saudiTourismConfig.getOneCallWeatherExternalAppUrl());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Server configuration error: URL Malformed", e);
    }

    if (StringUtils.isNotEmpty(latitude)) {
      uriBuilder.addParameter(Constants.CONST_PARAM_LAT, latitude);
    }
    if (StringUtils.isNotEmpty(longitude)) {
      uriBuilder.addParameter(Constants.CONST_PARAM_LON, longitude);
    }

    // For requests always use EN locale
    uriBuilder.addParameter(WeatherConstants.PARAM_LANG, Constants.DEFAULT_LOCALE);

    if (StringUtils.isNotBlank(units)) {
      uriBuilder.addParameter(WeatherConstants.PARAM_UNITS, units);
    }

    return uriBuilder.toString();
  }

  /**
   * This method constructs group call weather request URL.
   *
   * @param weatherCityIds City ID. List of city ID 'city.list.json.gz' can be downloaded here.
   *                       The limit of locations is 20.
   * @param units          Units of measurement. standard, metric and imperial units are
   *                       available. If you do not use the units parameter, standard units will
   *                       be applied by default.
   * @return produced url
   * @see <a href="https://openweathermap.org/current#severalid">Simple weather several ids</a>
   */
  String getWeatherGroupCallUrl(@NotNull final Collection<String> weatherCityIds,
      final String units) {
    if (CollectionUtils.isEmpty(weatherCityIds)) {
      throw new IllegalArgumentException("City codes must be provided");
    }

    final URIBuilder uriBuilder;
    try {
      uriBuilder = new URIBuilder(saudiTourismConfig.getGroupCallWeatherExternalAppUrl());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Server configuration error: URL Malformed", e);
    }

    uriBuilder.addParameter(Constants.PN_ID, StringUtils.join(weatherCityIds, ","));

    // Always request for english language
    uriBuilder.addParameter(WeatherConstants.PARAM_LANG, Constants.DEFAULT_LOCALE);

    if (StringUtils.isNotBlank(units)) {
      uriBuilder.addParameter(WeatherConstants.PARAM_UNITS, units);
    }

    return uriBuilder.toString();
  }

  /**
   * Validate and get measurement unit. If validation fails, metric system is used.
   *
   * @param unit unit as provided within request
   * @return measurement unit
   */
  static String getMeasurementUnit(String unit) {
    final String defaultUnits = WeatherConstants.DEFAULT_UNIT.name().toLowerCase();
    if (StringUtils.isBlank(unit)) {
      return defaultUnits;
    }
    try {
      return WeatherConstants.Unit.valueOf(unit.toUpperCase()).name().toLowerCase();
    } catch (Exception ignored) {
      LOGGER
          .warn("Could not identify measurement unit: {}, the metric will be used instead.", unit);
      return defaultUnits;
    }
  }
}
