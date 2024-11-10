package com.saudi.tourism.core.models.components.promotional;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
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
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * PromotionalSection Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PromotionalSectionBannerModel extends PromotionalSectionModel {


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
   * Variable of video.
   */
  @ChildResource
  @Expose
  @Getter
  private Video video;

  /**
   * Thumbnail.
   */
  @ChildResource
  @Setter
  @Expose
  private Image thumbnail;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    // Get the best image to be displayed (s7 image or dam image if s7 is empty)
    super.init();
    PageManager pageManager = getResolver().adaptTo(PageManager.class);
    Page currentPage = pageManager.getContainingPage(getCurrentResource());
    String vendorName = CommonUtils.getVendorName(currentPage);
    String packageName = CommonUtils.getPackageName(currentPage);
    link.setAppEventData(CommonUtils.getAnalyticsEventData(
        appEventData, LinkUtils.getAppFormatUrl(link.getUrl()),
        StringUtils.defaultIfBlank(link.getTitle(), link.getCopy()), vendorName, packageName));
    if (video != null) {
      video.setPoster(thumbnail.getFileReference());
    }
    if (thumbnail != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(thumbnail, "crop-600x600", "crop-460x620",
          "1280", "420", getResolver(), getCurrentResource());
    }
  }

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
