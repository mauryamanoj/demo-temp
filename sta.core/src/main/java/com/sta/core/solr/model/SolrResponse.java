package com.sta.core.solr.model;

import com.saudi.tourism.core.models.common.DictItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import com.saudi.tourism.core.models.components.events.Pagination;

/**
 * Model for Solr response.
 * @param <T> type of the data
 */
@Data
public class SolrResponse<T> implements Serializable {

  /**
   * Articles.
   */
  private List<T> data;

  /**
   * mobile queryResults.
   */
  private List<T> queryResults;

  /**
   * mobile auto-complete results
   */
  private List<String> autocompleteResults;

  /**
   * mobile suggestions.
   */
  private List<T> suggestions;

  /**
   * mobile trending.
   */
  private List<String> trending;

  /**
   * mobile total count.
   */
  private Integer totalCount;

  /**
   * The pagination.
   */
  private Pagination pagination;

  /**
   * The suggestion.
   */
  private String suggestion;

  /**
   * The title.
   */
  private String title;

  /**
   * Types.
   */
  private List<String> types;

  /**
   * Regions.
   */
  private List<DictItem> regions;

  /**
   * Content Type .
   */
  private List<DictItem> contentTypeFilter;
}
