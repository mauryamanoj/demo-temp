package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class SubMenuCta {
  /**
   * CTA icon.
   */
  @Expose
  private String icon;

  /**
   * CTA copy.
   */
  @Expose
  private String copy;

  /**
   * CTA url.
   */
  @Expose
  private String url;
}
