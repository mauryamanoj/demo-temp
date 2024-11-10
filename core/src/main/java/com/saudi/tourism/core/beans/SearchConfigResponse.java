package com.saudi.tourism.core.beans;

import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.components.search.SearchPillsModel;
import com.saudi.tourism.core.models.components.search.SearchTrendingsModel;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Bean for the Search Config API response.
 */
@Data
@Builder
public class SearchConfigResponse implements Serializable {


  /**
   * Variable to store pillBlock.
   */
  private SearchPillsModel pillBlock;

  /**
   * Variable to store pillBlock.
   */
  private SearchTrendingsModel trendingBlock;


  /**
   * Variable to store list of search categories.
   */
  private List<DictItem> searchCategories;
}
