package com.saudi.tourism.core.models.components.search;


import com.day.cq.commons.jcr.JcrConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * SearchResultModel.
 * This model contains all the required fields of the content page
 */
@AllArgsConstructor
@Data
@Builder
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResultModel implements Serializable {

  /**
   * Variable to store page title.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  private String pageTitle;

  /**
   * Variable to store page description.
   */
  @ValueMapValue(name = JcrConstants.JCR_DESCRIPTION)
  private String pageDescription;

  /**
   * Variable to store page path.
   */
  private String pagePath;

  /**
   * Variable to store feature image path.
   */
  @ValueMapValue(name = "featureImage")
  private String featureImage;

  /**
   * Variable to store total results.
   */
  private Long totalResults;

  /**
   * Page type.
   */
  private String type;

  /**
   * Id.
   */
  private String id;
  /**
   * categories.
   */
  private List<String> categories;

}
