package com.saudi.tourism.core.services.weather.model;

import com.drew.lang.annotations.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Weather request attributes.
 */
@Data
@NoArgsConstructor
public class WeatherRequestParams {

  /**
   * The current language.
   */
  private String locale;

  /**
   * The list of city ids for the weather request.
   */
  private List<String> city;

  /**
   * Units of measure.
   */
  private String units;

  /**
   * Latitude coordinate (for the request by coordinates).
   */
  private String latitude;

  /**
   * Longitude coordinate (for the request by coordinates).
   */
  private String longitude;

  /**
   * Constructor with a list of cities.
   *
   * @param cities city ids
   */
  public WeatherRequestParams(@NotNull final Collection<String> cities) {
    this.city = new LinkedList<>(cities);
  }

  /**
   * Constructor with an array of city ids.
   *
   * @param cities city ids
   */
  public WeatherRequestParams(final String... cities) {
    this.city = Arrays.asList(cities);
  }
}
