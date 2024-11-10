package com.saudi.tourism.core.models.components.contactuschannels;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Contact Us Channel Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Channel {

  /**
   * Channel Name.
   */
  @ValueMapValue
  @Expose
  private String channelName;

  /**
   * Channel Image.
   */
  @ValueMapValue
  @Expose
  private String channelImage;

  /**
   * Channel Image Alt Text.
   */
  @ValueMapValue
  @Expose
  private String channelImageAltText;

  /**
   * Channel Link.
   */
  @ValueMapValue
  @Expose
  private String channelLink;

}
