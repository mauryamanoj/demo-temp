package com.saudi.tourism.core.services.stories.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Story pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
  /**
   * The Total.
   */
  private int total;
  /**
   * The offset.
   */
  private int offset;
  /**
   * The limit.
   */
  private int limit;
}

