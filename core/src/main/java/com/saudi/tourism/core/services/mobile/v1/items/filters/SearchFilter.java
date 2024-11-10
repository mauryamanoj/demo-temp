package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

/** Search filter for items. */
@Component(service = ItemFilter.class, immediate = true)
public class SearchFilter implements ItemFilter {

  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    if (StringUtils.isBlank(request.getSearch())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(item.getTitles()) || StringUtils.isBlank(item.getTitles().getTitle())) {
      return Boolean.FALSE;
    }

    return StringUtils.containsIgnoreCase(item.getTitles().getTitle(), request.getSearch());
  }
}
