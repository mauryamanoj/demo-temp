package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.models.common.Link;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The type Admin page alert.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AdminPageAlert {

  /**
   * The leftText.
   */
  @ValueMapValue
  private String leftText;
  /**
   * The leftText2.
   */
  @ValueMapValue
  private String leftText2;
  /**
   * The middleText.
   */
  @ValueMapValue
  private String middleText;
  /**
   * The middleTextHash.
   */
  @ValueMapValue
  private String middleTextHash;
  /**
   * The enableAlert.
   */
  @ValueMapValue
  private boolean enableAlert;

  /**
   * The right CTA.
   */
  @ChildResource
  private Link link;

  /**
   * The viewportAlertTitle.
   */
  @ValueMapValue
  private String viewportAlertTitle;
  /**
   * The viewportAlertCopy.
   */
  @ValueMapValue
  private String viewportAlertCopy;

  /**
   * The viewportAlertDesktopTitle.
   */
  @ValueMapValue
  private String viewportAlertDesktopTitle;
  /**
   * The Announcement Banner Desktop Title.
   */
  @ValueMapValue
  private String annBannerDesktopTitle;
  /**
   * The Announcement Banner Url.
   */
  @ValueMapValue
  private String annBannerUrl;
  /**
   * The Announcement Banner Enable Alert.
   */
  @ValueMapValue
  private Boolean enableBannerAlert;
}
