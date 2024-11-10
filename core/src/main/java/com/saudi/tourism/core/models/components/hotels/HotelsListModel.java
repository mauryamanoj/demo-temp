package com.saudi.tourism.core.models.components.hotels;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Packages list model.
 */
@Data
public class HotelsListModel implements Serializable {

  /**
   * The Events.
   */
  @Expose
  private List<HotelDetail> data;

  /**
   * The Filters.
   */
  @Expose
  private HotelsFilterModel filters;
  /**
   * The pagination.
   */
  @Expose
  private Pagination pagination;

  /**
   * The hide filters.
   */
  @Expose
  private boolean hideFilters;

}
