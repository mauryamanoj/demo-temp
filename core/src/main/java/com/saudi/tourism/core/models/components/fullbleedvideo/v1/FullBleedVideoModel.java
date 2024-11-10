package com.saudi.tourism.core.models.components.fullbleedvideo.v1;

import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.PostConstruct;

/**
 * Full Bleed Video Model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Data
public class FullBleedVideoModel {
  /**
   * DESK_TOP_BREAKPOINT.
   */
  public static final String DESK_TOP_BREAKPOINT = "1280";
  /**
   * MOBILE_BREAK_POINT.
   */
  public static final String MOBILE_BREAK_POINT = "420";
  /**
   * Variable to title.
   */
  @ValueMapValue
  @Expose
  private String title;
  /**
   * Variable to videoReference.
   */
  @ValueMapValue
  @Expose
  private String videoReference;
  /**
   * Variable of image.
   */
  @Self
  @Expose
  private Image image;
  /**
   * Variable to videoType.
   */
  @ValueMapValue
  @Expose
  private String videoType;

  /**
   * Variable to youtubeReference.
   */
  @ValueMapValue
  @Expose
  private String youtubeReference;

  /**
   * Variable to alt.
   */
  @ValueMapValue
  @Expose
  private String alt;

  /**
   * Variable to layout.
   */
  @ValueMapValue
  @Expose
  private String layout;

  /**
   * Variable to imageReference.
   */
  @ValueMapValue
  private String imageReference;

  /**
   * Variable to mobileImageReference.
   */
  @ValueMapValue
  private String mobileImageReference;

  /**
   * Variable to s7imageReference.
   */
  @ValueMapValue
  private String s7imageReference;

  /**
   * Variable to s7mobileImageReference.
   */
  @ValueMapValue
  private String s7mobileImageReference;
  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;

  /**
   * image breakpoints.
   */
  @PostConstruct
  protected void init() {
    image.setFileReference(imageReference);
    image.setMobileImageReference(mobileImageReference);
    image.setAlt(alt);
    image.setS7fileReference(s7imageReference);
    image.setS7mobileImageReference(s7mobileImageReference);

    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
          DESK_TOP_BREAKPOINT, MOBILE_BREAK_POINT, resourceResolver, currentResource);
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
