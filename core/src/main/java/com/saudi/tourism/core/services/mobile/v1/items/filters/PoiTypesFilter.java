package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;

/**  Poi Types Filter. */
@Component(service = ItemFilter.class, immediate = true)
public class PoiTypesFilter implements ItemFilter {
  @Override
  public Boolean meetFilter(MobileRequestParams request, ItemResponseModel item) {

    if (CollectionUtils.isEmpty(request.getPoiTypes()) && !("poi".equals(item.getType()))) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(item.getPoiTypes())
        || item.getPoiTypes().stream().allMatch(StringUtils::isEmpty)) {
      return Boolean.TRUE;
    }

    return item.getPoiTypes().stream()
        .anyMatch(type -> request.getPoiTypes().stream()
            .anyMatch(poiType -> StringUtils.containsIgnoreCase(type, poiType)));
  }

}
