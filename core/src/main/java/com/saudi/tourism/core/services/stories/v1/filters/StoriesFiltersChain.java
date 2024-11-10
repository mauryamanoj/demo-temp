package com.saudi.tourism.core.services.stories.v1.filters;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;

/**
 * Stories Filter chain.
 */
public interface StoriesFiltersChain {
  Boolean doFilter(FetchStoriesRequest request, Story story);
}
