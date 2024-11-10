package com.saudi.tourism.core.services.stories.v1.filters;

import java.util.Objects;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;

/** Destinations Filter. */
@Component(service = StoriesFilter.class, immediate = true)
public class DestinationsFilter implements StoriesFilter {

  @Override
  public Boolean meetFilter(final @NonNull FetchStoriesRequest request, final @NonNull Story story) {
    if (CollectionUtils.isEmpty(request.getDestinations())) {
      return Boolean.TRUE;
    }

    String destinationPath = story.getDestinationPath();
    if (Objects.isNull(destinationPath)) {
      return Boolean.FALSE;
    }

    // Extract the ID from the destination's path
    String destinationId = LinkUtils.getLastPathSegment(destinationPath);

    return request.getDestinations().contains(destinationId);
  }
}
