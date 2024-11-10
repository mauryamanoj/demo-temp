package com.saudi.tourism.core.models.components.brands;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The Class FilterModel.
 */
@Data
public class BrandFilterModel implements Serializable {

  /**
   * The cities.
   */
  @Expose
  private List<AppFilterItem> brandCity;
  /**
   * The categories.
   */
  @Expose
  private List<AppFilterItem> brandCategory;

}
