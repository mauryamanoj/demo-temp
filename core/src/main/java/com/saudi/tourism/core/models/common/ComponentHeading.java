package com.saudi.tourism.core.models.common;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Class - component heading.
 */
@Data
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ComponentHeading {
  /**
  * Component heading.
  */
  @ChildResource
  @Expose
  private Heading heading;

  /**
  * link of the component title.
  */
  @ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
  @Expose
  private Link link;

  /**
   * Variable indicate heading showUnderline.
   */
  @ValueMapValue
  @Expose
  private boolean showUnderline;

  /**
   * Show Slider.
   */
  @ValueMapValue
  @Expose
  private boolean showSlider;

  /**
   * Show View All Slider.
   */
  @ValueMapValue
  @Expose
  private boolean showViewAllSlider;
}
