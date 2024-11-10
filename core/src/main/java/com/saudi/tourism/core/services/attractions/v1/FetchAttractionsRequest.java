package com.saudi.tourism.core.services.attractions.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to filter attractions CF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchAttractionsRequest {
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
   * Category Tag.
   */
  private String tag;

  /**
   * Destination of the attraction.
   */
  private String destination;

  /**
   * Properties used to sort the returned attraction.
   */
  private List<String> sortBy;


  /**
   * Handpicked attraction paths.
   */
  private List<String> paths;
}
