package com.saudi.tourism.core.models.components.nav.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import lombok.Builder;
import lombok.Value;

/**
 * Breadcrumb Item.
 */
@Builder
@Value
public class BreadcrumbItem {
  /**
   * Link of the Breadcrumb item.
   */
  @Expose
  private Link link;

  /**
   * Title of the Breadcrumb item.
   */
  @Expose
  private String title;
}
