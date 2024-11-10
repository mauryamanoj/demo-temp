package com.saudi.tourism.core.models.components.essentiallinks;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.OrnamentEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.PACKAGE_DETAIL_RES_TYPE;
import static com.saudi.tourism.core.utils.SaudiConstants.DEFAULT_CSS_MARGIN_CLASS;

/**
 * Essential Links Component Model.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class EssentialLinksModel implements Serializable {

  /**
   * Variant: grid-small.
   */
  public static final String VARIANT_GRID_SMALL = "grid-small";

  /**
   * Variant: grid-big.
   */
  public static final String VARIANT_GRID_BIG = "grid-big";
  /**
   * Title.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String title;

  /**
   * SubTitle.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String subTitle;

  /**
   * Banner variant.
   */
  @ValueMapValue
  @Expose
  private String variant;

  /**
   * css margin class.
   */
  @ValueMapValue
  @Expose
  private String cssMarginClass;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;

  /**
   * Apply link.
   */
  @ChildResource
  @Setter
  @Expose
  private Link linkButton;

  /**
   * hideMiddleOrnament.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String hideMiddleOrnament;

  /**
   * Ornament ID.
   */
  @Expose
  private String ornamentId;

  /**
   * Resource Object.
   */
  @SlingObject
  private transient Resource resource;



  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    if (Objects.nonNull(i18nProvider)) {
      String language = CommonUtils
              .getPageNameByIndex(resource.getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

      PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
      Page page = pageManager.getContainingPage(resource);
      if (Objects.nonNull(page)
          && page.getContentResource().isResourceType(PACKAGE_DETAIL_RES_TYPE)) {
        initForPackageDetailPage(i18nBundle, language);
      } else if (Objects.nonNull(this.linkButton)) {
        if ("whatsapp".equals(linkButton.getType())
            && Objects.nonNull(linkButton.getUrlWithExtension())) {
          linkButton.setUrlWithExtension("https://wa.me/" + linkButton.getUrl());
          linkButton.setUrl("https://wa.me/" + linkButton.getUrl());
        }
      }
    }

    // To handle old authored components with no cssMarginClass
    if (StringUtils.isEmpty(cssMarginClass)) {
      cssMarginClass = DEFAULT_CSS_MARGIN_CLASS;
    }

    // Handle ornament
    if (VARIANT_GRID_SMALL.equals(variant)) {
      ornamentId = OrnamentEnum.ORNAMENT_03A.getValue();
    }
    if (VARIANT_GRID_BIG.equals(variant)) {
      ornamentId = OrnamentEnum.ORNAMENT_07A.getValue();
    }

    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-660x337", "crop-375x280",
        "1280", "420", currentResource.getResourceResolver(), currentResource);
  }

  /**
   * Initialize banner for package-detail pages.
   * @param i18nBundle i18nBundle
   * @param language language
   */
  private void initForPackageDetailPage(ResourceBundle i18nBundle, String language) {
    PackagePageSettings settings = AdminUtil.getPackagePageSettings(language);
    title = i18nBundle.getString("Explore the Arabian winter");
    subTitle = CommonUtils.getI18nString(i18nBundle,
        "Discover the best destinations and activities");
    variant = "full-width";

    Link linkButtonItem = new Link();
    linkButtonItem.setCopy(i18nBundle.getString("Discover more"));
    linkButtonItem.setUrlWithExtension(LinkUtils.getUrlWithExtension(
        settings.getWinterCampaignPath()));
    linkButtonItem.setType(Constants.TYPE_INTERNAL);
    linkButtonItem.setTargetInNewWindow(false);
    setLinkButton(linkButtonItem);

    Image imageItem = new Image();
    imageItem.setFileReference(settings.getDesktopImage());
    imageItem.setMobileImageReference(settings.getMobileImage());
    setImage(imageItem);
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
