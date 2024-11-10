package com.saudi.tourism.core.services.article.v1.filters;

import java.util.List;

import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Articles Filter chain.
 */
@Component(service = ArticlesFiltersChain.class, immediate = true)
public class ArticlesFiltersChainImpl implements ArticlesFiltersChain {

  /**
   * Article Filters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ArticlesFilter> filters;

  @Override
  public Boolean doFilter(final @NonNull FetchArticlesRequest request, final @NonNull ArticleCFModel articleCFModel) {
    if (CollectionUtils.isEmpty(filters)) {
      return Boolean.TRUE;
    }

    // If one of the filters returned false
    // Let's stop, this Article will not be taken
    for (ArticlesFilter filter : filters) {
      if (!filter.meetFilter(request, articleCFModel)) {
        return Boolean.FALSE;
      }
    }

    return Boolean.TRUE;
  }
}
