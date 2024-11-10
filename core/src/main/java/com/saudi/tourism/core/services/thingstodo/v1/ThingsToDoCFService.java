package com.saudi.tourism.core.services.thingstodo.v1;


import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Things ToDo Service.
 */
public interface ThingsToDoCFService {

  /**
   * Get list of filtered events.
   *
   * @param request the query parameter
   * @return a list of events
   */
  FetchThingsToDoResponse getFilteredThingsToDo(FetchThingsToDoRequest request);

  /**
   * Fetch All ThingsToDo with filters.
   *
   * @param request          Current request.
   * @param resourceResolver Resource Resolver.
   *
   * @return All ThingsToDo with filters
   */
  SearchResult fetchAllThingsToDo(FetchThingsToDoRequest request, ResourceResolver resourceResolver);
}
