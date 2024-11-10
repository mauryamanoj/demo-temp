package com.saudi.tourism.core.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class contains the Full Bleed Slider details.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Slide {

  /**
   * Title.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String title;

  /**
   * Hide Favorite.
   */
  @Expose
  @Getter
  @ValueMapValue
  private boolean hideFavorite;

  /**
  * Title.
  */
  @Getter
  @ValueMapValue
  private String labelText;

  /**
   * favoritePath.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String favoritePath;

   /**
    * ornamentId.
    */
  @Getter
  @ValueMapValue
  private String ornamentId;
  /**
   * hideFav icon.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String hideFav;

  /**
   * Analytics title.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String analyticsTitle;

  /**
   * Variable heading weight.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String titleWeight;

  /**
   * Subtitle.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String subtitle;

  /**
   * Variable heading weight.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String subtitleWeight;

  /**
   * Description.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String description;

  /**
   * Slide link.
   */
  @Expose
  @Getter
  @ChildResource
  private Link link;

  /**
   * Variable of image.
   */
  @Expose
  @Getter
  @ChildResource
  private Image image;

  /**
   * Variable of region.
   */
  @Expose
  @Getter
  @Setter
  @ValueMapValue
  private String region;

  /**
   * Hidden slide.
   */
  @Getter
  @ValueMapValue
  private boolean hideSlide;

  /**
   * Label List.
   */
  @Getter
  @ChildResource
  private List<Resource> labelList;

  /**
   * Labels List.
   */
  @Getter
  @Expose
  private List<String> labels = new ArrayList<>();

  /**
   * Object for data-tracking.
   */
  @Expose
  @Getter
  private SliderDataLayer dataTracker;
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
    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
          "1280", "420", resourceResolver, currentResource);
    }

    if (dataTracker == null) {
      dataTracker = new SliderDataLayer();
    }
    dataTracker.setSlideTitle(title);
    dataTracker.setSlideName(analyticsTitle);
    dataTracker.setSlideAsset(image.getFileReference());

    if (link != null && StringUtils.isNotEmpty(link.getUrl())) {
      favoritePath = LinkUtils.getFavoritePath(link.getUrl());
    }

    if (Objects.nonNull(image.getFileReference()) && image.getFileReference()
            .contains("scth/ugc")) {
      image.setFileReference(image.getFileReference()
              .replace("http://", "https://") + "?scl=1");
    }
    if (Objects.nonNull(labelList) && !labelList.isEmpty()) {
      for (Resource labelResource : labelList) {
        labels.add(labelResource.getValueMap().get("label", String.class));
      }
    }
  }
}
