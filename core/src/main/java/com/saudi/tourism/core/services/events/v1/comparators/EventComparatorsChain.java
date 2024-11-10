package com.saudi.tourism.core.services.events.v1.comparators;

import com.saudi.tourism.core.services.events.v1.Event;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;

import java.util.Comparator;

/**
 * Event comparator chain.
 */
public interface EventComparatorsChain {
  Comparator<Event> buildComparator(FetchEventsRequest eventsRequest);
}
