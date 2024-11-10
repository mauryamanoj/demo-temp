package com.saudi.tourism.core.models.components.citydetails;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Linkw.
 */
@Model(adaptables = {Resource.class },
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Links {

  /**
   * Copy.
   */
  @ValueMapValue
  private String copy;

  /**
   * Variable of link url.
   */
  @ValueMapValue
  private String url;

}
