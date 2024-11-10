package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppVersion;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * NativeAppVersionService nativeAppVersionService.
 */
public interface NativeAppVersionService {
  /**
   *
   * @param request request.
   * @param path path .
   * @return model.
   */
  NativeAppVersion getAllVersionInfo(SlingHttpServletRequest request, String path);

}
