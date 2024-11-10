package com.saudi.tourism.core.models.components.destinationguide;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Item  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
@Slf4j
public class Item {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * title.
   */
  @Expose
  private String title;

  /**
   * Item Title.
   */
  @ValueMapValue
  private String itemTitle;

  /**
   * Weather Title.
   */
  @ValueMapValue
  private String weatherTitle;

  /**
   * Item Sub Title.
   */
  @ValueMapValue
  @Expose
  private String subTitle;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  @Optional
  private Image logo;

  /**
   * Link.
   */
  @ChildResource
  @Getter
  @Expose
  @Optional
  private Link link;

  /**
   * Item low Temp.
   */
  @ValueMapValue
  @Expose
  private Double lowTemp;

  /**
   * Item low Temp Icon.
   */
  @ValueMapValue
  @Expose
  private String lowTempIcon;

  /**
   * Item high Temp.
   */
  @ValueMapValue
  @Expose
  private Double highTemp;

  /**
   * Item high Temp Icon.
   */
  @ValueMapValue
  @Expose
  private String highTempIcon;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    if (logo != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(logo, "crop-600x600", "crop-600x600",
          "1280", "420", currentResource.getResourceResolver(), currentResource);

    }
    if (link != null) {
      link.setUrl(LinkUtils.getAuthorPublishUrl(resourceResolver, link.getUrl(),
           settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    }
  }
}
