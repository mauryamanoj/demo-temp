package com.saudi.tourism.core.services.mobile.v1.items.comparators;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Item comparators chain. */
@Component(service = ItemComparatorsChain.class, immediate = true)
public class ItemComparatorsChainImpl implements ItemComparatorsChain {
  /**
   * Item comparators.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ItemComparator> comparators;

  @Override
  public Comparator<ItemResponseModel> buildComparator(final @NonNull MobileRequestParams request) {
    final Function<ItemResponseModel, Calendar> extractor = item ->
        Optional.ofNullable(item)
            .map(ItemResponseModel::getDate)
            .map(date -> date.getStartDate()) // This extracts the startDate, potentially null
            .orElse(null);

    final var defaultComparator =
        Comparator.comparing(extractor, Comparator.nullsFirst(Calendar::compareTo));

    if (CollectionUtils.isEmpty(comparators)) {
      return defaultComparator;
    }

    if (CollectionUtils.isEmpty(request.getSortBy())) {
      return defaultComparator;
    }

    Comparator<ItemResponseModel> composedComparator = null;
    for (String sortBy : request.getSortBy()) {
      var optionalItemComparator = comparators.stream().filter(c -> c.supports(sortBy)).findFirst();
      if (!optionalItemComparator.isPresent()) {
        continue;
      }

      final var itemComparator = optionalItemComparator.get();
      if (Objects.isNull(composedComparator)) {
        composedComparator = itemComparator.buildComparator();
      } else {
        composedComparator = composedComparator.thenComparing(itemComparator.buildComparator());
      }
    }

    if (Objects.isNull(composedComparator)) {
      return defaultComparator;
    }

    return composedComparator;
  }
}
