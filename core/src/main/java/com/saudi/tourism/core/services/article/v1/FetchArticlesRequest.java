package com.saudi.tourism.core.services.article.v1;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to filter Articles CF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchArticlesRequest {

  /**
   * Default limit.
   */
  public static final Integer DEFAULT_LIMIT = 10;

  /**
   * Default offset.
   */
  public static final Integer DEFAULT_OFFSET = 0;

  /**
   * The language.
   */
  private String locale;

  /**
   * article Id.
   */
  private String articleId;

  /**
   * tag Id.
   */
  private List<String> tagId;

  /**
   * The limit.
   */
  private Integer limit = DEFAULT_LIMIT;

  /**
   * The offset.
   */
  private Integer offset = DEFAULT_OFFSET;

  /**
   * The keyword.
   */
  private String keyword;

  /**
   * CFM tags.
   */
  private List<String> tags;

  /**
   * Properties used to sort.
   */
  private List<String> sortBy;
}
