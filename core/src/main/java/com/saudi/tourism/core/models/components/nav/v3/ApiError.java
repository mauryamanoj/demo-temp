package com.saudi.tourism.core.models.components.nav.v3;

import lombok.Value;

/** Messages to show when SSID API returns an error. */
@Value
class ApiError {
  /**
   * Message to show when SSID API returns an error.
   */
  private String message = "Oops, something went wrong.";

  /**
   * CTA of the button to show when SSID API returns an error.
   */
  private String ctaText = "Try again";
}
