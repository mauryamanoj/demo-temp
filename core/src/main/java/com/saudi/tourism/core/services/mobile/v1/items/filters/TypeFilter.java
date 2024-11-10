package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Type filter for items. */
@Component(service = ItemFilter.class, immediate = true)
public class TypeFilter implements ItemFilter {

  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    if (CollectionUtils.isEmpty(request.getTypes())) {
      return Boolean.TRUE;
    }

    if (StringUtils.isBlank(item.getType())) {
      return Boolean.FALSE;
    }

    return request.getTypes().stream()
      .anyMatch(type -> StringUtils.equalsIgnoreCase(type, item.getType()));
  }
}
