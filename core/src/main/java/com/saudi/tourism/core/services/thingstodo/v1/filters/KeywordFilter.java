package com.saudi.tourism.core.services.thingstodo.v1.filters;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/** Keyword Filter. */
@Component(service = ThingsToDoFilter.class, immediate = true)
public class KeywordFilter implements ThingsToDoFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    final String keyword = request.getKeyword();

    if (StringUtils.isBlank(keyword)) {
      return Boolean.TRUE;
    }

    String title = thingToDo.getValueMap().get("title", String.class);
    String description = thingToDo.getValueMap().get("aboutDescription", String.class);

    if (StringUtils.isBlank(title) && StringUtils.isBlank(description)) {
      return Boolean.FALSE;
    }

    boolean isInTitle = StringUtils.containsIgnoreCase(title, keyword);
    boolean isInContent = StringUtils.containsIgnoreCase(description, keyword);

    return isInTitle || isInContent;
  }
}
