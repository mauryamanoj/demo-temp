package com.saudi.tourism.core.models.components.events;

import com.saudi.tourism.core.models.common.ComponentHeading;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * The Class PastEventListSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PastEventListSlingModel {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  private String title;

  /**
   * The stateLabel.
   */
  @ChildResource
  @Getter
  private List<FilterModel> filters;

  /**
   * The noResults.
   */
  @ValueMapValue
  @Getter
  private String noResults;

  /**
   * The loadMoreText.
   */
  @ValueMapValue
  @Getter
  private String loadMoreText;

  /**
   * The noResultsCopy.
   */
  @ValueMapValue
  @Getter
  private String noResultsCopy;
  /**
   * The loadMoreText.
   */
  @ValueMapValue
  @Getter
  private String noResultsTitle;

  /**
   * Variable - componentHeading for Teaser.
   */
  @Getter
  @ChildResource
  private ComponentHeading componentHeading;

}
