package com.saudi.tourism.core.services.mobile.v1.eventitem;

import com.saudi.tourism.core.services.mobile.v1.calendar.CalendarBottomCards;
import com.saudi.tourism.core.services.mobile.v1.calendar.CalendarItem;
import com.saudi.tourism.core.services.mobile.v1.calendar.FetchCalendarRequest;

import java.util.List;

/** All Events Section Items Service. */
public interface EventItemService {
  /**
   * Method to fetch all items of the section 'All Events'.
   *
   * @param locale
   * @return calendar items.
   */
  List<CalendarItem> fetchAllEventItems(String locale);

  /**
   * Method to get events card for calendar.
   *
   * @param request
   * @return event card.
   */
  CalendarBottomCards getEventCard(FetchCalendarRequest request);
}
