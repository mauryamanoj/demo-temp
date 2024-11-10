package com.saudi.tourism.core.services.mobile.v1.filters;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MobileFilter implements Serializable {
  /**
   * Filter header items.
   */
  private TypeFilterModel header;

  /**
   * The categories.
   */
  private List<FilterCategories> categories;
}
