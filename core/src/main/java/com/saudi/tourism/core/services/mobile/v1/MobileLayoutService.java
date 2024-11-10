package com.saudi.tourism.core.services.mobile.v1;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionsModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.TabsModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsDetailsResponseModel;

/** This defines all methods to the layout of mobile app. */
public interface MobileLayoutService {

  /**
   * Get info of tab by id.
   *
   * @param request request
   * @return tab infos
   */
  TabsModel getTabById(MobileRequestParams request);

  /**
   * Get info of section by id.
   *
   * @param request request
   * @return section infos
   */
  SectionsModel getSectionById(MobileRequestParams request);

  /**
   * Get info of item by id.
   *
   * @param request request
   * @return item details
   */
  ItemsDetailsResponseModel getItemById(
      MobileRequestParams request);
}
