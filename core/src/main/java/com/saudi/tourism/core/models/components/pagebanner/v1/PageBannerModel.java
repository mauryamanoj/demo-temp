package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.BannerCard;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Page Banner sling model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PageBannerModel {

  /**
   * Mobile image width.
   */
  public static final String MOBILE_IMAGE_WIDTH = "1000";

  /**
   * Desktop image width.
   */
  public static final String DESKTOP_IMAGE_WIDTH = "1920";

  /**
   * Mobile image relative width.
   */
  public static final String MOBILE_IMG_RELATIVE_WIDTH_100VW = "100vw";

  /**
   * Desktop image relative width.
   */
  public static final String DESKTOP_IMG_RELATIVE_WIDTH_100VW = "100vw";


  /**
   * Enable manual authoring.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Expose
  private boolean enableManualAuthoring;

  /**
   * isNotOnTop.
   */
  @ValueMapValue
  @Expose
  private boolean isNotOnTop;

  /**
   * hide image brush.
   */
  @ValueMapValue
  @Expose
  private String hideImageBrush;

  /**
   * Injecting component type value from the component.
   */
  @Expose
  private String type = "HomeBanner";


  /**
   * Cards.
   */
  @ChildResource
  @Expose
  private List<BannerCard> cards;

  /**
   * resourceResolver.
   */
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * currentResource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * Thumbnails.
   */
  @Expose
  private Thumbnails thumbs = new Thumbnails();

  /**
   * DISPLAYED ITEMS BY DEFAULT.
   */
  private static final Integer MAX_DISPLAYED_ITEMS = 3;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  @PostConstruct
  void init() {
    if (!enableManualAuthoring) {
      loadFromContentFragment();
    }

  }

  private void loadFromContentFragment() {
    var pageManager = resourceResolver.adaptTo(PageManager.class);
    var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null || StringUtils.isEmpty(currentPage.getProperties()
        .get("referencedFragmentPath", String.class))) {
      return;
    }

    var cfResource = resourceResolver.getResource(currentPage.getProperties()
        .get("referencedFragmentPath", String.class));
    if (cfResource == null) {
      return;
    }
    var cfModel = cfResource.adaptTo(PageBannerCFModel.class);
    if (cfModel == null) {
      return;
    }


    setFieldsFromContentFragment(cfModel, currentPage);
  }

  private void setFieldsFromContentFragment(PageBannerCFModel cfModel, Page currentPage) {
    hideImageBrush = cfModel.getHideImageBrush();
    cards = cfModel.getCards();

    cards.stream().forEach(card -> {
      Image image = card.getImage();
      Image logo = card.getLogo();
      if (Objects.nonNull(image)) {
        DynamicMediaUtils.setAllImgBreakPointsInfo(
            image, "crop-1920x768", "crop-460x620",
            "1280", "420",
            currentResource.getResourceResolver(),
            currentResource);
      }
      if (Objects.nonNull(logo)) {
        DynamicMediaUtils.setAllImgBreakPointsInfo(
            logo, "crop-1920x768", "crop-460x620",
            "1280", "420",
            currentResource.getResourceResolver(),
            currentResource);
      }
    });

    if (CollectionUtils.isNotEmpty(cfModel.getAssets())) {

      this.thumbs.setGallery(cfModel.getAssets());
      final var language = CommonUtils.getLanguageForPath(currentPage.getPath());
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));

      this.thumbs.getGallery().stream()
          .forEach(
              i -> {
                if (StringUtils.equals("image", i.getType())) {
                  i.setTypeLabel(CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_IMAGE));
                }

                if (StringUtils.equals("video", i.getType())) {
                  i.setTypeLabel(CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_VIDEO));
                }

                if (i.getImage() != null) {

                  DynamicMediaUtils.setAllImgBreakPointsInfo(
                      i.getImage(),
                      getSmartCropRenditionForThumbs(),
                      CropEnum.CROP_1920X1080.getValue(),
                      CropEnum.CROP_760x570.getValue(),
                      BreakPointEnum.DESKTOP.getValue(),
                      BreakPointEnum.MOBILE.getValue());
                }
              });


      var localMoreLabel = StringUtils.EMPTY;
      if (CollectionUtils.isNotEmpty(thumbs.getGallery())
          && CollectionUtils.size(thumbs.getGallery()) > MAX_DISPLAYED_ITEMS) {
        int additionalItems = thumbs.getGallery().size() - MAX_DISPLAYED_ITEMS;
        localMoreLabel =
            "+" + additionalItems + " " + CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_MORE);
      }
      thumbs.setMoreLabel(localMoreLabel);
    }


  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

  private List<SmartCropRenditions> getSmartCropRenditionForThumbs() {

    final var mobileScRendition = new SmartCropRenditions();
    mobileScRendition.setBreakpoint(BreakPointEnum.MOBILE.getValue());
    mobileScRendition.setRendition(CropEnum.CROP_760x570.getValue());
    mobileScRendition.setWidths(MOBILE_IMAGE_WIDTH);
    mobileScRendition.setImgRelativeWidth(MOBILE_IMG_RELATIVE_WIDTH_100VW);
    mobileScRendition.init();

    final var desktopScRendition = new SmartCropRenditions();

    desktopScRendition.setRendition(CropEnum.CROP_1920X1080.getValue());
    desktopScRendition.setWidths(DESKTOP_IMAGE_WIDTH);
    desktopScRendition.setImgRelativeWidth(DESKTOP_IMG_RELATIVE_WIDTH_100VW);
    desktopScRendition.init();

    return Arrays.asList(new SmartCropRenditions[] {mobileScRendition, desktopScRendition});


  }
}
