package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.app.page.NativeAppRewardsOrder;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Rewards Service.
 */
public interface RewardsService {

  /**
   * Get the NativeAppRewardsOrder model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  NativeAppRewardsOrder getNativeAppRewardsOrder(SlingHttpServletRequest request, String resourcePath);
}
