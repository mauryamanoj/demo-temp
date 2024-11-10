package com.saudi.tourism.core.models.components.nav.v2;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Object to store properties for Plan navigation type.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class PlanItem {

  /**
   * Variable to store the title.
   */
  @ValueMapValue
  @Expose
  private String planTitle;

  /**
   * Variable to store the description.
   */
  @ValueMapValue
  @Expose
  private String planDescription;

  /**
   * Variable to store the url.
   */
  @ValueMapValue
  @Expose
  private String planUrl;

  /**
   * Variable to store the icon.
   */
  @ValueMapValue
  @Expose
  private String planIcon;
}
