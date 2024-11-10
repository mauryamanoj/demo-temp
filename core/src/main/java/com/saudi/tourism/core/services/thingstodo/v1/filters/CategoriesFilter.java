package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.util.Arrays;
import java.util.Objects;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/** Categories Filter. */
@Component(service = ThingsToDoFilter.class, immediate = true)
public class CategoriesFilter implements ThingsToDoFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    if (CollectionUtils.isEmpty(request.getCategories())) {
      return Boolean.TRUE;
    }

    String[] categories = thingToDo.getValueMap().get("categories", String[].class);
    if (Objects.isNull(categories)) {
      return Boolean.FALSE;
    }

    return Arrays.stream(categories)
        .anyMatch(tag -> request.getCategories().stream()
            .anyMatch(category -> StringUtils.containsIgnoreCase(tag, category)));
  }
}
