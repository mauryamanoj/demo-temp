package com.saudi.tourism.core.services.article.v1.filters;

import java.util.Arrays;
import java.util.Objects;

import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Article tags Filter. */
@Component(service = ArticlesFilter.class, immediate = true)
public class ArticleTagsFilter implements ArticlesFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchArticlesRequest request, final @NonNull ArticleCFModel articleCFModel) {
    if (CollectionUtils.isEmpty(request.getTagId())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(articleCFModel.getTags())) {
      return Boolean.FALSE;
    }

    return Arrays.stream(articleCFModel.getTags())
        .anyMatch(tag -> request.getTagId().stream()
            .anyMatch(articleTag -> StringUtils.containsIgnoreCase(tag, articleTag)));
  }
}
