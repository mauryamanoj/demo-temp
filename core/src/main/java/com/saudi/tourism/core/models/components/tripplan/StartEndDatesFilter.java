package com.saudi.tourism.core.models.components.tripplan;

import lombok.Data;

import java.util.Calendar;

/**
 * Start and end dates request parameters for using in converts.
 */
@Data
public class StartEndDatesFilter {

  /**
   * The current locale for filtering.
   */
  private String locale;

  /**
   * Start date.
   */
  private Calendar startDate;

  /**
   * End date.
   */
  private Calendar endDate;

}
