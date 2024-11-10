package com.saudi.tourism.core.services.mobile.v1.items.comparators;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Comparator;
import java.util.function.Function;

/** Period item comparator. */
@Component(service = ItemComparator.class, immediate = true)
public class PeriodItemComparator implements ItemComparator {

  @Override
  public boolean supports(final @NonNull String sortBy) {
    return StringUtils.equals(sortBy, "period");
  }

  @Override
  public Comparator<ItemResponseModel> buildComparator() {
    Function<ItemResponseModel, Long> extractor =
        item ->
            CommonUtils.calculateDaysBetween(
                item.getDate().getStartDate(), item.getDate().getEndDate());
    // if an event have a null PeriodDays it will be returned first
    return Comparator.comparing(extractor, Comparator.nullsFirst(Long::compareTo));
  }
}
