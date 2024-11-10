package com.saudi.tourism.core.models.components.events;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The type MapLink.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class MapLink implements Serializable {

  /**
   * The name.
   */
  @ValueMapValue
  private String name;

  /**
   * The label.
   */
  @ValueMapValue
  private String label;

  /**
   * The link.
   */
  @ValueMapValue
  private String link;

  /**
   * The typeMapLink.
   */
  @ValueMapValue
  private String typeMapLink;

  /**
   * The platform.
   */
  @ValueMapValue
  private String platform;


  /**
   * active state.
   */
  @ValueMapValue
  private Boolean active;

}
