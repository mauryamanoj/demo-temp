package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Category filter for items. */
@Component(service = ItemFilter.class, immediate = true)
public class CategoryFilter implements ItemFilter {

  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {
    if (CollectionUtils.isEmpty(request.getCategories())) {
      return Boolean.TRUE;
    }

    if (CollectionUtils.isEmpty(item.getCategories())) {
      return Boolean.FALSE;
    }

    return item.getCategories().stream()
        .map(Category::getId)
        .anyMatch(
            tagId ->
                request.getCategories().stream()
                    .anyMatch(c -> StringUtils.containsIgnoreCase(tagId, c)));
  }
}
