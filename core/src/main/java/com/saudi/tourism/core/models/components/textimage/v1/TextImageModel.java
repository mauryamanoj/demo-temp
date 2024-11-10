package com.saudi.tourism.core.models.components.textimage.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;


/**
 * Text and Image Component Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class TextImageModel {
  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * white or transparent Background.
   */
  @ValueMapValue
  @Expose
  private Boolean whiteBackground;

  /**
   * Skip additional margin.
   */
  @ValueMapValue
  @Expose
  private Boolean skipMargin;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * Position.
   */
  @ValueMapValue
  @Expose
  private String position;

  /**
   * image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * logo.
   */
  @ChildResource
  @Expose
  private Image logo;

  /**
   * CTA Analytics Data.
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
   * resolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Slide init method.
   */
  @PostConstruct
  public void init() {
    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          image, "crop-1920x1080", "crop-460x620", "1280", "420", resourceResolver, currentResource);
    }
    if (null != logo) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          logo, "crop-1920x1080", "crop-460x620", "1280", "420", resourceResolver, currentResource);
    }
    if (StringUtils.isNotBlank(description)) {
      description = LinkUtils.rewriteLinksInHtml(description, resourceResolver, runModeService.isPublishRunMode());
    }
  }

  /**
   * getJson method for Text and Image component.
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
