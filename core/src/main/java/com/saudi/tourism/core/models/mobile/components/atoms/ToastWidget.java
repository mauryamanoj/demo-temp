package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Builder;
import lombok.Data;

/**
 * Toast Widget Model.
 */
@Data
@Builder
public class ToastWidget {
  /** type. */
  private String type;

  /** title. */
  private String title;

  /** description. */
  private String description;

  /** icon. */
  private String icon;
}
