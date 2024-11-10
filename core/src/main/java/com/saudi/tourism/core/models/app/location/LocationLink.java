package com.saudi.tourism.core.models.app.location;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Model class of link related data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class LocationLink implements Serializable {

  /**
   * Variable of link id.
   */
  @ValueMapValue
  private String id;

  /**
   * Variable of link url.
   */
  @ValueMapValue
  private String link;

  /**
   * External mode.
   */
  @ValueMapValue
  private String externalMode;

  /**
   * Variable of link title.
   */
  @ValueMapValue
  private String title;

}
