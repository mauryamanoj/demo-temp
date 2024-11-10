package com.saudi.tourism.core.services.mobile.v1.items.comparators;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.function.Function;

/** Start Date item comparator. */
@Component(service = ItemComparator.class, immediate = true)
public class StartDateItemComparator implements ItemComparator {

  @Override
  public boolean supports(final @NonNull String sortBy) {
    return StringUtils.equals(sortBy, "startDate");
  }

  @Override
  public Comparator<ItemResponseModel> buildComparator() {
    Function<ItemResponseModel, Calendar> extractor = item -> item.getDate().getStartDate();
    return Comparator.comparing(extractor, Comparator.nullsFirst(Calendar::compareTo));
  }
}
