package com.saudi.tourism.core.services.weather.model.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Model for new weather widget.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedWeatherModel implements Serializable {

  /**
   * City id.
   */
  private String cityId;

  /**
   * City.
   */
  private String city;

  /**
   * Weather forecast for today.
   */
  private Today today;

  /**
   * Daily forecast.
   */
  private List<Daily> dailyList;

  /**
   * OpenWeatherMap service city id.
   */
  @JsonIgnore
  private transient String weatherCityId;

  /**
   * Clone method for this class.
   *
   * @return new cloned instance
   */
  @JsonIgnore
  public ExtendedWeatherModel getCloned() {
    final ExtendedWeatherModel cloned = SerializationUtils.clone(this);
    // Copy transient fields
    cloned.setWeatherCityId(this.getWeatherCityId());
    return cloned;
  }
}
