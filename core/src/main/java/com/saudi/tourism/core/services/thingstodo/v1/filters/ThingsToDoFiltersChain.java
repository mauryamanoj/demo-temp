package com.saudi.tourism.core.services.thingstodo.v1.filters;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import org.apache.sling.api.resource.Resource;

/**
 * ThingsToDo Filter chain.
 */
public interface ThingsToDoFiltersChain {
  Boolean doFilter(FetchThingsToDoRequest request, Resource thingToDo);
}
