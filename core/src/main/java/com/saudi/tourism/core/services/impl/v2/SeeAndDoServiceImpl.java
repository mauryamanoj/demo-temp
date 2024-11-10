package com.saudi.tourism.core.services.impl.v2;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.page.NativeAppSeeAndDoOrder;
import com.saudi.tourism.core.services.v2.SeeAndDoService;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * SeeAndDoServiceImpl .
 */
@Component(service = SeeAndDoService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "See And Do Order Service"})
@Slf4j
public class SeeAndDoServiceImpl implements SeeAndDoService {
  /**
   * SEE_DO_ORDER variable .
   */
  private static final String SEE_DO_ORDER = "seeDoOrder";

  @Override
   public NativeAppSeeAndDoOrder getNativeSeeAndDoOrder(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + SEE_DO_ORDER);

    NativeAppSeeAndDoOrder model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppSeeAndDoOrder.class);
    }
    return model;
  }
}
