package com.saudi.tourism.core.services.events.v1.comparators;

import com.saudi.tourism.core.services.events.v1.Event;

import java.util.Comparator;

/** Event comparator interface. */
public interface EventComparator {
  boolean supports(String sortByKey);

  Comparator<Event> buildComparator();
}
