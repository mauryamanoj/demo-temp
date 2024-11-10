package com.sta.core.solr.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Model for Solr suggestion response.
 * @param <T> anyObj
 */
@Data
public class SolrSuggestionResponse<T> implements Serializable {

  /**
   * Suggestions.
   */
  private List<String> data;

  /**
   * results.
   */
  private List<T> results;
}
