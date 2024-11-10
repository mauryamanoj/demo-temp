package com.saudi.tourism.core.models.components.tripplan.v1;

import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Class handles all filters for Trip Plans.
 */
@Data
@NoArgsConstructor
public class TripPlanFilter implements Serializable {

  /**
   * All possible cities (ids).
   */
  private List<String> cityIds = new LinkedList<>();

  /**
   * All possible cities.
   */
  private List<RegionCityExtended> cities = new LinkedList<>();

  /**
   * Weather array for cities.
   */
  private List<SimpleWeatherModel> weather;

  /**
   * Copy constructor for cloning this instance.
   *
   * @param source source filter to clone
   */
  public TripPlanFilter(final TripPlanFilter source) {
    final List<String> sourceCityIds = source.getCityIds();
    if (CollectionUtils.isNotEmpty(sourceCityIds)) {
      cityIds.addAll(sourceCityIds);
      // Cities and weather fields will be updated after trip plan filter request
    }
  }
}
