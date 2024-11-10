package com.saudi.tourism.core.services;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * SearchResultsService Interface.
 */
public interface SearchResultsService {
  /**
   * Find content pages based the input text.
   *
   * @param resourceResolver  ResourceResolver
   * @param currentPage  Page
   * @param fullTextToSearch  String
   * @param resultsOffSet Long
   * @param isFullTextSearch Boolean
   * @return List<SearchResultModel>
   */
  List<SearchResultModel> webSearch(ResourceResolver resourceResolver,
                                            Page currentPage,
                                            String fullTextToSearch,
                                            Long resultsOffSet,
                                            Boolean isFullTextSearch);


  /**
   * Find content.
   * @param resourceResolver resource resolver
   * @param searchParams search parameters
   * @return search results
   */
  SearchListResultModel appSearch(ResourceResolver resourceResolver,
      SearchParams searchParams);
}
