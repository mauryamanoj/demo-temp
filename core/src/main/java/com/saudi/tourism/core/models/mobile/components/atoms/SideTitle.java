package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Side Title.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SideTitle {

  /**
   * color.
   */
  @ValueMapValue
  private String title;

  /**
   * color.
   */
  @ValueMapValue
  private String color;
}
