package com.saudi.tourism.core.services.stories.v1.filters;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;

/**
 * Story Filter interface.
 */
public interface StoriesFilter {

  Boolean meetFilter(FetchStoriesRequest request, Story story);
}
