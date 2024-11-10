package com.saudi.tourism.core.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * This class contains the Banner card details.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
public class BannerCard {

  /**
   * Title.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String title;



  /**
   * Title.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String description;

  /**
   * isLogoTitle.
   */
  @Expose
  @Getter
  @ValueMapValue
  private Boolean replaceTitleWithLogo;

  /**
   * isTransparency.
   */
  @Expose
  @Getter
  @ValueMapValue
  private Boolean isTransparency;

  /**
   * Variable of image.
   */
  @Expose
  @Getter
  @ChildResource
  private Image image;

  /**
   * Variable of logo.
   */
  @Expose
  @Getter
  @ChildResource
  private Image logo;

  /**
   * Variable of video.
   */
  @ChildResource
  @Expose
  @Getter
  private Video video;

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
   * Slide init method.
   */
  @PostConstruct
  public void init() {
    if (null != image && Objects.nonNull(resourceResolver)) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x768", "crop-460x620",
          "1280", "420", resourceResolver, currentResource);
    }
  }
}
