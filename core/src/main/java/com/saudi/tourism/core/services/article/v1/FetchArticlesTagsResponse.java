package com.saudi.tourism.core.services.article.v1;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/** Response to filter articles tags. */
@Builder
@Data
public class FetchArticlesTagsResponse implements Serializable {

  /** Articles Tags. */
  private List<ArticleTag> data;
}
