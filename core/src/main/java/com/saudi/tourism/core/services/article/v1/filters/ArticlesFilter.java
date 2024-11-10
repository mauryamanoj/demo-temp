package com.saudi.tourism.core.services.article.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;

/**
 * Articles Filter interface.
 */
public interface ArticlesFilter {

  Boolean meetFilter(FetchArticlesRequest request, ArticleCFModel articleCFModel);
}
