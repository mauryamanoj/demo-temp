package com.saudi.tourism.core.models;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.HyPackageFilterModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Halayalla packages filter list obj.
 */
@Data
public class HyPackageFilters implements Serializable {

  /**
   * filters list.
   */
  @Expose
  private List<HyPackageFilterModel> filters;
}
