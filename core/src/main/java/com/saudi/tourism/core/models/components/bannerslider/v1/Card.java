package com.saudi.tourism.core.models.components.bannerslider.v1;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Card  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Card {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Card Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Card tabTitle.
   */
  @ValueMapValue
  @Expose
  private String tabTitle;

  /**
   * Card description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;

  /**
   * Variable of video.
   */
  @ChildResource
  @Expose
  @Getter
  private Video video;

  /**
   * link.
   */
  @ChildResource
  @Setter
  @Expose
  private Link link;

  /**
   * Card appEventData.
   */
  @ValueMapValue
  @Setter
  private String appEventData;

  /**
   * CTA Data.
   */
  @ChildResource
  @Expose
  private CtaData ctaData;

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
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    Page currentPage = pageManager.getContainingPage(currentResource);
    String vendorName = CommonUtils.getVendorName(currentPage);
    String packageName = CommonUtils.getPackageName(currentPage);
    link.setAppEventData(CommonUtils.getAnalyticsEventData(
          appEventData, LinkUtils.getAppFormatUrl(link.getUrl()),
          StringUtils.defaultIfBlank(link.getTitle(), link.getCopy()), vendorName, packageName));
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-660x337", "crop-375x280",
          "1280", "420", currentResource.getResourceResolver(), currentResource);

  }
}
