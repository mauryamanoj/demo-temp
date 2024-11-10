package com.saudi.tourism.core.services.events.v1.comparators;

import com.saudi.tourism.core.services.events.v1.Event;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Comparator;
import java.util.function.Function;

/** Period event comparator. */
@Component(service = EventComparator.class, immediate = true)
public class PeriodEventComparator implements EventComparator {
  @Override
  public boolean supports(final @NonNull String sortByKey) {
    return StringUtils.equals(sortByKey, "period");
  }

  @Override
  public Comparator<Event> buildComparator() {
    Function<Event, Long> extractor = Event::getPeriodDays;
    // if an event have a null PeriodDays it will be returned first
    return Comparator.comparing(extractor, Comparator.nullsFirst(Long::compareTo));
  }
}
