package com.saudi.tourism.core.services.weather.model.output;

import lombok.Getter;
import lombok.Setter;

/**
 * Simple weather with adding minimal and maximal daily temperature values.
 */
@Getter
@Setter
public class SimpleWeatherMinMax extends SimpleWeatherModel {

  /**
   * This day minimal temperature (from Open Weather Map one call api request).
   */
  private float tempMin;

  /**
   * This day maximal temperature (from Open Weather Map one call api request).
   */
  private float tempMax;

  /**
   * Copy constructor that copies only necessary fields from the extended weather.
   *
   * @param extendedWeather extended weather instance to process
   */
  public SimpleWeatherMinMax(final ExtendedWeatherModel extendedWeather) {
    final Today today = extendedWeather.getToday();
    setWeather(today.getWeather());
    setTemp(formatAndParseFloat(today.getTemp()));
    setCityId(extendedWeather.getCityId());
    setCity(extendedWeather.getCity());
    setIcon(getWeather());
    setTempMin(formatAndParseFloat(today.getMin()));
    setTempMax(formatAndParseFloat(today.getMax()));
    setWeatherCityId(extendedWeather.getWeatherCityId());
  }

  private float formatAndParseFloat(float value) {
    String formattedValue = String.format("%.1f", value).replace(',', '.');
    return Float.parseFloat(formattedValue);
  }
}
