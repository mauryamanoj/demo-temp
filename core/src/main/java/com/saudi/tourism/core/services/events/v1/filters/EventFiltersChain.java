package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;

/**
 * Event Filter chain.
 */
public interface EventFiltersChain {
  Boolean doFilter(FetchEventsRequest request, EventCFModel event);
}
