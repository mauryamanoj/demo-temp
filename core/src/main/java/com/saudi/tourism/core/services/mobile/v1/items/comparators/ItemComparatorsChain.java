package com.saudi.tourism.core.services.mobile.v1.items.comparators;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;

import java.util.Comparator;

/** Item comparator chain. */
public interface ItemComparatorsChain {
  Comparator<ItemResponseModel> buildComparator(MobileRequestParams request);
}
