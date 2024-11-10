package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppLocaleModel;
import com.saudi.tourism.core.services.v2.NativeAppLocaleService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * NativeAppLocaleServiceImpl  nativeAppLocaleServiceImpl.
 */
@Component(service = NativeAppLocaleService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Native App Locale Service"})
public class NativeAppLocaleServiceImpl implements NativeAppLocaleService {

  /**
   * APP_LOCALE Constant.
   */
  private static final String APP_LOCALE = "nativeAppLocales";

  /**
   * get getAllLocaleInfo.
   *
   * @param request servlet request.
   * @param path    resource path.
   * @return model.
   */
  public NativeAppLocaleModel getAllLocaleInfo(SlingHttpServletRequest request, String path) {
    Resource resource = request.getResourceResolver().
        getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + APP_LOCALE);
    NativeAppLocaleModel model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppLocaleModel.class);
    }
    return model;
  }
}
