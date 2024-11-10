package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

/** Season filter for items. */
@Component(service = ItemFilter.class, immediate = true)
public class SeasonFilter implements ItemFilter {

  @Override
  public Boolean meetFilter(final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {
    if (CollectionUtils.isEmpty(request.getSeasons())) {
      return Boolean.TRUE;
    }

    final var itemSeason = item.getSeason();

    if (Objects.isNull(itemSeason) || StringUtils.isBlank(itemSeason.getId())) {
      return Boolean.FALSE;
    }

    return request.getSeasons().stream()
      .anyMatch(s -> StringUtils.equalsIgnoreCase(s, itemSeason.getId()));
  }
}
