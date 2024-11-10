package com.saudi.tourism.core.models.components.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.common.SliderDataLayer;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;


/**
 * This Class contains SimpleSlider details.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class SimpleSliderModel {

  /**
   * Variable to hold component title.
   */
  @Expose
  @Getter
  @ChildResource
  private ComponentHeading componentHeading;

  /**
   * Variable to hold list of slides.
   */
  @Expose
  @Getter
  @ChildResource
  private List<Slide> slides;

  /**
   * Slides List size.
   */
  @Expose
  @Getter
  private int size;

  /**
   * Component title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Component Hide title.
   */
  @Expose
  @ValueMapValue
  private boolean hideTitle;

  /**
   * Background Color.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String backgroundColor;

  /**
   * Ornament Bar ID.
   */
  @Expose
  @Getter
  @ValueMapValue
  private String ornamentBarId;

  /**
   * The package Service.
   */
  @OSGiService
  private PackageService packageService;

  /**
   * The SlingSettings Service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * The Resource resolver.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * Component title.
   */
  @Expose
  @Getter
  private List<PackageDetail> packageDetails;

  /**
   * The Current resource.
   */
  @SlingObject
  @Named("currentResource")
  private Resource currentResource;

  /** Favorites Endpoint Model. */
  @Self
  private FavoritesApiEndpointsModel favoritesApiEndpointsModel;

  /** Servlet endpoint to update favorites. */
  @Expose
  @Getter
  private String updateFavUrl;

  /** Servlet endpoint to delete a favorite. */
  @Expose
  @Getter
  private String deleteFavUrl;

  /** Servlet endpoint to fetch favorites. */
  @Expose
  @Getter
  private String getFavUrl;

  /** i18n provider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

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


  /**
   * Initialize the properties.
   */
  @PostConstruct
  private void init() {
    try {
      final String language = CommonUtils.getLanguageForPath(currentResource.getPath());
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();

      if  (Objects.nonNull(i18nProvider)) {
        final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        saveToFavoritesText = i18nBundle.getString(SAVE_TO_FAV_TEXT);
        savedToFavoritesText = i18nBundle.getString(I18_KEY_SAVED_TO_FAV);
      }

      if (slides == null && currentResource != null) {
        PackageDetail packageDetail = currentResource.adaptTo(PackageDetail.class);
        this.packageDetails =
          packageService.getRelatedPackages(request, language, Boolean.FALSE, packageDetail);
        if (this.packageDetails.isEmpty()) {
          PackagesRequestParams packagesRequestParams = new PackagesRequestParams();
          packagesRequestParams.setLocale(language);
          packagesRequestParams.setPath(packageDetail.getPath());
          packagesRequestParams.setLimit(Constants.EVENTS_SLIDER_COUNT);
          this.packageDetails =
            packageService.getFilteredPackages(packagesRequestParams).getData();
        }
      }

      // Translate regions in slides as they might be broken after we changed them to dropdowns
      translatingRegionNames(language);

      fillDataTrackerData();
    } catch (Exception ex) {
      LOGGER.error(" Error in EventSliderModel", ex);
    }

    if (CollectionUtils.isNotEmpty(slides)) {
      size = slides.size();
    }
  }

  /**
   * Translating region names.
   *
   * @param language language
   */
  private void translatingRegionNames(String language) {
    if (CollectionUtils.isNotEmpty(slides) && request != null) {
      final ResourceBundle i18n = request.getResourceBundle(new Locale(language));
      if (i18n != null) {
        final Set<String> i18nKeys = i18n.keySet();
        for (Slide slide : slides) {
          final String slideRegion = slide.getRegion();
          if (StringUtils.isBlank(slideRegion)) {
            continue;
          }

          String key = Optional.ofNullable(slideRegion).filter(i18nKeys::contains)
              .orElse(AppUtils.stringToID(slideRegion));
          if (i18nKeys.contains(key)) {
            slide.setRegion(i18n.getString(key));
          }
        }
      }
    }
  }

  /**
   * Fill slide's data tracker object.
   */
  private void fillDataTrackerData() {

    slides = slides.stream().filter(slide -> !slide.isHideSlide()).collect(Collectors.toList());

    if (CollectionUtils.isEmpty(slides)) {
      return;
    }

    final AtomicInteger atomicInteger = new AtomicInteger(0);
    slides.stream()
        .forEach(s -> {
          // Calculation opf desktop and mobile images
          DynamicMediaUtils.prepareDMImages(s.getImage(), DynamicMediaUtils.DM_CROP_360_480,
              DynamicMediaUtils.DM_CROP_360_480, false, DynamicMediaUtils.isCnServer(settingsService));

          // Calculation of DataTracker Object
          final SliderDataLayer dataTracker = s.getDataTracker();

          if (componentHeading != null && componentHeading.getHeading() != null) {
            dataTracker.setCarouselTitle(componentHeading.getHeading().getText());
            dataTracker.setCarouselName(componentHeading.getHeading().getAnalyticsText());
          } else {
            dataTracker.setCarouselTitle(StringUtils.EMPTY);
            dataTracker.setCarouselName(StringUtils.EMPTY);
          }

          dataTracker.setItemNumber(atomicInteger.getAndIncrement());
          dataTracker.setTotalItems(slides.size());
        });
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
