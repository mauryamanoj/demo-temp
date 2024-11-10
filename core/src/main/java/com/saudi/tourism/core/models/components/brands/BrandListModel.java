package com.saudi.tourism.core.models.components.brands;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Brand list model.
 */
@Data
public class BrandListModel implements Serializable {

  /**
   * The Brands.
   */
  @Expose
  private List<BrandDetail> data;

  /**
   * The Filters.
   */
  @Expose
  private BrandFilterModel filters;

  /**
   * The pagination.
   */
  @Expose
  private Pagination pagination;

}
