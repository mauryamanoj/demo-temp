package com.saudi.tourism.core.models.components.cardsgrid;

import com.saudi.tourism.core.models.common.Link;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Grid Card Model.
 *
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GridCard {
  /**
   * City.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String city;

  /**
   * Category.
   */
  @ValueMapValue
  @Getter
  private String activity;

  /**
   * Link.
   */
  @ChildResource
  @Getter
  private Link link;

}
