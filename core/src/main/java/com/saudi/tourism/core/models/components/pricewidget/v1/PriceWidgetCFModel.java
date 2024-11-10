package com.saudi.tourism.core.models.components.pricewidget.v1;

import lombok.Builder;
import lombok.Getter;

/**
 * Price Widget Model.
 */
@Builder
@Getter
public class PriceWidgetCFModel {

  /**
   * Ticket Type.
   */
  @Getter
  private String ticketType;

  /**
   * Ticket Price.
   */
  @Getter
  private String ticketPrice;

  /**
   * Ticket Price Suffix.
   */
  @Getter
  private String ticketPriceSuffix;

  /**
   * Ticket Details.
   */
  @Getter
  private String ticketDetails;

  /**
   * Ticket CTA Label.
   */
  @Getter
  private String ticketCTALabel;

  /**
   * Ticket CTA Link.
   */
  @Getter
  private String ticketCTALink;

}
