package com.saudi.tourism.core.models.components.tripplan.v1;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.saudi.tourism.core.utils.Constants.CITIES_PATH_FORMAT;

/**
 * Model for activities, it's used in Trip Planner & in c03-card component.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_ACTIVITY)
@NoArgsConstructor
@Slf4j
public class Activity implements Serializable {

  /**
   * id for mobile app (path without /content/sauditourism).
   */
  @Setter
  private String id;

  /**
   * Activity title (activity page title).
   */
  @Named(JcrConstants.JCR_TITLE)
  @ValueMapValue
  private String title;

  /**
   * Subtitle (activity page subtitle).
   */
  @ValueMapValue
  private String subtitle;

  /**
   * Short description (jcr:description of the page).
   */
  @Named(JcrConstants.JCR_DESCRIPTION)
  @ValueMapValue
  private String shortDescription;

  /**
   * Description of the activity.
   */
  @ValueMapValue
  private String description;

  /**
   * The city (id from jcr).
   */
  @ValueMapValue(name = Constants.CITY)
  private String cityId;

  /**
   * The city (object).
   */
  private RegionCity city;

  /**
   * {@code true} if this point is opened 24 hours/day.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  private Boolean roundTheClock;

  /**
   * Daily opening time of the point.
   */
  @ValueMapValue
  private String startTime;

  /**
   * Daily closing time of the point.
   */
  @ValueMapValue
  private String endTime;

  /**
   * Average time to spend, hours.
   */
  @ValueMapValue
  private int averageTime;

  /**
   * Daily opening time of the point to display (with arabic processing).
   */
  @ValueMapValue(name = "startTime")
  @Setter
  private String displayedStartTime;

  /**
   * Daily closing time of the point to display (with arabic processing).
   */
  @ValueMapValue(name = "endTime")
  @Setter
  private String displayedEndTime;

  /**
   * Caption (displayed over the image).
   */
  @ChildResource
  private ImageCaption caption;

  /**
   * Latitude.
   */
  @ValueMapValue
  private String latitude;

  /**
   * Longitude.
   */
  @ValueMapValue
  private String longitude;

  /**
   * Activity images.
   */
  @ChildResource
  private Image image;

  /**
   * One image source for API.
   */
  private String featuredImage;

  /**
   * Tags (array of tag ids, from cq:tags).
   */
  @Named(NameConstants.PN_TAGS)
  @ValueMapValue
  private String[] tagIds;

  /**
   * list of interests.
   */
  @ValueMapValue
  private String[] interests;

  /**
   * list of travelPartner values.
   */
  @ValueMapValue
  private String[] travelPartner;

  /**
   * Categories / tags for this activity.
   */
  private List<CategoryTag> tags;

  /**
   * Video Path.
   */
  @ValueMapValue
  private String videoPath;

  /**
   * Mobile Video Path.
   */
  @ValueMapValue
  private String mobileVideoPath;

  /**
   * The corresponding content page.
   */
  @JsonIgnore
  @ValueMapValue
  private transient String webMappingPath;

  /**
   * The corresponding app location page.
   */
  @JsonIgnore
  @ValueMapValue
  private transient String appMappingPath;

  /**
   * favId to be able to add the attraction page to favorites.
   */
  @Setter
  private String favId;

  /**
   * Mobile app id (path to the corresponding app location page).
   */
  private String appId;

  /**
   * Full path to the current activity page.
   */
  @Setter
  private String path;

  /**
   * Link to the corresponding web page (for displaying in Trip Planner etc.).
   */
  @Setter
  private String link;

  /**
   * Link Text for Link.
   */
  @Setter
  @Accessors(chain = true)
  private String linkText;

  /**
   * Locale for this activity (for i18n).
   */
  private String locale;

  /**
   * Service to get city by id.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient RegionCityService citiesService;

  /**
   * Sling Settings service to obtain runmodes.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Path property constructor for binary search using {@link java.util.Collections#binarySearch}.
   *
   * @param pathToPage path to activity page (to look for)
   */
  public Activity(final String pathToPage) {
    setPath(pathToPage);
  }

  /**
   * Injection constructor.
   *
   * @param currentResource jcr:content resource of the current activity page
   * @param settingsService sling settings service to check this env is author or publish
   */
  @Inject
  public Activity(
      @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED) @Named("currentResource")
      @NotNull final Resource currentResource,
      @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED) @NotNull
      final SlingSettingsService settingsService) {

    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    assert pageManager != null;

    // Update path (full path to the activity page)
    final Page currentPage = pageManager.getContainingPage(currentResource);
    setPath(currentPage.getPath());
    // Id for activities is like favorite path (Configs/activities/abha/abha-paraglider-zone)
    id = LinkUtils.getFavoritePath(path);

    // Update locale (for translations)
    locale = CommonUtils.getLanguageForPath(path);

    final ValueMap properties = currentResource.getValueMap();

    // Content page path related props
    webMappingPath = properties.get("webMappingPath", String.class);
    if (StringUtils.isNotBlank(webMappingPath)) {
      // Convert locale in the path in case if it was stored incorrectly or is used from master page
      // (workaround as sometimes in other locales this value contains path for english page)
      webMappingPath = CommonUtils.correctFullPathLocale(webMappingPath, locale);

      setFavId(LinkUtils.getFavoritePath(webMappingPath));
      setLink(LinkUtils.getAuthorPublishUrl(resourceResolver, webMappingPath,
          Boolean.toString(settingsService.getRunModes().contains(Externalizer.PUBLISH))));
    }

    // App location page path related props
    appMappingPath = properties.get("appMappingPath", String.class);
    if (StringUtils.isNotBlank(appMappingPath)) {
      // Convert locale in the path in case if it was stored incorrectly or is used from master page
      // (workaround as sometimes in other locales this value contains path for english page)
      appMappingPath = CommonUtils.correctFullPathLocale(appMappingPath, locale);

      this.appId = AppUtils.pathToID(appMappingPath);
    }

    // Process cq:tags into the list of CategoryTag objects
    final String[] cqTags = properties.get(NameConstants.PN_TAGS, String[].class);
    if (ArrayUtils.isNotEmpty(cqTags)) {
      tags = CommonUtils.buildCategoryTagsList(cqTags, resourceResolver, locale);
    }
  }

  /**
   * Post-construct initialization.
   */
  @PostConstruct
  private void initActivity() {
    // City id must be real id
    cityId = AppUtils.stringToID(cityId);
    // Fill city object
    city = citiesService.getRegionCityById(cityId, locale);

    String cityPath = String.format(CITIES_PATH_FORMAT, locale);
    // check if activity path is city page, if true, return city id to access city template
    if (StringUtils.isNotBlank(appId) && appId.contains(cityPath)) {
      appId = cityId;
    }

    Optional.ofNullable(image).filter(im -> StringUtils.isBlank(im.getAlt()))
            .ifPresent(im -> im.setAlt(title));
    if (image != null) {
      // Update image alt if it's empty, use activity title instead
      if (StringUtils.isBlank(image.getAlt())) {
        image.setAlt(title);
      }

      // Get the best image to be displayed (s7 image or dam image if s7 is empty)
      DynamicMediaUtils.prepareDMImages(image, TripPlanConstants.CROP_ACTIVITY_CARD_DSK,
          TripPlanConstants.CROP_ACTIVITY_CARD_MOB, DynamicMediaUtils.isCnServer(settingsService));
      featuredImage = image.getDesktopImage();
      if (StringUtils.isEmpty(featuredImage) && StringUtils.isNotBlank(image.getFileReference())) {
        featuredImage = image.getFileReference();
      }
    }
    // The feature image shouldn't be empty
    featuredImage = Optional.ofNullable(featuredImage).filter(StringUtils::isNotBlank)
        .map(str -> CommonUtils.getS7EncodedUrl(featuredImage))
        .orElse(Constants.DEFAULT_IMAGE_PLACEHOLDER);

    final boolean hasDescription = StringUtils.isNotBlank(description);
    final boolean hasShortDescription = StringUtils.isNotBlank(shortDescription);
    if (!hasDescription && hasShortDescription) {
      // If the description is empty, fill it from short description
      description = Constants.OPEN_PARAGRAPH_TAG + shortDescription + Constants.CLOSE_PARAGRAPH_TAG;
    } else if (!hasShortDescription && hasDescription) {
      // If the short description is empty, use the first sentence from the description for it
      shortDescription = CommonUtils.stripHtml(CommonUtils.getFirstSentence(description));
    }

    if (caption != null && StringUtils.isNotBlank(caption.getLink()) && StringUtils
        .isBlank(caption.getLinkType())) {
      caption.setLinkType(LinkUtils.getLinkType(caption.getLink()));
    }

    if (Boolean.TRUE.equals(roundTheClock)) {
      // Clean opening / closing time in case of round the clock
      startTime = null;
      endTime = null;
    } else if (Constants.ARABIC_LOCALE.equals(locale)) {
      // Convert displayed time to arabic numerals
      if (StringUtils.isNotBlank(startTime)) {
        setDisplayedStartTime(CommonUtils.getArabicNumeralChar(startTime));
      }
      if (StringUtils.isNotBlank(endTime)) {
        setDisplayedEndTime(CommonUtils.getArabicNumeralChar(endTime));
      }
    }

    // Cleanup before storing into cache
    citiesService = null;
  }

  /**
   * Setter for the city, updates also id.
   *
   * @param newCity new city instance
   */
  public void setCity(final RegionCity newCity) {
    if (newCity != null) {
      this.cityId = newCity.getId();
    }
    this.city = newCity;
  }
}
