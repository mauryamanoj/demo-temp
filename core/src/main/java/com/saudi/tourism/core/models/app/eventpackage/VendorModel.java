package com.saudi.tourism.core.models.app.eventpackage;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Vendor model in event package.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class VendorModel implements Serializable {
  /**
   * Item Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Item Url.
   */
  @ValueMapValue
  private String url;
}
