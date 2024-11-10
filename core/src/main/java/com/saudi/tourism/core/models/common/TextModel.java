package com.saudi.tourism.core.models.common;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;


/**
 * Model class of text related data.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextModel implements Serializable {


  /**
   * Variable text.
   */
  @Getter
  @ValueMapValue
  private String text;

  /**
   * Variable description.
   */
  @Getter
  @ValueMapValue
  private String description;

  /**
   * Variable text.
   */
  @Getter
  @ValueMapValue
  private String title;
}
