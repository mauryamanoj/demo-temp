package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
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

/** Comparator chain. */
@Component(service = ThingToDoComparatorsChain.class, immediate = true)
public class ThingToComparatorsChainImpl implements ThingToDoComparatorsChain {
  /**
   * Event Comparators.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ThingToDoComparator> comparators;

  @Override
  public Comparator<ThingToDoModel> buildComparator(final @NonNull FetchThingsToDoRequest request) {
    final Function<ThingToDoModel, Calendar> extractor = ThingToDoModel::getPublishedDate;
    final var defaultComparator = Comparator.comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed();

    if (CollectionUtils.isEmpty(comparators)) {
      return defaultComparator;
    }

    if (CollectionUtils.isEmpty(request.getSortBy())) {
      return defaultComparator;
    }

    Comparator<ThingToDoModel> composedComparator = null;
    for (String sortByKey : request.getSortBy()) {
      var optionalEventComparator =
          comparators.stream().filter(c -> c.supports(sortByKey)).findFirst();

      if (!optionalEventComparator.isPresent()) {
        continue;
      }

      final var comparator = optionalEventComparator.get();

      if (Objects.isNull(composedComparator)) {
        composedComparator = comparator.buildComparator();
      } else {
        composedComparator = composedComparator.thenComparing(comparator.buildComparator());
      }
    }

    if (Objects.isNull(composedComparator)) {
      return defaultComparator;
    }
    return composedComparator;
  }
}
