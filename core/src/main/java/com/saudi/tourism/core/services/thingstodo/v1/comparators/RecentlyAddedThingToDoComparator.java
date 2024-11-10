package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Function;

/** Recently Added comparator. */
@Component(service = ThingToDoComparator.class, immediate = true)
public class RecentlyAddedThingToDoComparator implements ThingToDoComparator {
  @Override
  public boolean supports(final @NonNull String sortByKey) {
    return StringUtils.equals(sortByKey, "recentlyAdded");
  }

  @Override
  public Comparator<ThingToDoModel> buildComparator() {
    Function<ThingToDoModel, Calendar> extractor = ThingToDoModel::getPublishedDate;
    // We want to get recently added first, that's why we added the reversed
    return Comparator.comparing(extractor).reversed();
  }
}
