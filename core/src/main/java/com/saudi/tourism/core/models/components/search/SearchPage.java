package com.saudi.tourism.core.models.components.search;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.List;

import com.saudi.tourism.core.models.common.DictItem;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Data
public class SearchPage implements Serializable {

  /**
   * searchPagePath.
   */
  @Getter
  @Expose
  private List<DictItem> contentTypeFilter;

  /**
   * Regions.
   */
  @Getter
  @Expose
  private List<DictItem> regions;

  /**
   * searchPagePath.
   */
  @Getter
  @Expose
  private String searchPagePath;

  /**
   * Api call limit.
   */
  @Getter
  @Expose
  private int searchLimit;

  /**
   * resultsCountLabel.
   */
  @Getter
  @Expose
  private String resultsCountLabel;

  /**
   * clearAllFiltersLabel.
   */
  @Getter
  @Expose
  private String clearAllFiltersLabel;

  /**
   * clearAllFiltersLabel.
   */
  @Getter
  @Expose
  private String applyAllFiltersLabel;

  /**
   * listViewLabel.
   */
  @Getter
  @Expose
  private String listViewLabel;

  /**
   * listViewLabel.
   */
  @Getter
  @Expose
  private String gridViewLabel;

  /**
   * locationLabel.
   */
  @Getter
  @Expose
  private String locationLabel;

  /**
   * contentLabel.
   */
  @Getter
  @Expose
  private String contentLabel;

  /**
   * readMoreLabel.
   */
  @Getter
  @Expose
  private String readMoreLabel;

  /**
   * Most recent search.
   */
  @Getter
  @Expose
  private String dateSort;

  /**
   * variable to hold searchPlaceholderLabel.
   */
  @Getter
  @Expose
  private String searchPlaceholderLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @Getter
  @Expose
  private String noResultsFoundLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @Getter
  @Expose
  private String sortFilterLabel;

  /**
   * variable to hold noResultFoundDescription.
   */
  @Getter
  @Expose
  private String noResultFoundDescription;

  /**
   * variable to hold View As.
   */
  @ValueMapValue
  @Expose
  private String viewAs;

  /**
   * variable to hold filters.
   */
  @ValueMapValue
  @Expose
  private String filters;

  /**
   * variable to hold View All results in the search modal.
   */
  @ValueMapValue
  @Expose
  private String viewAllResultsLabel;

  /**
   * variable to hold Recently searched in the search modal.
   */
  @ValueMapValue
  @Expose
  private String recentlySearchedLabel;

  /**
   * variable to hold Show all in the mobile search modal.
   */
  @ValueMapValue
  @Expose
  private String showAllFilters;

  /**
   * variable to hold mobile filters modal title.
   */
  @ValueMapValue
  @Expose
  private String modalFiltersTitle;

  /**
   * variable to hold mobile modal sort by filter.
   */
  @ValueMapValue
  @Expose
  private String modalSortByFilter;

  /**
   * variable to hold mobile modal view as.
   */
  @ValueMapValue
  @Expose
  private String modalViewAsLabel;
}
