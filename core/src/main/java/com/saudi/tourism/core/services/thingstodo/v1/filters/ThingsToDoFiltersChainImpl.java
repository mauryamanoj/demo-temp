package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.util.List;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * ThingsToDo Filter chain.
 */
@Component(service = ThingsToDoFiltersChain.class, immediate = true)
public class ThingsToDoFiltersChainImpl implements ThingsToDoFiltersChain {

  /**
   * ThingsToDo Filters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ThingsToDoFilter> filters;

  @Override
  public Boolean doFilter(final @NonNull FetchThingsToDoRequest request, final Resource thingToDo) {
    if (CollectionUtils.isEmpty(filters)) {
      return Boolean.TRUE;
    }

    // If one of the filters returned false
    // Let's stop, this ThingToDo will not be taken
    for (ThingsToDoFilter filter : filters) {
      if (!filter.meetFilter(request, thingToDo)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }
}
