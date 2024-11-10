package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import lombok.Builder;
import lombok.Data;

/**
 * Common CF model.
 */
@Data
@Builder
public class CommonCFModel {
  /**
   * title.
   */
  private String title;
  /**
   * subtitle.
   */
  private String subtitle;
  /**
   * ticketCTALabel.
   */
  private String ticketCTALabel;
  /**
   * ticketCTALink.
   */
  private String ticketCTALink;
  /**
   * ticketPrice.
   */
  private String ticketPrice;
  /**
   * ticketPriceSuffix.
   */
  private String ticketPriceSuffix;
  /**
   * ticketType.
   */
  private String ticketType;
  /**
   * ticketDetails.
   */
  private String ticketDetails;
  /**
   * DetailsPagePath.
   */
  private String detailsPage;

  /**
   * Destination CF Model.
   */
  private DestinationCFModel destination;
}
