package com.saudi.tourism.core.models.components.packages;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.app.location.InfoItemModel;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

/**
 * The Class PackageDetail.
 */
@Model(adaptables = Resource.class,
       resourceType = {Constants.PACKAGE_DETAIL_RES_TYPE},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "search",
          name = "jackson",
          extensions = "json",
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@Slf4j
public class PackageDetail extends PackageBase {

  /**
   * The category tags from author.
   */
  @ValueMapValue
  @Getter
  private String[] packageCategoryTags;

  /**
   * List for categories.
   */
  @Getter
  private List<String> categories;

  /**
   * The category for Json.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> category;

  /**
   * The target tag from author.
   */
  @ValueMapValue
  @Getter
  private String[] packageTargetTags;

  /**
   * List of targets.
   */
  @Getter
  private List<String> targets;

  /**
   * The target for Json.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> target;

  /**
   * The area tag from author.
   */
  @ValueMapValue
  @Getter
  private String[] packageAreaTags;

  /**
   * List for areas.
   */
  @Getter
  private List<String> areas;

  /**
   * List for cities.
   */
  @Getter
  private List<String> cities;

  /**
   * The area for Json.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> area;

  /**
   * The price.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String price;

  /**
   * The Booking page url for APP.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String bookNow;

  /**
   * The price display.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String priceDisplay;
  /**
   * The budget tag from author.
   */
  @ValueMapValue
  @Getter
  private String budgetTag;

  /**
   * The budget for Json.
   */
  @Getter
  @Setter
  @Expose
  private AppFilterItem budget;

  /**
   * The duration from author.
   */
  @ValueMapValue
  @Getter
  private String durationAuth;

  /**
   * The duration for Json.
   */
  @Setter
  @Getter
  @Expose
  private AppFilterItem duration;

  /**
   * The card image.
   */
  @ValueMapValue
  @Getter
  @Expose
  @Setter
  private String cardImage;

  /**
   * The card image caption.
   */
  @ChildResource
  @Getter
  @Expose
  private ImageCaption cardImageCaption;

  /**
   * The feature image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String featureImage;

  /**
   * The bannerImage.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String bannerImage;

  /**
   * The Dynamic Media bannerImage.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String s7bannerImage;

  /**
   * The banner image caption.
   */
  @ChildResource
  @Getter
  @Expose
  private ImageCaption bannerImageCaption;

  /**
   * The Slider image.
   */
  @ValueMapValue
  @Getter
  @Expose
  @Setter
  private String sliderImage;

  /**
   * The Slider image.
   */
  @ChildResource
  @Getter
  @Expose
  private ImageCaption sliderImageCaption;

  /**
   * The Alt image.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String altImage;

  /**
   * Short description for list.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String shortDescription;

  /**
   * The id.
   */
  @Getter
  @Expose
  private String id;

  /**
   * The path.
   */
  @Getter
  @Expose
  private String path;


  /**
   * The link used in Favorites linking.
   */
  @Getter
  @Expose
  private String link;

  /**
   * The favId.
   */
  @Setter
  @Getter
  @Expose
  private String favId;

  /**
   * The favCategory.
   */
  @Setter
  @Getter
  @Expose
  private String favCategory;

  /**
   * The ctaText.
   */
  @Getter
  @Setter
  @Expose
  private String ctaText;

  /**
   * The detailAppUrl.
   */
  @Getter
  @Expose
  private String detailAppUrl;

  /**
   * The title.
   */
  @ValueMapValue(name = JCR_TITLE)
  @Getter
  @Expose
  private String title;
  /**
   * The dmc.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String dmc;

  /**
   * The copy.
   */
  @ValueMapValue(injectionStrategy = InjectionStrategy.REQUIRED)
  @Getter
  @Expose
  private String copy;

  /**
   * The subtitle.
   */
  @Setter
  @Getter
  @Expose
  private String subtitle;

  /**
   * The areasListString.
   */
  @Getter
  @Expose
  private String areasEn;

  /**
   * List of event related paths for the servlet.
   */
  @Getter
  @Setter
  private List<String> relatedPackagesPaths;

  /**
   * All info authored items for this page.
   */
  @ValueMapValue
  @Getter
  private String infoInitialItems;

  /**
   * vendor disclaimer.
   */
  @Expose
  @Getter
  private String vendorDisclaimer;

  /**
   * infoItems for json.
   */
  @Expose
  @Setter
  private List<InfoItemModel> infoItems;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * Inject SlingSettingsService.
   */
  @Inject
  private transient SlingSettingsService settingsService;

  /**
   * Reference of Saudi Tourism Configuration.
   */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    try {
      String language = CommonUtils.getLanguageForPath(path);

      String shortDesc =
          StringUtils.defaultIfBlank(shortDescription, CommonUtils.getFirstSentence(copy));
      setShortDescription(shortDesc);
      setSubtitle(shortDesc);
      setPackageDetailImages();
      setFavId(LinkUtils.getFavoritePath(path));
      favCategory = SearchUtils.getFavCategory(currentResource);

      if (Objects.nonNull(settingsService)) {
        detailAppUrl = LinkUtils.getFavoritePath(path);
        bookNow = LinkUtils.getAppFormatUrl(bookNow);
        path = LinkUtils.transformUrl(path,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
        link = path;
        // Create id (with a workaround for home (/en) pages)
        id = AppUtils.stringToID(StringUtils.defaultIfBlank(StringUtils.substringAfter(path,
            Constants.FORWARD_SLASH_CHARACTER + language + Constants.FORWARD_SLASH_CHARACTER),
            SpecialCharConstants.FORWARD_SLASH));
      }
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

        setLead(buildLead(language, i18nBundle));

        vendorDisclaimer = i18nBundle.getString("package-booking-disclaimer");

        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Locale locale = new Locale(language);
        Locale localeEn = new Locale((Constants.DEFAULT_LOCALE));
        String filterId;
        String filterValue;

        //Categories
        String[] categoryTags = getPackageCategoryTags();
        categories = CommonUtils.getIds(CommonUtils
            .getCategoryFromTagName(categoryTags, resourceResolver, Constants.DEFAULT_LOCALE));
        List<AppFilterItem> categoryList = new ArrayList<>();
        for (String filterItem : categoryTags) {
          filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
          filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
          AppFilterItem categoryItem = new AppFilterItem(filterId, filterValue);
          categoryList.add(categoryItem);
        }
        setCategory(categoryList);

        //Targets
        String[] targetTags = getPackageTargetTags();
        targets = CommonUtils.getIds(CommonUtils
            .getCategoryFromTagName(targetTags, resourceResolver, Constants.DEFAULT_LOCALE));
        List<AppFilterItem> targetList = new ArrayList<>();
        for (String filterItem : targetTags) {
          filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
          filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
          AppFilterItem targetItem = new AppFilterItem(filterId, filterValue);
          targetList.add(targetItem);
        }
        setTarget(targetList);

        //Areas
        String[] areaTags = getPackageAreaTags();
        areasEn = String.join(",", CommonUtils
            .getCategoryFromTagName(areaTags, resourceResolver, Constants.DEFAULT_LOCALE));

        areas = CommonUtils.getIds(CommonUtils
            .getCategoryFromTagName(areaTags, resourceResolver, Constants.DEFAULT_LOCALE));
        List<AppFilterItem> areaList = new ArrayList<>();
        for (String filterItem : areaTags) {
          filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
          filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
          AppFilterItem areaItem = new AppFilterItem(filterId, filterValue);
          areaList.add(areaItem);
        }
        setArea(areaList);

        //cities
        cities = new ArrayList<>();
        cities =
            Arrays.stream(areaTags).map(areaTag -> resourceResolver.getResource(
                Constants.TAGS_URL + areaTag
                  .replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER)))
            .filter(Objects::nonNull)
            .map(Resource::getValueMap)
            .map(vm -> vm.get("city", String.class))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());

        //Budgets
        AppFilterItem budgetItem =
            new AppFilterItem(budgetTag, CommonUtils.getI18nString(i18nBundle, budgetTag));
        setBudget(budgetItem);
        budgetTag = budgetItem.getId();
        //Durations
        duration =
            new AppFilterItem(durationAuth, CommonUtils.getI18nString(i18nBundle, durationAuth));
        durationAuth = duration.getId();

        //Price Display, CTA Text
        if (Objects.nonNull(price) && !price.isEmpty()) {
          priceDisplay = StringUtils
              .replaceEach(CommonUtils.getI18nString(i18nBundle, Constants.STARTING_FROM_I18KEY),
                  new String[] {"{0}"}, new String[] {price});
          ctaText = StringUtils
                  .replaceEach(CommonUtils.getI18nString(i18nBundle, Constants.FROM_SAR_I18KEY),
                          new String[] {"{0}"}, new String[] {price});
          String priceReplaced = price.replaceAll("\\D+", "");
          setPrice(priceReplaced);
        }

        // {key1:value1,key2:value2} will be processed here from infoInitialItems
        if (Objects.nonNull(infoInitialItems)) {
          List<String> items = Arrays.asList(infoInitialItems.split(","));
          List<InfoItemModel> infoItem = items.stream().
                  filter(i -> i.contains(":")).
                  map(i -> i.split(":")).
                  map(arr -> new InfoItemModel(arr[0], arr[1]))
                  .collect(Collectors.toList());
          setInfoItems(infoItem);
        }

        altImage = Optional.ofNullable(altImage).orElse(title);

        baseInit(i18nBundle, currentResource);
      }
    } catch (Exception e) {
      LOGGER.error("Error in PackageDetail ", e);
    }
  }

  /**
   * Set Image sets for package details.
   */
  private void setPackageDetailImages() {
    if (Objects.nonNull(s7bannerImage) && Objects.nonNull(saudiTourismConfigs) && s7bannerImage
            .startsWith(saudiTourismConfigs.getScene7Domain())) {
      boolean isCnServer = DynamicMediaUtils.isCnServer(settingsService);
      // add crop if and only if s7banner image start with scene7 domain
      setCardImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7bannerImage, DynamicMediaUtils.DM_CROP_667_375,
              isCnServer));
      setSliderImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7bannerImage, DynamicMediaUtils.DM_CROP_360_480,
              isCnServer));
      setFeatureImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7bannerImage, DynamicMediaUtils.DM_CROP_360_480,
              isCnServer));
      setBannerImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7bannerImage, DynamicMediaUtils.DM_CROP_1920_1080,
              isCnServer));
    } else {
      if (Objects.nonNull(bannerImage) && bannerImage.contains("scth/ugc")) {
        bannerImage = bannerImage.replace("http://", "https://") + "?scl=1";
      }
      // If no s7 image use individual images or fallback to bannerImage(dam image)
      setCardImage(StringUtils.defaultIfBlank(cardImage, bannerImage));
      setFeatureImage(StringUtils.defaultIfBlank(featureImage, bannerImage));
      setSliderImage(StringUtils.defaultIfBlank(sliderImage, bannerImage));
    }
  }

  /**
   * Base init.
   * @param i18nBundle i18nBundle.
   * @param resource resource.
   */
  private void baseInit(ResourceBundle i18nBundle, Resource resource) {
    List<PackageInfo> additionalInformation = updatePackageInfoItems();

    setAdditionalInformation(additionalInformation);

    setDays(getPackageDaysModels().getDays());

    VendorList vendor = new VendorList();
    vendor.setVendorTitle(CommonUtils.getI18nString(i18nBundle, Constants.ZAHID_TRAVEL_GROUP));

    VendorLink vendorLink = new VendorLink();
    String vendorItem = CommonUtils
            .getPageNameByIndex(resource.getPath(), Constants.AEM_PACKAGES_DMC_PATH_POSITION);
    vendorLink.setTitle(i18nBundle.getString(vendorItem));
    vendorLink.setUrl(LinkUtils.getAppFormatUrl(
        Objects.requireNonNull(resource.getResourceResolver().
            adaptTo(PageManager.class)).getContainingPage(resource).getPath()));

    vendor.setItems(vendorLink);
    setVendors(vendor);
  }

  /**
   * Build lead string.
   *
   * @param language   the language
   * @param i18nBundle the i18nBundle
   * @return the string
   */
  private LeadFormSuccessError buildLead(final String language, final ResourceBundle i18nBundle) {

    dmc = CommonUtils.readDmcFromPath(dmc, path);
    Map<String, String> vendorPlaceholders = new HashMap<>();
    vendorPlaceholders.put("dmc", i18nBundle.getString(dmc));
    vendorPlaceholders.put("dmc-link", i18nBundle.getString(dmc + "-link"));

    LeadFormSuccessError leadFormSuccessError = null;
    String configResourcePath = StringUtils.replaceEach(Constants.ADMIN_PACKAGE_FORM_CONFIG_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language});

    final Resource configResource = resourceResolver.getResource(configResourcePath);
    if (configResource == null) {
      LOGGER.error("Package form config resource not found {}", configResourcePath);
    } else {
      @Nullable PackagesSignupFormConfig config =
          configResource.adaptTo(PackagesSignupFormConfig.class);

      if (config != null) {

        leadFormSuccessError = config.getLead();
        leadFormSuccessError.getSuccess().setCopy(AdminUtil
            .replaceVendorPlaceholders(leadFormSuccessError.getSuccess().getCopy(),
                vendorPlaceholders));
        leadFormSuccessError.getError().setCopy(AdminUtil
            .replaceVendorPlaceholders(leadFormSuccessError.getError().getCopy(),
                vendorPlaceholders));
      }
    }
    return leadFormSuccessError;
  }


  /**
   * Update package info items list.
   *
   * @return the list
   */
  @NotNull private List<PackageInfo> updatePackageInfoItems() {
    List<PackageInfo> additionalInformation = new ArrayList<>();
    if (!getImportantInformation().getDetails().isEmpty()) {
      additionalInformation.add(getImportantInformation());
    }
    if (!getPackageExclude().getDetails().isEmpty()) {
      additionalInformation.add(getPackageExclude());
    }
    if (!getPackageInclude().getDetails().isEmpty()) {
      additionalInformation.add(getPackageInclude());
    }
    if (!getPriceInfo().getDetails().isEmpty()) {
      additionalInformation.add(getPriceInfo());
    }

    return additionalInformation;
  }

}
