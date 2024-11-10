package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppFeatureModel;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * ShowingFeatureService .
 */
public interface ShowingFeatureService {
  /**
   * getAllFeaturesInfo .
   * @param request request .
   * @param path path .
   * @return model.
   */
  NativeAppFeatureModel getAllFeaturesInfo(SlingHttpServletRequest request, String path);
}
