package com.saudi.tourism.core.models.mobile.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * AboutGoLandingContentSection model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AboutGoLandingContentSection implements Serializable {

  /**
   * title.
   */
  @ValueMapValue
  private String title;
  /**
   * description.
   */
  @ValueMapValue
  private String description;
  /**
   * componentUIType.
   */
  @ValueMapValue
  private String componentUIType;
  /**
   * image.
   */
  @ChildResource
  private List<AboutGoLandingItem> items;


}
