package com.saudi.tourism.core.models.components.events.eventscards.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;

/** Events Cards Load Model. */
@Builder
@Getter
public class EventsCardsLoadMoreModel {
  /**
   * Load More Label.
   */
  @Expose
  private String loadMoreLabel;
}
