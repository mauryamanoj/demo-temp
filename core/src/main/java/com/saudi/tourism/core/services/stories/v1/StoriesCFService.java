package com.saudi.tourism.core.services.stories.v1;

import java.util.List;

import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Stories CF Service.
 */
public interface StoriesCFService {

  /**
   * Fetch All Stories.
   *
   * @param request Fetch Stories Request
   * @param resourceResolver Resource Resolver
   * @return list of Season CF Model
   */
  FetchStoriesResponse fetchAllStories(FetchStoriesRequest request, ResourceResolver resourceResolver);
  /**
   * Fetch Latest Stories.
   *
   * @param locale Current locale
   * @param limit limit
   * @return list of Season CF Model
   */
  List<StoryCFModel> fetchLatestStories(String locale, String limit);

  FetchStoriesResponse getFilteredStories(FetchStoriesRequest request);
}
