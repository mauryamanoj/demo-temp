package com.sta.core.solr.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Model for Solr response.
 * @param <T> type of the data
 */
@Data
@JsonRootName(value = "section")
public class SolrGroupSection<T> implements Serializable {

  /**
   * The title of the section.
   */
  private String title;

  /**
   * The type of the section.
   */
  private String type;
  /**
   * Docs data.
   */
  private List<T> data;
  /**
   * The pagination.
   */
  private Pagination pagination;



}
