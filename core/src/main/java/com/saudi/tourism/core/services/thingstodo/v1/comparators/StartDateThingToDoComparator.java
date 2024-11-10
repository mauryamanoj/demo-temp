package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Function;

import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Start Date comparator. */
@Component(service = ThingToDoComparator.class, immediate = true)
public class StartDateThingToDoComparator implements ThingToDoComparator {
  @Override
  public boolean supports(final @NonNull String sortByKey) {
    return StringUtils.equals(sortByKey, "startDate");
  }

  @Override
  public Comparator<ThingToDoModel> buildComparator() {
    Function<ThingToDoModel, Calendar> extractor = ThingToDoModel::getStartDate;
    return Comparator.comparing(extractor, Comparator.nullsLast(Comparator.naturalOrder()));
  }
}
