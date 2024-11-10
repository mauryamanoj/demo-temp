package com.saudi.tourism.core.beans.nativeapp;

import lombok.Builder;
import lombok.Data;

/**
 * Calendar Filters.
 */
@Data
@Builder
public class CalendarFilters {

  /**
   * Start Date.
   */
  private String startDate;
  /**
   * End Date.
   */
  private String endDate;

}
