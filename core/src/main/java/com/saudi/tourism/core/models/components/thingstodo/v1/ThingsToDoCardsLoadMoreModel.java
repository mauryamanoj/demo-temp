package com.saudi.tourism.core.models.components.thingstodo.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;

/** Things ToDo Cards Load Model. */
@Builder
@Getter
public class ThingsToDoCardsLoadMoreModel {
  /**
   * Load More Label.
   */
  @Expose
  private String loadMoreLabel;
}
