package com.saudi.tourism.core.services.filters.v1;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class FilterModel.
 */
@Data
public class FilterModel implements Serializable {

  /**
   * The categories.
   */
  private FiltersItemFilterModel categories;

  /**
   * The locations.
   */
  private FiltersItemFilterModel destinations;

  /**
   * The seasons.
   */
  private FiltersItemFilterModel seasons;

  /**
   * The date.
   */
  private FiltersItemFilterModel date;
}
