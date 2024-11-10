package com.saudi.tourism.core.models.components.evisa.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * C-07 eVisa Model.
 *
 * @author cbarrios
 */

@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class EvisaModel {

  /**
   * ResourceResolver.
   */
  @SlingObject
  @JsonIgnore
  @Getter(AccessLevel.NONE)
  private transient ResourceResolver resourceResolver;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  @Getter(AccessLevel.NONE)
  private transient Resource currentResource;

  /**
   * Main title.
   */
  @ValueMapValue
  @Expose
  private String mainTitle;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Background Image.
   */
  @ChildResource
  @Expose
  private Image backgroundImage;

  /**
   * Apply link.
   */
  @ChildResource
  @Expose
  private Link linkButton;

  @PostConstruct
  void init() {
    // The icon used as 'image' is transparent and very small scene7 is not handling well
    // Decision set 's7fileReference' & 's7fileReference' to avoid the FE fetching image from scene7
    if (image != null) {
      image.setS7fileReference(null);
      image.setS7mobileImageReference(null);
    }

    DynamicMediaUtils.setAllImgBreakPointsInfo(
        backgroundImage,
        CropEnum.CROP_1920X1080.getValue(),
        CropEnum.CROP_375x667.getValue(),
        BreakPointEnum.DESKTOP.getValue(),
        BreakPointEnum.MOBILE.getValue(),
        resourceResolver,
        currentResource);
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
