package com.saudi.tourism.core.models.components.cards;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Packages list model.
 */
@Data
public class CardsListModel implements Serializable {

  /**
   * The Events.
   */
  @Expose
  private List<CardDetail> data;

  /**
   * The Filters.
   */
  @Expose
  private CardsFilterModel filters;
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
