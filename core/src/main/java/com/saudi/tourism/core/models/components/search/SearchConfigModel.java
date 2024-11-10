package com.saudi.tourism.core.models.components.search;


import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The type Search config model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SearchConfigModel implements Serializable {

  /**
   * Variable to store page path.
   */
  @ValueMapValue
  @Expose
  private String suggestionsEndpoint;

  /**
   * Variable to store total results.
   */
  @ValueMapValue
  @Expose
  private String searchPagePath;

  /**
   * Variable to store total results.
   */
  @ValueMapValue
  @Expose
  private String searchPlaceholder;

  /**
   * Variable to store total results.
   */
  @ValueMapValue
  @Expose
  private String cancelLabel;

  /**
   * Variable to store total results.
   */
  @ValueMapValue
  @Expose
  private String clearLabel;

  /**
   * Variable to store pillBlock.
   */
  @ChildResource
  @Expose
  private SearchPillsModel pillBlock;

  /**
   * Variable to store trendingBlock.
   */
  @ChildResource
  @Expose
  private SearchTrendingsModel trendingBlock;

  /**
   * Variable to store featured.
   */
  @ChildResource
  @Expose
  private SearchTrendingsModel featured;

  /**
   * Variable to search Page.
   */
  @ChildResource
  @Expose
  private SearchPage searchPage;
}
