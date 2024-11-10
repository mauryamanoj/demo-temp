package com.saudi.tourism.core.models.components.promotional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.ImageRelativeWidthEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PromotionalBanner Model.
 */
@Model(
    adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PromotionalBannerModel {

  /**
   * Resource resolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceResolver resolver;

  /**
   * currentResource.
   */
  @SlingObject
  private transient Resource currentResource;

  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /**
   * type.
   */
  @Default(values = "Story")
  @ValueMapValue
  @Expose
  private String type;

  /**
   * height.
   */
  @ValueMapValue
  @Expose
  private String height;

  /**
   * isNotOnTop.
   */
  @ValueMapValue
  @Expose
  private boolean isNotOnTop;

  /**
   * Show Arrows.
   */
  @ValueMapValue
  @Expose
  private Boolean showArrows;

  /**
   * Select Logo Size.
   */
  @ValueMapValue
  @Expose
  private String logoSize;

  /**
   * isHideImageBruch.
   */
  @ValueMapValue
  @Expose
  private Boolean hideImageBrush;

  /**
   * promotionalSections.
   */
  @ChildResource
  @Setter
  @Expose
  private List<PromotionalSectionBannerModel> cards;

  /**
   * Logo Image.
   */
  @ChildResource
  @Expose
  private Image logo;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    if (logo != null) {
      if (logoSize != null) {
        if (logoSize.equals("small")) {
          DynamicMediaUtils.setAllImgBreakPointsInfo(logo, CropEnum.CROP_469x264.getValue(),
              CropEnum.CROP_469x264.getValue(),
              "469", "469", resolver, currentResource);
        } else {
          DynamicMediaUtils.setAllImgBreakPointsInfo(logo, CropEnum.CROP_1920x768.getValue(),
              CropEnum.CROP_1920x768.getValue(),
              "1280", "420", resolver, currentResource);
        }
      }
    }

    if (CollectionUtils.isNotEmpty(cards)) {
      List<SmartCropRenditions> smartCropRenditions = new ArrayList<>();
      for (PromotionalSectionBannerModel banner : cards) {
        Image image = banner.getImage();
        if (image != null) {
          if (height.equals("large")) {
            smartCropRenditions = buildSmartCropsRenditions("460", "1920",
              CropEnum.CROP_460x620.getValue(), CropEnum.CROP_1920x768.getValue());
            DynamicMediaUtils.setAllImgBreakPointsInfo(
                image,
                smartCropRenditions,
                CropEnum.CROP_1920x768.getValue(),
                CropEnum.CROP_460x620.getValue(),
                BreakPointEnum.DESKTOP.getValue(),
                BreakPointEnum.MOBILE.getValue());
          } else if (height.equals("medium")) {
            smartCropRenditions = buildSmartCropsRenditions("460", "1920",
              CropEnum.CROP_460x620.getValue(), CropEnum.CROP_1920x768.getValue());
            DynamicMediaUtils.setAllImgBreakPointsInfo(
                image,
                smartCropRenditions,
                CropEnum.CROP_1920x768.getValue(),
                CropEnum.CROP_460x620.getValue(),
                BreakPointEnum.DESKTOP.getValue(),
                BreakPointEnum.MOBILE.getValue());
          } else {
            smartCropRenditions = buildSmartCropsRenditions("460", "660",
              CropEnum.CROP_460x620.getValue(), CropEnum.CROP_660x337.getValue());
            DynamicMediaUtils.setAllImgBreakPointsInfo(
                image,
                smartCropRenditions,
                CropEnum.CROP_660x337.getValue(),
                CropEnum.CROP_460x620.getValue(),
                BreakPointEnum.DESKTOP.getValue(),
                BreakPointEnum.MOBILE.getValue());
          }
        }
        banner.setImage(image);
      }
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

  private List<SmartCropRenditions> buildSmartCropsRenditions(String widthsMobile, String widthsDesktop,
                                                              String cropMobile, String cropDesktop) {

    SmartCropRenditions mobileScRendition = null;
    SmartCropRenditions desktopScRendition = null;
    mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
    mobileScRendition.setRendition(cropMobile);
    mobileScRendition.setWidths(widthsMobile);
    mobileScRendition.setImgRelativeWidth(ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
    mobileScRendition.init();

    desktopScRendition = new SmartCropRenditions();

    desktopScRendition.setRendition(cropDesktop);
    desktopScRendition.setWidths(widthsDesktop);
    desktopScRendition.setImgRelativeWidth(
        ImageRelativeWidthEnum.RELATIVE_WIDTH_100VW.getValue());
    desktopScRendition.init();

    return Arrays.asList(new SmartCropRenditions[]{mobileScRendition, desktopScRendition});
  }
}
