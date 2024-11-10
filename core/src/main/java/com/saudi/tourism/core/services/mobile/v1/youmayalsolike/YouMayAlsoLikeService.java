package com.saudi.tourism.core.services.mobile.v1.youmayalsolike;

import com.saudi.tourism.core.models.components.events.eventscards.v1.EventsCardsModel;
import com.saudi.tourism.core.models.components.thingstodo.v1.ThingsToDoCardsModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;

public interface YouMayAlsoLikeService {
  /**
   * This method fill the events request objects from events cards model.
   * @param eventsCards eventsCards
   * @return fetchEventRequest
   */
  FetchEventsRequest fillEventsRequestFromEventsCards(EventsCardsModel eventsCards);

  /**
   * This method fill the things-to-do request objects from events cards model.
   * @param eventsCards eventsCards
   * @return fetchThingsToDoRequest
   */
  FetchThingsToDoRequest fillThingsToDoRequestFromEventCards(ThingsToDoCardsModel eventsCards);

  /**
   * This method will get all filtered events.
   * @param request request
   * @return filtered events
   */
  FetchEventsResponse getFilteredEvents(FetchEventsRequest request);

  /**
   * This method will get all filtered things to do.
   * @param request request
   * @return filtered things to do
   */
  FetchThingsToDoResponse getFilteredThingsToDo(FetchThingsToDoRequest request);
}
