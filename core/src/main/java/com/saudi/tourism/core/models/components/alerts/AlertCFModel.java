package com.saudi.tourism.core.models.components.alerts;

import lombok.Builder;
import lombok.Getter;

/**
 * AlertCFModel.
 */
@Builder
@Getter
public class AlertCFModel {

  /**
   * alert Text.
   */
  private String alert;

  /**
   * Alert Color.
   */
  private String alertColor;
}
