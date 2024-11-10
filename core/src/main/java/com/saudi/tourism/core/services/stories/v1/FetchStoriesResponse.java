package com.saudi.tourism.core.services.stories.v1;

import java.io.Serializable;
import java.util.List;

import com.saudi.tourism.core.services.filters.v1.FilterModel;
import lombok.Builder;
import lombok.Data;

/** Response to filter Stories CF. */
@Builder
@Data
public class FetchStoriesResponse implements Serializable {
  /** Stories. */
  private List<Story> data;

  /**Pagination.*/
  private Pagination pagination;

  /** The Filters.*/
  private FilterModel filters;
}
