package com.saudi.tourism.core.services.thingstodo.v1.filters;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import org.apache.sling.api.resource.Resource;

/**
 * Event Filter interface.
 */
public interface ThingsToDoFilter {

  Boolean meetFilter(FetchThingsToDoRequest request, Resource thingToDo);
}
