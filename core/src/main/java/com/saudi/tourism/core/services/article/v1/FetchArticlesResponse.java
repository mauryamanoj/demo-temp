package com.saudi.tourism.core.services.article.v1;

import java.io.Serializable;
import java.util.List;

import com.saudi.tourism.core.services.events.v1.Pagination;
import lombok.Builder;
import lombok.Data;

/** Response to filter articles CF. */
@Builder
@Data
public class FetchArticlesResponse implements Serializable {

  /** Articles. */
  private List<ArticleSummary> data;

  /**Pagination.*/
  private Pagination pagination;
}
