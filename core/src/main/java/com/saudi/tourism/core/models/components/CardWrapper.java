package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Object to store properties for Article navigation type.
 */
@Data
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardWrapper {

  /**
   * The title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * The link.
   */
  @ChildResource
  @Expose
  private Link link;

}
