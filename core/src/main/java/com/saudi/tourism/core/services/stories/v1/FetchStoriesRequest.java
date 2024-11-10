package com.saudi.tourism.core.services.stories.v1;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to filter stories CF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchStoriesRequest {
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
   * Properties used to calculate image size & crops.
   */
  private String imagesSize = "small";

  /**
   * destinations.
   */
  private List<String> destinations;

  /**
   * categories.
   */
  private List<String> categories;
}
