package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import java.util.Comparator;
import java.util.function.Function;

import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Period comparator. */
@Component(service = ThingToDoComparator.class, immediate = true)
public class PeriodThingToDoComparator implements ThingToDoComparator {
  @Override
  public boolean supports(final @NonNull String sortByKey) {
    return StringUtils.equals(sortByKey, "period");
  }

  @Override
  public Comparator<ThingToDoModel> buildComparator() {
    Function<ThingToDoModel, Long> extractor = ThingToDoModel::getPeriodDays;
    return Comparator.comparing(extractor, Comparator.nullsLast(Long::compareTo));
  }
}
