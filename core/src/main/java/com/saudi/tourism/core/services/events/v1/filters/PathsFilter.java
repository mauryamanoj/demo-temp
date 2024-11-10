package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Paths Filter. */
@Component(service = EventFilter.class, immediate = true)
public class PathsFilter implements EventFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchEventsRequest request, final @NonNull EventCFModel event) {
    if (CollectionUtils.isEmpty(request.getPaths())) {
      return Boolean.TRUE;
    }

    if (StringUtils.isEmpty(event.getPath())) {
      return Boolean.FALSE;
    }

    return request.getPaths().stream()
      .filter(p -> StringUtils.equals(p, event.getPath()))
      .findFirst()
      .isPresent();
  }
}
