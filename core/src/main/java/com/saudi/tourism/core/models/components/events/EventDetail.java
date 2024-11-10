package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.RangeIterator;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.SliderDataLayer;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;

/**
 * The type Event detail.
 */
@SuppressWarnings("squid:S2065")
@Model(adaptables = Resource.class,
       resourceType = {Constants.EVENT_DETAIL_RES_TYPE},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "search",
          name = "jackson",
          extensions = "json",
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@ToString
@Slf4j
public class EventDetail extends EventBase implements Serializable {

  /**
   * Category tags.
   */
  @Getter
  @Expose
  private List<String> category;

  /**
   * Target group tags names.
   */
  @Getter
  private List<String> targetGroup;

  /**
   * venue tags names.
   */
  @Getter
  private List<String> venue;

  /**
   * venue tags names.
   */
  @Getter
  private List<String> venueTitles;

  /**
   * Category tags.
   */
  @Getter
  @Setter
  private List<String> categoryTitles;

  /**
   * Target group tags names.
   */
  @Getter
  @Setter
  private List<String> targetTitles;

  /**
   * The event location icon.
   */
  @ValueMapValue
  @Default(values = Constants.LOCATION_ICON)
  @Getter
  private String locationIcon;

  /**
   * The cardImage.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String cardImage;

  /**
   * The Card mobile image.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String cardImageMobile;

  /**
   * The Feature event image.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String featureEventImage;

  /**
   * Workaround for using dam image in trip planner instead of s7 image.
   * TODO FIXME Remove this after we add s7 into trip planner
   */
  @JsonIgnore
  @ValueMapValue(name = PN_FEATURE_EVENT_IMAGE)
  @Getter
  private transient String imageFileReference;

  /**
   * The Dynamic Media Feature event image.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String s7featureEventImage;

  /**
   * The Feature event mobile image.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String featureEventMobileImage;

  /**
   * The Event Slider image.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String sliderImage;

  /**
   * The Alt image.
   */
  @ValueMapValue
  @Getter
  private String altImage;

  /**
   * The Calendar end date.
   */
  @ValueMapValue
  @Setter
  @Getter
  @Expose
  private String calendarEndDate;

  /**
   * The Calendar start date.
   */
  @ValueMapValue
  @Setter
  @Getter
  @Expose
  private String calendarStartDate;

  /**
   * The Calendar end date.
   */
  @ValueMapValue(name = "calendarEndDate")
  @Getter
  @Setter
  private Calendar endDateCal;

  /**
   * The Calendar start date.
   */
  @ValueMapValue(name = "calendarStartDate")
  @Getter
  @Setter
  private Calendar startDateCal;

  /**
   * Start time Calendar object.
   */
  @ValueMapValue(name = "startTime")
  @Getter
  private Calendar startTimeCal;

  /**
   * End time Calendar Object.
   */
  @ValueMapValue(name = "endTime")
  @Getter
  private Calendar endTimeCal;

  /**
   * {@code true} if the event location is opened 24 hours/day.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Getter
  private Boolean roundTheClock;

  /**
   * Daily opening time of the event location.
   */
  @ValueMapValue
  @Getter
  private String startTime;

  /**
   * Daily closing time of the event location.
   */
  @ValueMapValue
  @Getter
  private String endTime;

  /**
   * Average time to spend, hours.
   */
  @Getter
  @ValueMapValue
  private Integer averageTime;

  /**
   * Latitude.
   */
  @ValueMapValue
  @Getter
  private String latitude;

  /**
   * Longitude.
   */
  @ValueMapValue
  @Getter
  private String longitude;

  /**
   * Zoom.
   */
  @ValueMapValue
  @Getter
  private String zoom;

  /**
   * Street.
   */
  @ValueMapValue
  @Getter
  private String street;

  /**
   * Street number.
   */
  @ValueMapValue
  @Getter
  private String streetNumber;

  /**
   * Zipcode.
   */
  @ValueMapValue
  @Getter
  private String zipcode;

  /**
   * Short description for list.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String shortDescription;
  /**
   * Title if no season selected.
   */
  @ValueMapValue
  @Getter
  private String noSeasonTitle;

  /**
   * Show on Map flag.
   */
  @ValueMapValue
  @Getter
  private Boolean showOnMap;

  /**
   * The Start date month.
   */
  @Getter
  @Setter
  private String startDateMonth;

  /**
   * The End date month.
   */
  @Getter
  @Setter
  private String endDateMonth;

  /**
   * The Event image, added to match frontend api structure.
   */
  @Getter
  private EventImage image;

  /**
   * The ID.
   */
  @Getter
  @Setter
  private String id;

  /**
   * App Id.
   */
  @Getter
  private String appId;

  /**
   * The Year.
   */
  @Getter
  private String year;

  /**
   * The Start date day.
   */
  @Getter
  @Setter
  private String startDateDay;

  /**
   * The End date day.
   */
  @Getter
  @Setter
  private String endDateDay;

  /**
   * Displayed start date.
   */
  @Getter
  @Setter
  private String displayedStartDate;

  /**
   * Displayed end date.
   */
  @Getter
  @Setter
  private String displayedEndDate;

  /**
   * The Is show end date.
   */
  @Getter
  private boolean isShowEndDate = false;

  /**
   * The Tickets.
   */
  @ChildResource
  @Getter
  private List<InfoItem> eventInfo;

  /**
   * Event Register / Booking link.
   */
  @ChildResource
  @Getter
  private Link eventLink;

  /**
   * The full path to the corresponding page resource.
   */
  @Getter
  @Setter
  private String path;

  /**
   * The season origin value.
   */
  @Getter
  @Setter
  private String seasonId;

  /**
   * The freePaid origin value.
   */
  @Getter
  @Setter
  private String freePaidId;

  /**
   * The path.
   */
  @ValueMapValue
  @Named("eventLink-copy")
  @Getter
  private String eventLinkCopy;

  /**
   * The path.
   */
  @ValueMapValue
  @Named("eventLink-analyticsDataCopy")
  @Getter
  @Expose
  private String eventLinkAnalyticsDataCopy;



  /**
   * The path.
   */
  @ValueMapValue
  @Named("eventLink-url")
  @Getter
  private String eventLinkUrl;

  /**
   * Is Halayalla link.
   */
  @ValueMapValue
  @Getter
  private boolean isHalaYallaLink;

  /**
   * The path.
   */
  @ValueMapValue
  @Named("eventLink-targetInNewWindow")
  @Getter
  private Boolean targetInNewWindow;

  /**
   * The favId.
   */
  @Setter
  @Getter
  private String favId;

  /**
   * The favCategory.
   */
  @Setter
  @Getter
  private String favCategory;

  /**
   * The proper hyper reference link pointing to this event page.
   */
  @Getter
  @Expose
  private String link;

  /**
   * The urlSlingExporter.
   */
  @Getter
  private String urlSlingExporter;

  /**
   * List of event related paths for the servlet.
   */
  @Getter
  @Setter
  private List<String> relatedEventsPaths;

  /**
   * Combined tag id + label (combined categoryTags & category).
   */
  @Getter
  @Setter
  private List<CategoryTag> tags;

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
   * Object for data-tracking.
   */
  @JsonIgnore
  @Getter
  private transient SliderDataLayer dataTracker = new SliderDataLayer();

  /**
   * Cities service (to get cities by id).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient RegionCityService citiesService;


  /**
   * if it is an internal booking.
   */
  @Getter
  @ValueMapValue
  private boolean internalBooking;

  /**
   * STOCH : EventType.
   */
  @Getter
  @ValueMapValue
  private String eventType;

  /**
   * STOCH : 360Image.
   */
  @Getter
  @ValueMapValue
  @JsonProperty("360Image")
  @Named("360Image")
  private String image360;


  /**
   * STOCH : weekendEvent.
   */
  @Getter
  @ValueMapValue
  private Boolean weekendEvent;

  /**
   * STOCH : promoTagText.
   */
  @Getter
  @ValueMapValue
  private Double promoTagText;

  /**
   * STOCH : featuredInMap.
   */
  @Getter
  @ValueMapValue
  private Boolean featuredInMap;

  /**
   * STOCH : MapLink.
   */
  @ChildResource
  @Getter
  private List<MapLink> mapLinks;

  /**
   * STOCH : createdDate.
   */
  @Getter
  @ValueMapValue(name = "jcr:created")
  private String createdDate;

  /**
   * STOCH : lastModified.
   */
  @Getter
  @ValueMapValue(name = "cq:lastModified")
  private String lastModified;

  /**
   * The channel that will consume api.
   */
  @ValueMapValue
  @Getter
  @Setter
  private List<String> channel;



  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    setSeasonId(getSeason());
    setFreePaidId(getFreePaid());
    internalBooking = this.isHalaYallaLink;

    path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    // language needed for tag translation title
    String language = CommonUtils.getLanguageForPath(path);

    setEventDetailImages();

    extractShortDescription();

    zoom = Optional.ofNullable(zoom).orElse(Constants.DEFAULT_ZOOM);

    urlSlingExporter = LinkUtils.getFavoritePath(path);
    this.link = LinkUtils
        .getAuthorPublishUrl(resourceResolver, path, settingsService.getRunModes().contains(Externalizer.PUBLISH));

    setFavId(LinkUtils.getFavoritePath(path));
    favCategory = SearchUtils.getFavCategory(currentResource);

    // Create id (with a workaround for home (/en) pages)
    id = AppUtils.stringToID(StringUtils.defaultIfBlank(StringUtils.substringAfter(path,
        Constants.FORWARD_SLASH_CHARACTER + language + Constants.FORWARD_SLASH_CHARACTER),
        SpecialCharConstants.FORWARD_SLASH));

    // appId equals to id
    appId = id;

    // Localize necessary fields
    initCity(language);

    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
    setRegion(CommonUtils.getI18nString(i18n, getRegion()));
    setFreePaid(CommonUtils.getI18nString(i18n, getFreePaid()));
    setSeason(CommonUtils.getI18nString(i18n, getSeason()));

    String[] categoryTags = getCategoryTags();
    category = CommonUtils.getCategoryFromTagName(categoryTags, resourceResolver, language);
    targetGroup =
        CommonUtils.getCategoryFromTagName(getTargetGroupTags(), resourceResolver, language);
    venue =
      CommonUtils.getCategoryFromTagName(getVenueTags(), resourceResolver, language);
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    // It's always not null, assert to avoid checkstyle warning
    assert tagManager != null;

    categoryTitles = Optional.ofNullable(getCategoryTags()).map(Arrays::stream)
        .orElse(Stream.empty()).map(tagManager::resolve).filter(Objects::nonNull)
        .map(Tag::getTitle).collect(Collectors.toList());
    targetTitles = Optional.ofNullable(getTargetGroupTags()).map(Arrays::stream)
        .orElse(Stream.empty()).map(tagManager::resolve).filter(Objects::nonNull)
        .map(Tag::getTitle).collect(Collectors.toList());
    venueTitles = Optional.ofNullable(getVenueTags()).map(Arrays::stream)
      .orElse(Stream.empty()).map(tagManager::resolve).filter(Objects::nonNull)
      .map(Tag::getTitle).collect(Collectors.toList());

    startDateMonth = CommonUtils.getDateMonth(getCalendarStartDate(), Constants.FORMAT_MONTH);
    endDateMonth = CommonUtils.getDateMonth(getCalendarEndDate(), Constants.FORMAT_MONTH);
    isShowEndDate = !StringUtils.equals(getCalendarEndDate(), getCalendarStartDate());
    year = CommonUtils.getDateMonth(getCalendarStartDate(), Constants.FORMAT_YEAR);
    startDateDay = CommonUtils.convertDateToSTring(getCalendarStartDate(), Constants.FORMAT_DAY);
    endDateDay = CommonUtils.convertDateToSTring(getCalendarEndDate(), Constants.FORMAT_DAY);

    // Clean opening / closing time in case of round the clock
    if (Boolean.TRUE.equals(roundTheClock)) {
      this.startTime = null;
      this.endTime = null;
    } else {
      // Extracts time in "HH:mm" format from calendar values
      startTime = CommonUtils.convertDateToSTring(getStartTime(), Constants.FORMAT_HOUR_MINUTE);
      endTime = CommonUtils.convertDateToSTring(getEndTime(), Constants.FORMAT_HOUR_MINUTE);
    }

    // Update display date format (to be "28/08")
    initDisplayedStartEnd(language);

    String categoryImage = getCategoryImage(categoryTags);
    cardImage = StringUtils.defaultIfBlank(cardImage, categoryImage);
    cardImageMobile = StringUtils.defaultIfBlank(cardImageMobile, categoryImage);
    featureEventImage = StringUtils.defaultIfBlank(featureEventImage, categoryImage);
    featureEventMobileImage = StringUtils.defaultIfBlank(featureEventMobileImage, categoryImage);
    sliderImage = StringUtils.defaultIfBlank(sliderImage, categoryImage);
    image = new EventImage();
    if (Objects.nonNull(getCardImage())) {
      image.setSrc(getCardImage());
    }
    image.setAlt(getAltImage());

    setTags(CommonUtils.buildCategoryTagsList(categoryTags, resourceResolver, language));

    dataTracker.setSlideTitle(getTitle());
    dataTracker.setSlideName(getAnalyticsTitle());
    dataTracker.setSlideAsset(image.getSrc());

    if (!StringUtils.isAllBlank(eventLinkCopy, eventLinkUrl)) {
      this.eventLink =
          new Link(eventLinkUrl, eventLinkCopy, Boolean.TRUE.equals(targetInNewWindow));
    }

    // Cleanup to be able to cache this object
    this.resourceResolver = null;
    this.currentResource = null;
    this.settingsService = null;
    this.i18nProvider = null;
    this.citiesService = null;
  }

  /**
   * initialize city.
   * @param lang language
   */
  private void initCity(String lang) {
    final String cityId = getCity();
    if (StringUtils.isNotBlank(cityId)) {
      final RegionCity city = citiesService.getRegionCityById(cityId, lang);
      if (city != null) {
        setCityObj(city);
        setCity(city.getName());
      }
    }
  }

  /**
   * initialize displayedStart & displayedEnd variables.
   * @param lang language
   */
  private void initDisplayedStartEnd(String lang) {
    String displayedStart = getDisplayDate(startDateCal, lang);
    String displayedEnd = getDisplayDate(endDateCal, lang);
    if (Constants.ARABIC_LOCALE.equals(lang)) {
      if (StringUtils.isNotBlank(displayedStart)) {
        displayedStart = CommonUtils.getArabicNumeralChar(displayedStart);
      }
      if (StringUtils.isNotBlank(displayedEnd)) {
        displayedEnd = CommonUtils.getArabicNumeralChar(displayedEnd);
      }
    }
    setDisplayedStartDate(displayedStart);
    setDisplayedEndDate(displayedEnd);
  }

  /**
   * Convert date to dd/MM format.
   * @param date date
   * @param language language
   * @return converted date
   */
  @Nullable private static String getDisplayDate(final Calendar date, final String language) {
    return CommonUtils.dateToString(date, Constants.FORMAT_EVENT_DATE_ARABIC, language);
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
   * Method returns the Category Image.
   *
   * @param categoryTags String[]
   * @return return the Category Image.
   */
  private String getCategoryImage(String[] categoryTags) {
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    if (ArrayUtils.isNotEmpty(categoryTags) && Objects.nonNull(tagManager)) {
      RangeIterator<Resource> iterator =
          tagManager.find(DamConstants.MOUNTPOINT_ASSETS, new String[] {categoryTags[0]}, true);
      if (Objects.nonNull(iterator) && iterator.hasNext()) {
        Resource next = iterator.next();
        Asset asset = DamUtil.resolveToAsset(next);
        if (Objects.nonNull(asset)) {
          return asset.getPath();
        }
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * Set Image sets for event details.
   */
  private void setEventDetailImages() {
    if (Objects.nonNull(s7featureEventImage) && Objects.nonNull(saudiTourismConfigs)
        && s7featureEventImage.startsWith(saudiTourismConfigs.getScene7Domain())
        && !s7featureEventImage.contains("scth/ugc")) {
      boolean isCnServer = DynamicMediaUtils.isCnServer(settingsService);
      // add crop if and only if s7banner image start with scene7 domain
      // TODO CHECKME Check please this not existing crop 560 x 314 and fix it.
      setCardImage(DynamicMediaUtils.getFormedSrcUrlWithProfileAndWidHei(
          s7featureEventImage, isCnServer, "crop-375x210", "750", "420"));
      // TODO CHECKME Check this not existing crop 315x177 and fix it.
      setCardImageMobile(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featureEventImage, "crop-315x177", false, isCnServer));
      setFeatureEventImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featureEventImage, DynamicMediaUtils.DM_CROP_1920_1080,
          isCnServer));
      setFeatureEventMobileImage(DynamicMediaUtils.getFormedSrcUrlWithProfileAndWidHei(
          s7featureEventImage, isCnServer, DynamicMediaUtils.DM_CROP_260_195, "520", "390"));
      setSliderImage(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(s7featureEventImage, DynamicMediaUtils.DM_CROP_360_480,
          isCnServer));
    } else {
      // If no s7 image use featureEventImage(dam image)
      if (Objects.nonNull(featureEventImage) && featureEventImage.contains("scth/ugc")) {
        featureEventImage = featureEventImage.replace("http://", "https://") + "?scl=1";
      }
      setCardImage(featureEventImage);
      setCardImageMobile(featureEventImage);
      setFeatureEventMobileImage(featureEventImage);
      setSliderImage(featureEventImage);
    }
  }
}
