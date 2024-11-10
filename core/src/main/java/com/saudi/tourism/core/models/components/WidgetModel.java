package com.saudi.tourism.core.models.components;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Class for Widget component.
 */

@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class WidgetModel {

  /**
  * Source.
  */
  @ValueMapValue
  private String source;

  /**
   * Variable for title.
   */
  @ValueMapValue
  private String title;
}
