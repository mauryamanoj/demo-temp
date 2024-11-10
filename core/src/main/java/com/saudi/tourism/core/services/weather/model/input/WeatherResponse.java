package com.saudi.tourism.core.services.weather.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WeatherAPiModel.
 */
@Data
public class WeatherResponse {

  /**
   * Timezone offset in seconds.
   */
  @JsonProperty("timezone_offset")
  private int timezoneOffset;

  /**
   * Current data.
   */
  private Current current;

  /**
   * Hourly data.
   */
  private Hourly[] hourly;

  /**
   * Daily data.
   */
  private Daily[] daily;

}
