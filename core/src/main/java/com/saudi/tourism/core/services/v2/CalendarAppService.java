package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.beans.nativeapp.CalendarResponse;
import com.saudi.tourism.core.beans.nativeapp.EventsDataObj;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;

import java.util.List;

/**
 * Service Interface for CalenderApp.
 */
public interface CalendarAppService {
  /**
   *
   * @param model Events Model.
   * @param locale  Locale.
   * @return List of upcoming Event's data.
   */
  List<EventsDataObj> getUpcomingEvents(EventListModel model, String locale);

  /**
   *
   * @param model Events Model.
   * @param locale  Locale.
   * @return List of Current week event's data.
   */
  List<EventsDataObj> getWeeklyEvents(EventListModel model, String locale);

  /**
   *
   * @param model Events Model.
   * @param locale  Locale.
   * @return list of current month event's data
   */
  List<EventsDataObj> getMonthlyEvents(EventListModel model, String locale);

  /**
   *
   * @param event Events Model.
   * @return events object.
   */
  EventsDataObj getEventObjData(EventDetail event);

  /**
   *
   * @param model Events Model.
   * @param filterStartDate filter start date
   * @param filterEndDate filter end date
   * @param upComingMonthEvents list of upcoming months events
   */
  void getEventsData(EventListModel model, String filterStartDate,
                     String filterEndDate, List<EventsDataObj> upComingMonthEvents);

  /**
   *
   * @param model Events Model.
   * @param locale  Locale.
   * @return final calendar app api response
   */
  CalendarResponse getCalendarResponse(EventListModel model, String locale);

}
