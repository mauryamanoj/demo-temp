package com.saudi.tourism.core.models.common;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

/**
 * This model will adapt with Social Media Component.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialMediaModel {

  /**
   * Variable to hold list of socialItems.
   */
  @Getter
  @ChildResource
  private List<SocialChannelModel> socialItems;

}
