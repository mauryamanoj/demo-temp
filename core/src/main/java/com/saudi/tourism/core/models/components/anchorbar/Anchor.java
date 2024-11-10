package com.saudi.tourism.core.models.components.anchorbar;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Anchor.
 */
@Model(adaptables = {
    Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Anchor {

  /**
   * ID.
   */
  @Expose
  @ValueMapValue
  private String id;

  /**
   * Label.
   */
  @ValueMapValue
  @Expose
  private String label;

}
