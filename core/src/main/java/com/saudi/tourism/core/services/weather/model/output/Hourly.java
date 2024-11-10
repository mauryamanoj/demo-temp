package com.saudi.tourism.core.services.weather.model.output;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Hourly weather forecast.
 */
@Data
@Builder
public class Hourly implements Serializable {

  /**
   * Forecast time.
   */
  private String time;

  /**
   * Forecast temperature.
   */
  private float temp;

  /**
   * Forecast feels-like temperature.
   */
  private float feelsLike;

  /**
   * Weather.
   */
  private String weather;

  /**
   * Icon.
   */
  private String icon;
}
