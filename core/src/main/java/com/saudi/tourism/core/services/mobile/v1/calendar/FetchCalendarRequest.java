package com.saudi.tourism.core.services.mobile.v1.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fetch Calendar API request params. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchCalendarRequest {
  /** The language. */
  private String locale;

  /** The limit. */
  private Integer limit = 0;

  /** The offset. */
  private Integer offset = 0;

  /** Start date of the calendar. */
  private String startDate;

  /** End date of the calendar. */
  private String endDate;
}
