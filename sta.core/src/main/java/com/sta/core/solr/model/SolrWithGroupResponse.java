package com.sta.core.solr.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Model for Solr response with Group.
 *
 * @param <T> type of the data
 */
@Data
public class SolrWithGroupResponse<T> implements Serializable {

  /**
   * List of sections.
   */
  private List<SolrGroupSection<T>> sections;
  /**
   * The suggestion.
   */
  private String suggestion;

}
