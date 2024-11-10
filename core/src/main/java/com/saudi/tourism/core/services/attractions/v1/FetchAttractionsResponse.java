package com.saudi.tourism.core.services.attractions.v1;

import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.services.destinations.v1.Destination;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/** Response to filter Attractions CF. */
@Builder
@Data
public class FetchAttractionsResponse implements Serializable {
  /** Attractions. */
  private List<Attraction> data;
  /**
   * destination List.
   */
  private List<Destination> destinationList;

  /**
   * category Tag.
   */
  private List<Category> categoryList;

  /**Pagination.*/
  private Pagination pagination;
}
