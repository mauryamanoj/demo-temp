package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.util.Objects;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/** Destinations Filter. */
@Component(service = ThingsToDoFilter.class, immediate = true)
public class DestinationsFilter implements ThingsToDoFilter {

  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    if (CollectionUtils.isEmpty(request.getDestinations())) {
      return Boolean.TRUE;
    }

    String destinationPath = thingToDo.getValueMap().get("locationValue", String.class);
    if (Objects.isNull(destinationPath)) {
      return Boolean.FALSE;
    }

    // Extract the ID from the destination's path
    String destinationId = LinkUtils.getLastPathSegment(destinationPath);

    return request.getDestinations().contains(destinationId);
  }
}
