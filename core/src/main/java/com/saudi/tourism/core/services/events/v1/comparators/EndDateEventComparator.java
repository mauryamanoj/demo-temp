package com.saudi.tourism.core.services.events.v1.comparators;

import com.saudi.tourism.core.services.events.v1.Event;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Function;

/** End Date event comparator. */
@Component(service = EventComparator.class, immediate = true)
public class EndDateEventComparator implements EventComparator {
  @Override
  public boolean supports(final @NonNull String sortByKey) {
    return StringUtils.equals(sortByKey, "endDate");
  }

  @Override
  public Comparator<Event> buildComparator() {
    Function<Event, Calendar> extractor = Event::getEndDate;
    // if an event have a null endDate it will be returned first
    return Comparator.comparing(extractor, Comparator.nullsFirst(Calendar::compareTo));
  }
}
