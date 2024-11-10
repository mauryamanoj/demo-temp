package com.saudi.tourism.core.services.weather.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Current data.
 */
@Data
public class Current {

  /**
   * Sunrise time in seconds.
   */
  private long sunrise;

  /**
   * Sunset time in seconds.
   */
  private long sunset;

  /**
   * Temperature.
   */
  private float temp;

  /**
   * Feels-like temperature.
   */
  @JsonProperty("feels_like")
  private float feelsLike;

  /**
   * Humidity in percents.
   */
  private float humidity;

  /**
   * Wind speed.
   */
  @JsonProperty("wind_speed")
  private float windSpeed;

  /**
   * Weather data.
   */
  private Weather[] weather;

}
