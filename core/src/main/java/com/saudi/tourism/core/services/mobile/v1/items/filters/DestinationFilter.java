package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

/** Destination filter for items. */
@Component(service = ItemFilter.class, immediate = true)
public class DestinationFilter implements ItemFilter {

  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    if (StringUtils.isNotBlank(item.getType()) && item.getType().equals("experience")) {
      return Boolean.TRUE;
    }

    if (CollectionUtils.isEmpty(request.getDestinations())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(item.getLocation())) {
      return Boolean.FALSE;
    }

    if (item.getLocation().getAllDestinations() != null && item.getLocation().getAllDestinations()) {
      return Boolean.TRUE;
    }

    final var itemDestination = item.getLocation().getDestination();

    if (Objects.isNull(itemDestination) || StringUtils.isBlank(itemDestination.getId())) {
      return Boolean.FALSE;
    }

    return request.getDestinations().stream()
      .anyMatch(d -> StringUtils.equalsIgnoreCase(d, itemDestination.getId()));
  }
}
