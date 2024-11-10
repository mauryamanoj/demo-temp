package com.saudi.tourism.core.services.mobile.v1.items.adapters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import java.util.List;

public interface AutoItemsAdapterFactory {

  /**
   * This method gets all items from events and things to do.
   *
   * @param resourceResolver Resource Resolver
   * @param settingsService Settings Service
   * @param request request
   * @param types Types
   * @return list of items
   */
  List<ItemResponseModel> getItems(
      ResourceResolver resourceResolver,
      SlingSettingsService settingsService,
      MobileRequestParams request,
      List<String> types);
}
