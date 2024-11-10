package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;

import java.util.Comparator;

/**
 * comparator chain.
 */
public interface ThingToDoComparatorsChain {
  Comparator<ThingToDoModel> buildComparator(FetchThingsToDoRequest request);
}
