package com.saudi.tourism.core.services.article.v1.filters;

import java.util.Objects;

import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/** Article keyword Filter. */
@Component(service = ArticlesFilter.class, immediate = true)
public class ArticleKeywordFilter implements ArticlesFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchArticlesRequest request, final @NonNull ArticleCFModel articleCFModel) {
    final String keyword = request.getKeyword();

    if (StringUtils.isBlank(keyword)) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(articleCFModel.getTitle()) && Objects.isNull(articleCFModel.getContent())) {
      return Boolean.FALSE;
    }

    boolean isInTitle = StringUtils.containsIgnoreCase(articleCFModel.getTitle(), keyword);
    boolean isInContent = StringUtils.containsIgnoreCase(articleCFModel.getContent(), keyword);

    return isInTitle || isInContent;
  }
}
