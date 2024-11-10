package com.saudi.tourism.core.services.weather.model.output;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Today weather forecast fields.
 */
@Data
@Builder
public class Today implements Serializable {
  /**
   * Sunrise time.
   */
  private String sunrise;

  /**
   * Sunset time.
   */
  private String sunset;

  /**
   * Current temperature.
   */
  private float temp;

  /**
   * Minimum daily temperature.
   */
  private float min;

  /**
   * Maximum daily temperature.
   */
  private float max;

  /**
   * Feels like.
   */
  private float feelsLike;

  /**
   * Humidity.
   */
  private float humidity;

  /**
   * Wind speed.
   */
  private float windSpeed;

  /**
   * Weather.
   */
  private String weather;

  /**
   * Icon.
   */
  private String icon;

  /**
   * Hourly forecast.
   */
  private List<Hourly> hourly;

}
