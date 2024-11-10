package com.saudi.tourism.core.models.components.mapwidget.v1;

import lombok.Builder;
import lombok.Getter;

/**
 * Map Widget Model.
 */
@Builder
@Getter
public class MapWidgetCFModel {

  /**
   * title.
   */
  @Getter
  private String title;

  /**
   * locationLabel.
   */
  @Getter
  private String locationLabel;

  /**
   * locationValue.
   */
  @Getter
  private String locationValue;

  /**
   * Latitude.
   */
  @Getter
  private String latitude;

  /**
   * Longitude.
   */
  @Getter
  private String longitude;

  /**
   * CTA Label.
   */
  @Getter
  private String ctaLabel;

}
