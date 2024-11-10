package com.saudi.tourism.core.services.events.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to filter events CF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchEventsRequest {
  /**
   * The language.
   */
  private String locale;

  /**
   * The limit.
   */
  private Integer limit = 0;

  /**
   * The offset.
   */
  private Integer offset = 0;

  /**
   * Start date of the events.
   */
  private String startDate;

  /**
   * End date of the events.
   */
  private String endDate;

  /**
   * Season of the events.
   */
  private String season;

  /**
   * Destination of the events.
   */
  private String destination;

  /**
   * Properties used to sort the returned events.
   */
  private List<String> sortBy;

  /**
   * Properties used to calculate image size & crops.
   */
  private String imagesSize = "small";


  /**
   * Handpicked events paths.
   */
  private List<String> paths;
}
