package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * The type Packages list model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackagesListModel implements Serializable {

  /**
   * The Events.
   */
  @Expose
  private List<PackageDetail> data;

  /**
   * The Filters.
   */
  @Expose
  private PackageFilterModel filters;
  /**
   * The pagination.
   */
  @Expose
  private Pagination pagination;

}
