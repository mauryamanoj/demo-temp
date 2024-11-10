package com.saudi.tourism.core.services.stories.v1.filters;

import java.util.Objects;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Categories Filter. */
@Component(service = StoriesFilter.class, immediate = true)
public class CategoriesFilter implements StoriesFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchStoriesRequest request, final @NonNull Story story) {
    if (CollectionUtils.isEmpty(request.getCategories())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(story.getCategoriesTags())) {
      return Boolean.FALSE;
    }

    return story.getCategoriesTags().stream()
        .anyMatch(tag -> request.getCategories().stream()
            .anyMatch(category -> StringUtils.containsIgnoreCase(tag, category)));
  }
}
