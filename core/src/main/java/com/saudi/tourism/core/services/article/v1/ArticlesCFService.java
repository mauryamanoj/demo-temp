package com.saudi.tourism.core.services.article.v1;


/**
 * ArticlesCF Service.
 */
public interface ArticlesCFService {

  /**
   * Get list of articles.
   *
   * @param request the query parameter
   * @return a list of articles
   */
  FetchArticlesResponse getArticles(FetchArticlesRequest request);

  /**
   * Get article by id.
   *
   * @param request the query parameter
   * @return the article
   */
  Article getArticle(FetchArticlesRequest request);

  /**
   * Get list of articles for home page.
   *
   * @param fetchArticlesRequest the query parameter
   * @return a list of articles
   */
  FetchArticlesHomeResponse getArticlesHome(FetchArticlesRequest fetchArticlesRequest);

  /**
   * Get list of articles tags.
   *
   * @param fetchArticlesRequest the query parameter
   * @return a list of articles tags
   */
  FetchArticlesTagsResponse getArticlesTags(FetchArticlesRequest fetchArticlesRequest);
}
