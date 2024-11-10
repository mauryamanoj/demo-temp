package com.saudi.tourism.core.services.thingstodo.v1;

import java.io.Serializable;
import java.util.List;

import com.saudi.tourism.core.services.events.v1.Pagination;
import com.saudi.tourism.core.services.filters.v1.FilterModel;
import lombok.Builder;
import lombok.Data;

/** Reponse to filter things todo CF. */
@Builder
@Data
public class FetchThingsToDoResponse implements Serializable {
  /** Events. */
  private List<ThingToDoModel> data;

  /** The Filters.*/
  private FilterModel filters;

  /**Pagination.*/
  private Pagination pagination;
}
