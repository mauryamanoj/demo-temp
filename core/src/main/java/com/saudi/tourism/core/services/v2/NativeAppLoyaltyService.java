package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppLoyaltyCarouselModel;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * NativeAppLoyaltyService nativeAppLoyaltyService.
 */
public interface NativeAppLoyaltyService {
  /**
   *
   * @param request request.
   * @param path path .
   * @return model.
   */
  NativeAppLoyaltyCarouselModel getLoyaltyCarouselInfo(SlingHttpServletRequest request, String path);

}
