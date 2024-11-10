package com.saudi.tourism.core.beans;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Search parameters.
 */
@Value
@Builder
public class SearchParams {

  /**
   * Search paths to run query against.
   */
  @Singular
  private final List<String> searchPaths;

  /**
   * Exclude path from search.
   */
  private final String excludeSearchPath;

  /**
   * Node types.
   */
  private final List<String> nodeTypes;

  /**
   * String fragment to be searched.
   */
  private final String searchString;

  /**
   * Offset.
   */
  @NonNull
  private final Long offset;

  /**
   * Rows per page.
   */
  private final int limit;

  /**
   * Use full text search.
   */
  private final boolean fullTextSearch;

}
