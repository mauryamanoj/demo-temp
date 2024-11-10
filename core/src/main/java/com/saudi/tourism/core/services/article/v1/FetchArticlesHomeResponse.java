package com.saudi.tourism.core.services.article.v1;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/** Response to filter articles home CF. */
@Builder
@Data
public class FetchArticlesHomeResponse implements Serializable {

  /**
   * Featured Articles.
   */
  private List<ArticleSummary> featuredArticles;

  /**
   * New Articles.
   */
  private List<ArticleSummary> newArticles;

  /**
   * Trending Articles.
   */
  private List<ArticleSummary> trendingArticles;

}
