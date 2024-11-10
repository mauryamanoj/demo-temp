package com.saudi.tourism.core.models.app.location;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ActivitiesItemsModel {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Description.
   */
  @ValueMapValue
  private String description;

  /**
   * Image.
   */
  @ValueMapValue
  private String image;

}
