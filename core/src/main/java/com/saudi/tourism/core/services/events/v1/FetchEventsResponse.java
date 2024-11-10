package com.saudi.tourism.core.services.events.v1;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** Reponse to filter events CF. */
@Builder
@Data
public class FetchEventsResponse implements Serializable {
  /** Events. */
  private List<Event> data;

  /**Pagination.*/
  private Pagination pagination;
}
