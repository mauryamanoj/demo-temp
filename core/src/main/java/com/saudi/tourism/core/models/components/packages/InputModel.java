package com.saudi.tourism.core.models.components.packages;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class InputModel for input dialog mapping.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
public class InputModel implements Serializable {

  /**
   * The filterTitle.
   */
  @ValueMapValue
  @Getter
  private String label;
  /**
   * The pattern.
   */
  @ValueMapValue
  @Getter
  private String pattern;
  /**
   * The placeholder.
   */
  @ValueMapValue
  @Getter
  private String placeholder;
  /**
   * The date.
   */
  @ValueMapValue
  @Getter
  private String type;
  /**
   * The date.
   */
  @ValueMapValue
  @Getter
  private String name;
  /**
   * The date.
   */
  @ValueMapValue
  @Getter
  private boolean required;

}
