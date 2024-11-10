package com.saudi.tourism.core.models.components.events;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Event list model.
 */
@Data
public class EventListModel implements Serializable {

  /**
   * The Events.
   */
  private List<EventDetail> data;

  /**
   * The Filters.
   */
  private EventFilterModel filters;
  /**
   * The pagination.
   */
  private Pagination pagination;

}
