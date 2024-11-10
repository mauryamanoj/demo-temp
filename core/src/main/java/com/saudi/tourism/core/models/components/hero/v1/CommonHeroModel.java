package com.saudi.tourism.core.models.components.hero.v1;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.models.components.WeatherWidgetStaticModel;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_BEST_FOR;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;

/**
 * Model used in H-02 Secondary Hero, H-03 Detail Hero and as a list for H-01 Brand Page Hero.
 *
 * @author cbarrios
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
@Slf4j
public class CommonHeroModel implements Serializable  {

  /**
   * Create Itinerary CTA type "link".
   */
  private static final String CTA_TYPE_LINK = "link";

  /**
   * Create Itinerary CTA button event for DataLayer/Analytics.
   */
  private static final String TRIP_PLANNER_CTA_BUTTON_EVENT = "Create Itinerary CTA is clicked";

  /**
   * Current page object.
   */
  @JsonIgnore
  @ScriptVariable
  private transient Page currentPage;

  /**
   * Component title.
   */
  @ValueMapValue
  @Expose
  private String title;
  /**
   * Component title.
   */
  @ValueMapValue
  @Expose
  private String analyticsTitle;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String titleWeight;
  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String enableCustomBranding;

  /**
   * Component subtitle.
   */
  @ValueMapValue
  @Expose
  private String subtitle;

  /**
   * Component subtitleBelowTitle.
   */
  @ValueMapValue
  @Expose
  private String setSubtitleBelow;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String subtitleWeight;

  /**
   * Variable of image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Variable of image.
   */
  @ChildResource
  @Expose
  private Video video;

  /**
   * Component video path.
   */
  @ValueMapValue
  @Expose
  private String videoPath;

  /**
   * Component mobile video path.
   */
  @ValueMapValue
  @Expose
  private String mobileVideoPath;

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * List of Events.
   */
  @ValueMapValue
  @Expose
  private String details;

  /**
   * List of Events.
   */
  @ValueMapValue
  @Expose
  private String readmore;
  /**
   * hide image brush.
   */
  @ValueMapValue
  @Expose
  private String hideImageBrush;

  /**
   * List of Events.
   */
  @ValueMapValue
  @Expose
  private String readless;

  /**
   * Region.
   */
  @ValueMapValue
  @Expose
  private String region;

  /**
   * Region.
   */
  @Expose
  private String cityOfRegion;

  /**
   * City.
   */
  @ValueMapValue
  @Expose
  private String city;

  /**
   * Language for adding to API request in weather-widget.html.
   */
  @Expose
  private String language;

  /**
   * List of 'Best for' labels.
   */
  @Expose
  private List<String> bestForLabels = new LinkedList<>();

  /**
   * Interactive video.
   */
  @ValueMapValue
  @Expose
  private Boolean interactiveVideo;

  /**
   * Play Video link title.
   */
  @ValueMapValue
  @Expose
  private String playVideoTitle;

  /**
   * hide Fav.
   */
  @ValueMapValue
  @Expose
  private boolean hideFav;

  /**
   * Label of Create Itinerary button.
   */
  @ValueMapValue
  @Expose
  private String createItineraryLabel;

  /**
   * Itinerary Path.
   */
  @ValueMapValue
  @Expose
  private String itineraryPath;

  /**
   * Data Layer cta title for the create itinerary button.
   */
  @ValueMapValue
  @Expose
  private String itineraryCtaTitle;

  /**
   * Itinerary Type.
   */
  @ValueMapValue
  @Expose
  private String itineraryType;

  /**
   * Set this to true if CTA must be always expanded.
   */
  @Expose
  private boolean ctaExpanded;

  /**
   * show full Height.
   */
  @ValueMapValue
  @Expose
  private boolean showFullHeight;
  /**
   * base path for weather extended.
   */
  @ValueMapValue
  @Expose
  private String basePath;

  /**
   * Hide CIty Text.
   */
  @ValueMapValue
  @Expose
  private boolean hideCityText;

  /**
   * Image caption (with a ling) to display over the slide.
   */
  @ChildResource
  @Expose
  private ImageCaption caption;

  /**
   * The urlSlingExporter.
   */
  @Expose
  private String urlSlingExporter;

  /**
   * The Favorite Path.
   */
  @Expose
  private String favoritePath;

  /**
   * Save To Favorite Text.
   */
  @Expose
  private String saveToFavoritesText;

  /**
   * Saved To Favorite Text.
   */
  @Expose
  private String savedToFavoritesText;

  /** Servlet endpoint to update favorites. */
  @Expose
  private String updateFavUrl;

  /** Servlet endpoint to delete a favorite. */
  @Expose
  private String deleteFavUrl;

  /** Servlet endpoint to fetch favorites. */
  @Expose
  private String getFavUrl;

  /**
   * The path to the page where this Hero component is displayed.
   */
  @Expose
  private String path;
  /**
   * parameters.
   */
  @Expose
  private String parameters;

  /**
   * App Event Data.
   */
  @Expose
  private String appEventData;

  /**
   * Should be true to show a sticky/fixed button
   * When scrolling the page used by Trip Planer.
   */
  @Expose
  private Boolean hasFixedButton;

  /**
   * Brush ID.
   */
  @ValueMapValue
  @Expose
  private String brushId;

  /**
   * The 'Best For' Text.
   */
  @Expose
  private String bestForText;

  /**
   * season.
   */
  @Expose
  @Setter
  private String season;


  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;
  /**
   * Saudi Tourism Configurations.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SaudiTourismConfigs saudiTourismConfig;
  /**
   * Cities service.
   */
  @JsonIgnore
  @OSGiService
  private transient RegionCityService citiesService;
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
   * locale.
   */
  @Expose
  private String localeCode;
  /** i18n provider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /** Favorites Endpoint Model. */
  @Self
  private FavoritesApiEndpointsModel favoritesApiEndpointsModel;
  /** Weather Widget Model. */
  @Self
  @Expose
  private WeatherWidgetStaticModel weatherWidgetStaticModel;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void initCommonHeroModel() {

    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
          "1280", "420", resourceResolver, currentResource);
    }

    try {
      if (currentPage == null) {
        return;
      }

      boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
      InheritanceValueMap ivm =
           new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
      localeCode = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);
      parameters = "city=" + city + "&locale=" + localeCode;
      path = currentPage.getPath();
      language = CommonUtils.getLanguageForPath(path);
      urlSlingExporter = LinkUtils.getFavoritePath(path);
      favoritePath = LinkUtils.getFavoritePath(path);
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
      basePath = saudiTourismConfig.getWeatherExtendedBasePath();
      if (Objects.nonNull(createItineraryLabel) && Objects.nonNull(itineraryPath)
            && !itineraryPath.equals("#vendor")) {
        itineraryType = "link";
      }
      final boolean ctaIsButton = !CTA_TYPE_LINK.equals(itineraryType);
      if (ctaIsButton) {
        // Clear path just in case to not be confused in html (originally there was {if} in hbs)
        itineraryPath = StringUtils.EMPTY;
      } else if (StringUtils.isNotBlank(itineraryPath)) {
        itineraryPath = LinkUtils.getAuthorPublishUrl(resourceResolver, itineraryPath, isPublish);
      }
      if (Objects.nonNull(link) && StringUtils.isNotBlank(link.getUrl())) {
        link.setUrl(LinkUtils.getAuthorPublishUrl(resourceResolver, link.getUrl(), isPublish));
      }
      final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
      initBestFor(i18nBundle);

      final String dataLayerCtaEventName;
      final Resource pageContentResource = currentPage.getContentResource();
      if (pageContentResource.isResourceType(Constants.PACKAGE_DETAIL_RES_TYPE)) {
        // For all package pages by default type is booking
        dataLayerCtaEventName = Constants.DEFAULT_TOUR_PACKAGE_HERO_EVENT;
        itineraryType = Optional.ofNullable(itineraryType).filter(StringUtils::isNotBlank)
            .orElse("booking");
        itineraryCtaTitle = Optional.ofNullable(itineraryCtaTitle).filter(StringUtils::isNotBlank)
            .orElse("hero-cta-booking");
        // For package booking button it must be in collapsed state by default
        ctaExpanded = false;

      } else if (!ctaIsButton) {
        // In case of we need link
        dataLayerCtaEventName = "Hero Banner CTA Link is clicked";
        if (StringUtils.isBlank(itineraryCtaTitle)) {
          itineraryCtaTitle = "hero-cta-link";
        }
        // For links the button must be also not expanded
        ctaExpanded = false;

      } else {
        // By default button type is planner. Don't need to check if
        // pageContentResource.isResourceType(Constants.RT_TRIP_PLAN_PAGE)
        // because can be displayed not only on trip planner pages.
        dataLayerCtaEventName = TRIP_PLANNER_CTA_BUTTON_EVENT;
        if (StringUtils.isNotBlank(createItineraryLabel)) {
          itineraryType = Optional.ofNullable(itineraryType).filter(StringUtils::isNotBlank)
            .orElse("planner");
          itineraryCtaTitle = Optional.ofNullable(itineraryCtaTitle).filter(StringUtils::isNotBlank)
            .orElse("hero-cta-planner");
        }
        // For trip planner the button should be always expanded
        ctaExpanded = true;
      }

      // analytics data
      String vendorName = CommonUtils.getVendorName(currentPage);
      String packageName = CommonUtils.getPackageName(currentPage);
      appEventData = CommonUtils
          .getAnalyticsEventData(dataLayerCtaEventName, LinkUtils.getAppFormatUrl(itineraryPath),
              itineraryCtaTitle, vendorName, packageName);

      hasFixedButton = StringUtils.isNotBlank(createItineraryLabel);

      //Translations
      saveToFavoritesText = i18nBundle.getString(SAVE_TO_FAV_TEXT);
      savedToFavoritesText = i18nBundle.getString(I18_KEY_SAVED_TO_FAV);
      bestForText = i18nBundle.getString(I18_KEY_BEST_FOR);
    } catch (Exception e) {
      LOGGER.error(" Error in CommonHeroModel", e);

    } finally {
      // Cleanup
      this.currentPage = null;
      this.settingsService = null;
      this.citiesService = null;
    }
  }

  /**
   * Initialize values for bestFor.
   * @param i18nBundle i18nBundle.
   */
  private void initBestFor(ResourceBundle i18nBundle) {
    // Get city categories from the city by city id
    Optional.ofNullable(city).map(cityId -> citiesService.getRegionCityExtById(cityId, language))
        .map(RegionCityExtended::getDestinationFeatureTags).ifPresent(
        //@formatter:off
            destinationTags ->
        //@formatter:on
                destinationTags.stream().map(CategoryTag::getCopy)
                    .collect(Collectors.toCollection(() -> bestForLabels)));

    Optional.ofNullable(region).ifPresent(regionId -> {
      setCityOfRegion(i18nBundle.getString("A city of " + regionId));

      // Get categories from the region if they were not populated from the city
      if (CollectionUtils.isEmpty(bestForLabels)) {
        Optional.ofNullable(citiesService.getRegionCityExtById(regionId, language))
            .map(RegionCityExtended::getDestinationFeatureTags).ifPresent(
            //@formatter:off
              regionDestinationTags ->
            //@formatter:on
                  regionDestinationTags.stream().map(CategoryTag::getCopy)
                      .collect(Collectors.toCollection(() -> bestForLabels)));
      }
    });
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
