package com.saudi.tourism.core.services.weather.model.output;

import lombok.Data;

import java.io.Serializable;

/**
 * Temperature.
 */
@Data
public class Temp implements Serializable {

  /**
   * Day temperature.
   */
  private float day;

  /**
   * Minimum temperature for this day.
   */
  private float min;

  /**
   * Maximum temperature for this day.
   */
  private float max;
}
