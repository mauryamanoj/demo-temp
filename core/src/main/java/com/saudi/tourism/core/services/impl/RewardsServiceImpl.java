package com.saudi.tourism.core.services.impl;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.page.NativeAppRewardsOrder;
import com.saudi.tourism.core.services.RewardsService;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * RewardsServiceImpl .
 */
@Component(service = RewardsService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Rewards Order Service"})
@Slf4j
public class RewardsServiceImpl implements RewardsService {
  /**
   * rewardsOrder variable .
   */
  private static final String REWARDS_ORDER = "rewardsOrder";

  @Override
  public NativeAppRewardsOrder getNativeAppRewardsOrder(@NonNull final SlingHttpServletRequest request,
                                                        @NonNull final String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
            + Constants.FORWARD_SLASH_CHARACTER + REWARDS_ORDER);

    NativeAppRewardsOrder model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppRewardsOrder.class);
    }
    return model;
  }
}
