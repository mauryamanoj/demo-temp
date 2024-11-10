package com.saudi.tourism.core.models.components.travellerquotes.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * This class contains the Traveller Quotes Slide details.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class QuotesSlide {

  /**
  * Variable quote.
  */
  @Getter
  @Setter
  @ValueMapValue
  @Expose
  private String quote;

  /**
   * Variable quoterName.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String quoterName;

  /**
   * Variable quoterNameWeight.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String quoterNameWeight;

  /**
  * Variable quoterCity.
  */
  @Getter
  @ValueMapValue
  @Expose
  private String quoterCity;

  /**
   * Variable quoterCityWeight.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String quoterCityWeight;

  /**
   * Variable of image.
   */
  @Getter
  @ChildResource
  @Expose
  private Image image;

  /**
   * Image caption.
   */
  @Getter
  @ChildResource
  @Expose
  private ImageCaption caption;

  /**
   * Path reference to audio file.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String audioFile;
  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;
  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;
  /**
   * icon.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String icon;

  @PostConstruct
  protected void init() {
    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
          "1280", "420", resourceResolver, currentResource);
    }
  }
}
