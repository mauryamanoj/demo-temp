package com.saudi.tourism.core.services.mobile.v1.calendar;

import java.util.Calendar;
import java.util.List;

/**
 * This service defines all methods for calendar apis.
 */
public interface CalendarService {
  /**
   * Get Calendar Data.
   *
   * @param request request params
   * @return calendar
   */
  CalendarModel getCalendar(FetchCalendarRequest request);


  /**
   * Get Calendar Bottom Cards Data.
   *
   * @param request
   * @return calendar bottom cards
   */
  List<CalendarBottomCards> getCalendarBottomCards(FetchCalendarRequest request);

  /**
   * Filter calendar items by startDate and endDate.
   *
   * @param request
   * @param startDate
   * @param endDate
   * @return boolean
   */
  Boolean isBetweenStartEndDates(FetchCalendarRequest request, Calendar startDate, Calendar endDate);
}
