package com.saudi.tourism.core.models.components.reachus.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Quick Link Card Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class QuickLinkCardModel {
  /**
   * Subtitle.
   */
  @ValueMapValue(name = "subtitle")
  @Expose
  private String subTitle;
  /**
   * URL.
   */
  @ChildResource
  @Expose
  private Link link;
}
