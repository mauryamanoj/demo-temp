package com.saudi.tourism.core.models.components.search;


import com.saudi.tourism.core.models.components.events.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * SearchResultModel.
 * This model contains all the required fields of the content page
 */
@Data
public class SearchListResultModel implements Serializable {

  /**
   * The search results.
   */
  private List<SearchResultModel> data;

  /**
   * The pagination.
   */
  private Pagination pagination;

}
