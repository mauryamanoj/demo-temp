package com.saudi.tourism.core.models.components.citydetails;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Collapsible Text and Link.
 */
@Model(adaptables = {Resource.class },
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class CollapsibleTextAndLink {

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * readMore Text.
   */
  @ValueMapValue
  @Expose
  private String readMoreLabel;

  /**
   * readLess Text.
   */
  @ValueMapValue
  @Expose
  private String readLessLabel;

}
