package com.saudi.tourism.core.models.components.packages;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class PackageInfoItem.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PackageInfoItem implements Serializable {

  /**
   * The text.
   */
  @ValueMapValue
  @Getter
  private String text;

}
