package com.saudi.tourism.core.models.components.helpwidget.v1;

import lombok.Builder;
import lombok.Getter;

/**
 * Help Widget Model.
 */
@Builder
@Getter
public class HelpWidgetCFModel {

  /**
   * helpWidgetHeading.
   */
  @Getter
  private String helpWidgetHeading;

  /**
   * helpWidgetDescription.
   */
  @Getter
  private String helpWidgetDescription;

  /**
   * helpWidgetCTALabel.
   */
  @Getter
  private String helpWidgetCTALabel;

  /**
   * helpWidgetCTALink.
   */
  @Getter
  private String helpWidgetCTALink;

}
