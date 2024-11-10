package com.saudi.tourism.core.models.components.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * SearchResults version 2.
 *
 */
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class SearchResultsV2 {

  /**
   * resultsCountLabel.
   */
  @Expose
  @ValueMapValue
  private String resultsCountLabel;

  /**
   * locationLabel.
   */
  @Expose
  @ValueMapValue
  private String locationLabel;

  /**
   * contentLabel.
   */
  @Expose
  @ValueMapValue
  private String contentLabel;

  /**
   * loadMoreLabel.
   */
  @Expose
  @ValueMapValue
  private String loadMoreLabel;

  /**
   * Most recent search.
   */
  @Expose
  @ValueMapValue
  private String dateSort;

  /**
   * variable to hold Search Placeholder.
   */
  @Expose
  @ValueMapValue
  private String searchPlaceholderLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @ValueMapValue
  @Expose
  private String noResultsFoundLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @ValueMapValue
  @Expose
  private String sortFilterLabel;

  /**
   * variable to hold noResultsFoundDescription.
   */
  @ValueMapValue
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
   * variable to hold clear all filters labels.
   */
  @ValueMapValue
  @Expose
  private String clearAllFiltersLabel;

  /**
   * variable to hold number of results found.
   */
  @ValueMapValue
  @Expose
  private String resultCountLabel;

  /**
   * getJson method for search results component.
   *
   * @return json representation.
   */
  @PostConstruct
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }

}
