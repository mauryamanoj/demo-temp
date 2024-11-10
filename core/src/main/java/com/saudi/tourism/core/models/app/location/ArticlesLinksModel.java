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
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ArticlesLinksModel {

  /**
   * Link.
   */
  @ValueMapValue
  private String link;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Latitude.
   */
  @ValueMapValue
  private String latitude;

  /**
   * Longitude.
   */
  @ValueMapValue
  private String longitude;

  /**
   * Type.
   */
  @ValueMapValue
  private String type;

}
