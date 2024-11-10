package com.saudi.tourism.core.models.components.articlecard.v1;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;

import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * C-13 Article Card Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ArticleCardModel {
  /**
   * Sling Settings Service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Variable of image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Variable of title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Title Weight.
   */
  @ValueMapValue
  @Expose
  private String titleWeight;

  /**
   * Variable of description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Image caption.
   */
  @ChildResource
  @Expose
  private ImageCaption caption;
  /**
   *
   * Resource resolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  @JsonIgnore
  private transient ResourceResolver resolver;
  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  @PostConstruct
  private void init() {
    // Get the best image to be displayed (s7 image or dam image if s7 is empty)
    if (image != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, DynamicMediaUtils.DM_CROP_1160_650,
          DynamicMediaUtils.DM_CROP_375_280, "1280", "420", resolver, currentResource);
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
