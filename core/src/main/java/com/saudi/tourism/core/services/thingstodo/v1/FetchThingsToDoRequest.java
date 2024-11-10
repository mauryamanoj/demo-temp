package com.saudi.tourism.core.services.thingstodo.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to filter things todo CF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchThingsToDoRequest {

  /**
   * Default limit.
   */
  public static final Integer DEFAULT_LIMIT = 10;

  /**
   * Default offset.
   */
  public static final Integer DEFAULT_OFFSET = 0;

  /**
   * The language.
   */
  private String locale;

  /**
   * The limit.
   */
  private Integer limit = DEFAULT_LIMIT;

  /**
   * The offset.
   */
  private Integer offset = DEFAULT_OFFSET;

  /**
   * Properties used to calculate image size & crops.
   */
  private String imagesSize = "small";

  /**
   * Handpicked events paths.
   */
  private List<String> paths;

  /**
   * destination for HalaYalla.
   */
  private String destination;

  /**
   * Free only.
   */
  private Boolean freeOnly;

  /**
   * CFM type: attraction, activity, tour.
   */
  private List<String> type;

  /**
   * CFM categories.
   */
  private List<String> categories;

  /**
   * CFM POI types.
   */
  private List<String> poiTypes;

  /**
   * CFM Destinations.
   */
  private List<String> destinations;

  /**
   * CFM seasons.
   */
  private List<String> seasons;

  /**
   * Start date.
   */
  private String startDate;

  /**
   * End date.
   */
  private String endDate;

  /**
   * Properties used to sort.
   */
  private List<String> sortBy;

  /**
   * The keyword.
   */
  private String keyword;
}
