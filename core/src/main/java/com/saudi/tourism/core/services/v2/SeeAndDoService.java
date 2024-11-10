package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.app.page.NativeAppSeeAndDoOrder;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * See and do Service For Order.
 */
public interface SeeAndDoService {

  /**
   * Get the NativeSeeAndDoOrder model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  NativeAppSeeAndDoOrder getNativeSeeAndDoOrder(SlingHttpServletRequest request, String resourcePath);
}
