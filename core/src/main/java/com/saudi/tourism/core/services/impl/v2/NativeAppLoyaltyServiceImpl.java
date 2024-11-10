package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppLoyaltyCarouselModel;
import com.saudi.tourism.core.services.v2.NativeAppLoyaltyService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * NativeAppLoyaltyServiceImpl nativeAppLoyaltyServiceImpl .
 */
@Component(service = NativeAppLoyaltyService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Native App Loyalty Service"})

public class NativeAppLoyaltyServiceImpl implements NativeAppLoyaltyService {

  /**
   * LOYALITY_CARSOUSEL Constant.
   */
  private static final String LOYALITY_CARSOUSEL = "nativeAppLoyaltyCarousel";


  /**
   * get getLoyaltyCarouselInfo.
   *
   * @param request servlet request.
   * @param path    resource path.
   * @return model.
   */
  public NativeAppLoyaltyCarouselModel getLoyaltyCarouselInfo(SlingHttpServletRequest request, String path) {
    Resource resource = request.getResourceResolver().
        getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + LOYALITY_CARSOUSEL);
    NativeAppLoyaltyCarouselModel model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppLoyaltyCarouselModel.class);
    }
    return model;
  }
}
