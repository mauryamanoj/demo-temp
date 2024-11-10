package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * This Class contains Social Channel details.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialChannelModel {

  /**
   * Variable of socialChannel.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String socialChannel;

  /**
   * Variable of socialChannelUrl.
   */
  @Getter
  @Expose
  @ValueMapValue
  private String socialChannelUrl;
}
