package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Slider model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SliderModel {
  /**
   * Winter Slider Card id.
   */
  @ValueMapValue
  @Expose
  private String id;

  /**
   * Winter Slider Card Title.
   */
  @ValueMapValue
  @Expose
  private String title;
  /**
   * Winter Slider Card Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Winter Slider Card Icon.
   */
  @ValueMapValue
  @Expose
  private String icon;
  /**
   * Winter Slider Card show/hide Icon.
   */
  @ValueMapValue
  @Expose
  private String showIcon;
  /**
   * Winter Slider Card CTA.
   */
  @ValueMapValue
  @Expose
  private String sliderCta;

  /**
   * Winter Slider Card Url.
   */
  @ValueMapValue
  @Expose
  private String cardUrl;

  /**
   * Winter Slider Card Image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Winter Slider Card CTA Analytics.
   */
  @ValueMapValue
  @Expose
  private String sliderAnalyticsCtaText;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * resolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * setting service.
   */
  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  @PostConstruct
  protected void init() {
    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
          "1280", "420", resolver, currentResource);
    }
    cardUrl = LinkUtils.getAuthorPublishUrl(resolver, cardUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}
