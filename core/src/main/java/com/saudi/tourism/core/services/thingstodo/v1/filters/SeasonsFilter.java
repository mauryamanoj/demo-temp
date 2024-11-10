package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.util.Objects;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/** Seasons Filter. */
@Component(service = ThingsToDoFilter.class, immediate = true)
public class SeasonsFilter implements ThingsToDoFilter {

  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    if (CollectionUtils.isEmpty(request.getSeasons())) {
      return Boolean.TRUE;
    }

    String seasonPath = thingToDo.getValueMap().get("season", String.class);

    if (Objects.isNull(seasonPath)) {
      return Boolean.FALSE;
    }

    // Extract the ID from the season's path
    String seasonId = LinkUtils.getLastPathSegment(seasonPath);

    // Check if the extracted ID is in the list of requested seasons
    return request.getSeasons().contains(seasonId);
  }
}
