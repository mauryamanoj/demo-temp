package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;

import java.util.Comparator;

/** comparator interface. */
public interface ThingToDoComparator {
  boolean supports(String sortByKey);

  Comparator<ThingToDoModel> buildComparator();
}
