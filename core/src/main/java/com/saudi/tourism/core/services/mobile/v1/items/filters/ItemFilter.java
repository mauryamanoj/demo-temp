package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;


/**
 * Section Filter interface.
 */
public interface ItemFilter {

  Boolean meetFilter(MobileRequestParams request, ItemResponseModel item);
}
