package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppLocaleModel;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * NativeAppLocaleService nativeAppLocaleService.
 */
public interface NativeAppLocaleService {
  /**
   *
   * @param request request.
   * @param path path .
   * @return model.
   */
  NativeAppLocaleModel getAllLocaleInfo(SlingHttpServletRequest request, String path);

}
