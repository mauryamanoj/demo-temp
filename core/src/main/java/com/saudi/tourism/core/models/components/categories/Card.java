package com.saudi.tourism.core.models.components.categories;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
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
import javax.inject.Inject;
import java.util.Objects;

/**
 * Card  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Card {
  // Injected services and resources
  /** tagManager. */
  @Inject
  private TagManager tagManager;
  /** resourceResolver. */
  @Inject private transient ResourceResolver resourceResolver;
  /** category. */
  @ValueMapValue
  private String category;
  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Card title.
   */
  @Setter
  @Expose
  private String title;

  /**
   * Card image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;

  /**
   * Card icon.
   */
  @Setter
  @Expose
  private String icon;

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
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /** language. */
  private String language = Constants.DEFAULT_LOCALE;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    resourceResolver = currentResource.getResourceResolver();
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    Page currentPage = pageManager.getContainingPage(currentResource);
    if (Objects.nonNull(currentPage)) {
      language = CommonUtils.getLanguageForPath(currentPage.getPath());
    }
    if (Objects.isNull(tagManager)) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
    }
    Tag tag = tagManager.resolve(category);
    if (Objects.nonNull(tag)) {
      Category categoryTag = TagUtils.createCategoryFromTag(tag, language);
      if (categoryTag != null) {
        title = categoryTag.getTitle();
        icon = categoryTag.getIcon();
      }
    }

    String vendorName = CommonUtils.getVendorName(currentPage);
    String packageName = CommonUtils.getPackageName(currentPage);
    link.setAppEventData(CommonUtils.getAnalyticsEventData(
        appEventData, LinkUtils.getAppFormatUrl(link.getUrl()),
        StringUtils.defaultIfBlank(link.getTitle(), link.getCopy()), vendorName, packageName));
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-660x337", "crop-375x280",
        "1280", "420", currentResource.getResourceResolver(), currentResource);
  }

}
