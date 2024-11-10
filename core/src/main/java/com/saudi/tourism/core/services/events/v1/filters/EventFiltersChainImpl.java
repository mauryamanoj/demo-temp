package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;

/**
 * Event Filter chain.
 */
@Component(service = EventFiltersChain.class, immediate = true)
public class EventFiltersChainImpl implements EventFiltersChain {

  /**
   * Event Filters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<EventFilter> filters;

  @Override
  public Boolean doFilter(final @NonNull FetchEventsRequest request, final @NonNull EventCFModel event) {
    if (CollectionUtils.isEmpty(filters)) {
      return Boolean.FALSE;
    }

    // If one of the filters returned false
    // Let's stop, this event will not be taken
    for (EventFilter filter : filters) {
      if (!filter.meetFilter(request, event)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }
}
