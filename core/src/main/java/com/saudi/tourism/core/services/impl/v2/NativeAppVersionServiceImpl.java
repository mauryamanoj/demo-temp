package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppVersion;
import com.saudi.tourism.core.services.v2.NativeAppVersionService;

import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * NativeAppVersionServiceImpl nativeAppVersionServiceImpl .
 */
@Component(service = NativeAppVersionService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Native App Version Service"})

public class NativeAppVersionServiceImpl implements NativeAppVersionService {

  /**
   * App_VERSION_RES Constant.
   */
  private static final String APP_VERSION = "nativeAppVersion";


  /**
   * get getAllVersionInfo.
   *
   * @param request servlet request.
   * @param path    resource path.
   * @return model.
   */
  public NativeAppVersion getAllVersionInfo(SlingHttpServletRequest request, String path) {
    Resource resource = request.getResourceResolver().
        getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + APP_VERSION);
    NativeAppVersion model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppVersion.class);
    }
    return model;
  }

}
