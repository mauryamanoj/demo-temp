package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class SubMenu {
  /**
   * Submenu CTA.
   */
  @Expose
  private SubMenuCta cta;
}
