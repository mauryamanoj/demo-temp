package com.saudi.tourism.core.services.weather.model.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Simple weather bean / model.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SimpleWeatherModel implements Serializable {

  /**
   * The current weather (localized string).
   */
  @Expose
  private String weather;

  /**
   * The current temperature.
   */
  @Expose
  private Float temp;

  /**
   * City id.
   */
  @Expose
  private String cityId;

  /**
   * The City.
   */
  @Expose
  private String city;

  /**
   * The icon for the current weather.
   */
  @Expose
  private String icon;

  /**
   * OpenWeatherMap service city id.
   */
  @JsonIgnore
  private transient String weatherCityId;

  /**
   * Copy constructor that copies only necessary fields from extended weather.
   *
   * @param extendedWeather extended weather instance to process
   */
  public SimpleWeatherModel(final ExtendedWeatherModel extendedWeather) {
    final Today today = extendedWeather.getToday();
    setWeather(today.getWeather());
    setTemp(today.getTemp());
    setCity(extendedWeather.getCity());
    setIcon(getWeather());
    setWeatherCityId(extendedWeather.getWeatherCityId());
  }

  /**
   * Clone this instance. Using SerializationUtils for creating a deep copy.
   *
   * @return new cloned instance
   */
  @JsonIgnore
  public SimpleWeatherModel getCloned() {
    final SimpleWeatherModel cloned = SerializationUtils.clone(this);
    // Copy transient fields
    cloned.setWeatherCityId(this.getWeatherCityId());
    return cloned;
  }
}
