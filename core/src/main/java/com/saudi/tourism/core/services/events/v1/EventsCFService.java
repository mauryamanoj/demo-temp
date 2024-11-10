package com.saudi.tourism.core.services.events.v1;


import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import lombok.NonNull;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * Events Service.
 */
public interface EventsCFService {

  /**
   * Get list of filtered events.
   *
   * @param request the query parameter
   * @return a list of events
   */
  FetchEventsResponse getFilteredEvents(FetchEventsRequest request);

  /**
   * This method gets all events from event content fragments.
   *
   * @param resourceResolver resourceResolver
   * @param locale locale
   * @return all events
   */
  List<EventCFModel> getAllEvents(@NonNull ResourceResolver resourceResolver, @NonNull String locale);
}
