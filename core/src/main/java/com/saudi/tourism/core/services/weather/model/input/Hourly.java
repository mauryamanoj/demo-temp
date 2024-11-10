package com.saudi.tourism.core.services.weather.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Hourly forecast data.
 */
@Data
public class Hourly {

  /**
   * Date.
   */
  @JsonProperty("dt")
  private long date;

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
   * Weather data.
   */
  private Weather[] weather;
}
