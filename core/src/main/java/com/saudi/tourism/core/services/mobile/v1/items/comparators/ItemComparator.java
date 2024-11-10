package com.saudi.tourism.core.services.mobile.v1.items.comparators;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;

import java.util.Comparator;

/** Item comparator interface. */
public interface ItemComparator {
  boolean supports(String sortBy);
  Comparator<ItemResponseModel> buildComparator();
}
