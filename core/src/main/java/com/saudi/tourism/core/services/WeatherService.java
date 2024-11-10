package com.saudi.tourism.core.services;

import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;

import java.util.List;

/**
 * This contains all methods to get weather data.
 */
public interface WeatherService {

  /**
   * Get simple weather data from API service for one city, using OpenWeatherMap one call API,
   * answers current and daily min and max temperature.
   *
   * @param weatherRequestParams weather request parameters
   * @return SimpleWeatherModel weather data from api
   */
  SimpleWeatherMinMax getSimpleWeatherSingleCity(WeatherRequestParams weatherRequestParams);

  /**
   * Get simple weather data for a group of cities, using OpenWeatherMap simple weather group
   * call API, answers only the current temperature, without minimum and maximum for this day.
   *
   * @param weatherRequestParams weather request parameters
   * @return SimpleWeatherModel weather data from api
   */
  List<SimpleWeatherModel> getSimpleWeatherGroupOfCities(WeatherRequestParams weatherRequestParams);

  /**
   * Get extended weather data from OpenWeatherMap API service.
   *
   * @param weatherRequestParams request parameters
   * @return Object weather data from api
   */
  ExtendedWeatherModel getExtendedWeather(WeatherRequestParams weatherRequestParams);
}
