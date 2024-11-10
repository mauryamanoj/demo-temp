package com.saudi.tourism.core.models.components.singlepromobanner.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Model for Single Promo banner.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SinglePromoBannerModel {
  /**
   * Resource resolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceResolver resolver;

  /**
   *  currentResource.
   */
  @SlingObject
  private transient Resource currentResource;
  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image image;
  /**
   * Icons and link to stores.
   */
  @ChildResource
  @Expose
  private List<Link> stores;

  /** init method. */
  @PostConstruct
  protected void init() {
    if (image != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          image, "crop-600x600",
          "crop-600x600",
          "1280",
          "420",
          resolver, currentResource);
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
