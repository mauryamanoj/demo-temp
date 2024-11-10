package com.saudi.tourism.core.models.components.nav.v3;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class Avatar {
  /**
   * URL to the desktop avatar image.
   *
   * @return String URL to the desktop avatar image.
   */
  @Expose
  private String desktop = "media/account-page/avatar-36x36.png";

  /**
   * URL to the mobile avatar image.
   *
   * @return String URL to the mobile avatar image.
   */
  @Expose
  private String mobile = "media/account-page/avatar-60x60.png";
}
