package com.saudi.tourism.core.services.events.v1.comparators;

import com.saudi.tourism.core.services.events.v1.Event;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Event comparator chain. */
@Component(service = EventComparatorsChain.class, immediate = true)
public class EventComparatorsChainImpl implements EventComparatorsChain {
  /**
   * Event Comparators.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<EventComparator> comparators;

  @Override
  public Comparator<Event> buildComparator(final @NonNull FetchEventsRequest eventsRequest) {
    final Function<Event, Calendar> extractor = Event::getStartDate;
    final var defaultComparator = Comparator.comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()));

    if (CollectionUtils.isEmpty(comparators)) {
      return defaultComparator;
    }

    if (CollectionUtils.isEmpty(eventsRequest.getSortBy())) {
      return defaultComparator;
    }

    Comparator<Event> composedComparator = null;
    for (String sortByKey : eventsRequest.getSortBy()) {
      var optionalEventComparator =
          comparators.stream().filter(c -> c.supports(sortByKey)).findFirst();

      if (!optionalEventComparator.isPresent()) {
        continue;
      }

      final var eventComparator = optionalEventComparator.get();

      if (Objects.isNull(composedComparator)) {
        composedComparator = eventComparator.buildComparator();
      } else {
        composedComparator = composedComparator.thenComparing(eventComparator.buildComparator());
      }
    }

    if (Objects.isNull(composedComparator)) {
      return defaultComparator;
    }
    return composedComparator;
  }
}
