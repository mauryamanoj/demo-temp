package com.saudi.tourism.core.services.weather.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Daily forecast data.
 */
@Data
public class Daily {

  /**
   * Date.
   */
  @JsonProperty("dt")
  private long date;

  /**
   * Weather data.
   */
  private Weather[] weather;

  /**
   * Temperature data.
   */
  private Temp temp;

  /**
   * Temperature model.
   */
  @Data
  public class Temp {

    /**
     * Noon temperature.
     */
    private float day;

    /**
     * Forecasted minimum temperature during the day.
     */
    private float min;

    /**
     * Forecasted maximum temperature during the day.
     */
    private float max;
  }
}
