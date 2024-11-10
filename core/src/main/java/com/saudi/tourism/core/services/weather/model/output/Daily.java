package com.saudi.tourism.core.services.weather.model.output;

import lombok.Data;

import java.io.Serializable;

/**
 * Daily forecast.
 */
@Data
public class Daily implements Serializable {

  /**
   * Date.
   */
  private String date;

  /**
   * Temperature.
   */
  private Temp temp;

  /**
   * Weather.
   */
  private String weather;

  /**
   * Icon.
   */
  private String icon;

}
