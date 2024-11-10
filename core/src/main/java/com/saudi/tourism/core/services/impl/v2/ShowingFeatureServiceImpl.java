package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppFeatureModel;
import com.saudi.tourism.core.services.v2.ShowingFeatureService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * ShowingFeatureServiceImpl ShowingFeatureServiceImpl.
 */
@Component(service = ShowingFeatureService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "ShowingFeature App Service"})
public class ShowingFeatureServiceImpl implements ShowingFeatureService {

  @Override
  public NativeAppFeatureModel getAllFeaturesInfo(SlingHttpServletRequest request, String path) {
    Resource resource = request.getResourceResolver().
        getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
    NativeAppFeatureModel model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppFeatureModel.class);
    }
    return model;
  }
}
