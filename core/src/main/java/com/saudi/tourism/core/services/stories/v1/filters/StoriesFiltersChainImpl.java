package com.saudi.tourism.core.services.stories.v1.filters;

import java.util.List;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Stories Filter chain.
 */
@Component(service = StoriesFiltersChain.class, immediate = true)
public class StoriesFiltersChainImpl implements StoriesFiltersChain {

  /**
   * Stories Filters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<StoriesFilter> filters;

  @Override
  public Boolean doFilter(final @NonNull FetchStoriesRequest request, final @NonNull Story story) {
    if (CollectionUtils.isEmpty(filters)) {
      return Boolean.TRUE;
    }

    // If one of the filters returned false
    // Let's stop, this story will not be taken
    for (StoriesFilter filter : filters) {
      if (!filter.meetFilter(request, story)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }
}
