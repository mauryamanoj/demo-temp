package com.saudi.tourism.core.models.components.brands;

import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * The type Brand detail.
 */
@SuppressWarnings("squid:S2065")
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class BrandDetail implements Serializable {

  /**
   * Category tags.
   */
  @ValueMapValue
  @Getter
  private String[] category;

  /**
   * Category tags.
   */
  @ValueMapValue
  @Getter
  private String[] city;

  /**
   * cities.
   */
  @Getter
  @Setter
  private List<String> cities;

  /**
   * categories.
   */
  @Getter
  @Setter
  private List<String> categories;

  /**
   * Title.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Thumbnail Image.
   */
  @Getter
  @Setter
  @Expose
  private String thumbnailImage;

  /**
   * The cardImage.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String cardImage;

  /**
   * The Card mobile image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String cardImageMobile;

  /**
   * The Featured image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String featuredImage;

  /**
   * The Feature Brand Image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String featuredBrandImage;

  /**
   * The S7Featured image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  @Named("s7featuredBrandImage")
  private String s7featuredImage;


  /**
   * The Feature Brand partner Image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String featuredPartnerImage;

  /**
   * The S7Featured image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String s7featuredPartnerImage;

  /**
   * The Credits to Earn.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String creditsToEarn;


  /**
   * The Amount to Spend.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String amountToSpend;

  /**
   * The Currency.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String currency;

  /**
   * The howToCollectCredits.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String howToCollectCredits;

  /**
   * The partnerInfo.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String partnerInfo;

  /**
   * The partnerWebsiteLink.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String partnerWebsiteLink;

  /**
   * The isNew.
   */
  @ValueMapValue
  @Getter
  @Expose
  private Boolean isNew;


  /**
   * The isPopular.
   */
  @ValueMapValue
  @Getter
  @Expose
  private Boolean isPopular;

  /**
   * Path of the current Resource.
   */
  @Getter
  @Setter
  @Expose
  private String path;

  /**
   * Short description for list.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String shortDescription;

  /**
   * Description.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String copy;

  /**
   * The ID.
   */
  @Getter
  @Setter
  @Expose
  private String id;

  /**
   * The Rules and Regulation.
   */

  @ValueMapValue
  @Getter
  @Expose
  private String[] rulesAndRegulations;


  /**
   * The Other Location.
   */

  @Getter
  @ChildResource
  @Expose
  private List<Location> locations;

  /**
   * The minimum or closest distance to a partner branch.
   */

  @Getter
  @Setter
  private double minDistance;

  /**
    * The cities.
    */
  @Getter
  @Setter
  @Expose
  private List<AppFilterItem> brandCity;

  /**
   * The category.
   */
  @Getter
  @Setter
  @Expose
  private List<AppFilterItem> brandCategory;

  /**
   * ResourceResolver.
   */
  @JsonIgnore
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * ResourceResolver.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * The Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
      injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Reference of Saudi Tourism Configuration.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Inject SlingSettingsService.
   */
  @Inject
  private transient SaudiModeConfig saudiModeConfig;

  /**
   * Cities service (to get cities by id).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient RegionCityService citiesService;

  /**
   * List of event related paths for the servlet.
   */
  @Getter
  @Setter
  private List<String> relatedPackagesPaths;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

    path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    // language needed for tag translation title
    String language = CommonUtils.getLanguageForPath(path);
    if (Objects.nonNull(saudiModeConfig)) {
      boolean isPublish = false;
      if (saudiModeConfig.getPublish() != null
          && saudiModeConfig.getPublish().equals("true")) {
        isPublish = true;
      }
      path = LinkUtils.getAuthorPublishUrl(resourceResolver, path, isPublish);
      // Create id (with a workaround for home (/en) pages)
      id = AppUtils.stringToID(StringUtils.defaultIfBlank(StringUtils.substringAfter(path,
          Constants.FORWARD_SLASH_CHARACTER + language + Constants.FORWARD_SLASH_CHARACTER),
        SpecialCharConstants.FORWARD_SLASH));
    }
    extractShortDescription();
    setBrandDetailImages();
    //Resolve Category Tag.
    if (Objects.nonNull(i18nProvider) && Objects.nonNull(tagManager)) {
      // update city to i18n value
      Locale localeEn = new Locale((Constants.DEFAULT_LOCALE));
      Locale locale = new Locale(language);
      String[] categoryVal = getCategory();
      categories = CommonUtils.getCategoryFromTagName(categoryVal, resourceResolver, Constants.DEFAULT_LOCALE);
      List<AppFilterItem> categoryList = new ArrayList<>();
      for (String filterItem : category) {
        String filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
        String filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
        AppFilterItem categoryItem = new AppFilterItem(filterId, filterValue);
        categoryList.add(categoryItem);
      }
      setBrandCategory(categoryList);
      categories = CommonUtils.getIds(categories);
      String[] brandLocation = getCity();
      cities = CommonUtils.getCategoryFromTagName(brandLocation, resourceResolver, Constants.DEFAULT_LOCALE);
      List<AppFilterItem> cityList = new ArrayList<>();
      for (String filterItem : city) {
        String filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
        String filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
        AppFilterItem cityItem = new AppFilterItem(filterId, filterValue);
        cityList.add(cityItem);
      }
      setBrandCity(cityList);
      cities = CommonUtils.getIds(cities);
    }
    //Resolve ID tag.
    String brandId = getId();
    String resolveID = CommonUtils.getTagNameFromCategory(brandId, resourceResolver, language);
    if (resolveID != null) {
      setId(resolveID);
    }
    // Cleanup to be able to cache this object
    this.resourceResolver = null;
    this.currentResource = null;
    this.settingsService = null;
    this.i18nProvider = null;
    this.citiesService = null;
  }

  /**
   * Extract short description.
   */
  private void extractShortDescription() {
    if (Objects.nonNull(getCopy())) {
      shortDescription =
          StringUtils.defaultIfBlank(shortDescription, CommonUtils.getFirstSentence(getCopy()));
    }
  }

  /**
   * Set Images for all dimensions.
   */
  private void setBrandDetailImages() {
    if (Objects.nonNull(s7featuredImage) && Objects.nonNull(saudiTourismConfigs)
        && s7featuredImage.startsWith(saudiTourismConfigs.getScene7Domain())
        && !s7featuredImage.contains("scth/ugc")) {
      boolean isCnServer = DynamicMediaUtils.isCnServer(settingsService);
      // add crop if and only if s7banner image start with scene7 domain
      // TODO CHECKME Check please this not existing crop 560 x 314 and fix it.
      setFeaturedImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featuredImage, DynamicMediaUtils.DM_CROP_1920_1080,
              isCnServer));
    } else {
      // If no s7 image use featureImage(dam image)
      if (Objects.nonNull(featuredBrandImage) && featuredBrandImage.contains("scth/ugc")) {
        featuredBrandImage = featuredBrandImage.replace("http://", "https://") + "?scl=1";
      }
      setFeaturedImage(featuredBrandImage);
    }
    if (Objects.nonNull(s7featuredPartnerImage) && Objects.nonNull(saudiTourismConfigs)
        && s7featuredPartnerImage.startsWith(saudiTourismConfigs.getScene7Domain())
        && !s7featuredPartnerImage.contains("scth/ugc")) {
      boolean isCnServer = DynamicMediaUtils.isCnServer(settingsService);
      // add crop if and only if s7banner image start with scene7 domain
      // TODO CHECKME Check please this not existing crop 560 x 314 and fix it.
      setCardImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featuredPartnerImage, "crop-560x314", false, isCnServer));
      // TODO CHECKME Check this not existing crop 315x177 and fix it.
      setCardImageMobile(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featuredPartnerImage, "crop-315x177", false, isCnServer));
      setThumbnailImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featuredPartnerImage, DynamicMediaUtils.DM_CROP_360_480,
          isCnServer));
    } else {
      // If no s7 image use featureImage(dam image)
      if (Objects.nonNull(featuredPartnerImage) && featuredPartnerImage.contains("scth/ugc")) {
        featuredPartnerImage = featuredPartnerImage.replace("http://", "https://") + "?scl=1";
      }
      setCardImage(featuredPartnerImage);
      setCardImageMobile(featuredPartnerImage);
      setThumbnailImage(featuredPartnerImage);
    }
  }
}

