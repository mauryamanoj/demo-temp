package com.sta.core.solr.model;

import lombok.Data;

/**
 * Model for Solr request.
 */
@Data
public class SolrRequest {

  /**
   * The language.
   */
  private String locale;

  /**
   * The language.
   */
  private String source;

  /**
   * The type.
   */
  private String type;

  /**
   * The region ( city )  filter.
   */
  private String region;

  /**
   * The limit.
   */
  private Integer limit = 0;

  /**
   * The offset.
   */
  private Integer offset = 0;

  /**
   * The search query.
   */
  private String query;

  /**
   * The suggestion string.
   */
  private String suggestion;

  /**
   * The sort field.
   */
  private String sort;

  /**
   * flag to  include articles.
   */
  private String includeArticles;
}
