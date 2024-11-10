package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

/** Destination Filter. */
@Component(service = EventFilter.class, immediate = true)
public class DestinationFilter implements EventFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchEventsRequest request, final @NonNull EventCFModel event) {
    if (StringUtils.isBlank(request.getDestination())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(event.getDestination())) {
      return Boolean.FALSE;
    }

    if (Objects.isNull(event.getDestination().getResource())) {
      return Boolean.FALSE;
    }

    return StringUtils.equals(event.getDestination().getResource().getPath(), request.getDestination());
  }
}
