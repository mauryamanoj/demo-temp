package com.saudi.tourism.core.models.components.tripplan.v1;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents one trip itinerary point, which can be event or activity.
 */
@Data
@NoArgsConstructor
@Slf4j
public class TripPoint implements Serializable {

  /**
   * Type of this point (activity, event etc).
   */
  private String type;

  /**
   * Id of the point (activity id or event id).
   */
  private String id;

  /**
   * App id.
   */
  private String appId;

  /**
   * Path to the corresponding AEM page.
   */
  private String path;

  /**
   * favId for app to map to web pages.
   */
  private String favId;

  /**
   * Link to the corresponding web page (for displaying in Trip Planner etc.).
   */
  private String link;

  /**
   * Image for this point.
   */
  private String imageSource = Constants.DEFAULT_IMAGE_PLACEHOLDER;

  /**
   * City id.
   */
  private String cityId;

  /**
   * City (localized).
   */
  private RegionCity city;

  /**
   * Point title.
   */
  private String title;

  /**
   * Point Subtitle.
   */
  private String subtitle;

  /**
   * Short description (jcr:description of the page).
   */
  private String shortDescription;

  /**
   * Description of the visiting point.
   */
  private String description;

  /**
   * For events: start date.
   */
  private String startDate;

  /**
   * For events: end date.
   */
  private String endDate;

  /**
   * For events: start date to display (with arabic processing).
   */
  private String displayedStartDate;

  /**
   * For events: end date to display (with arabic processing).
   */
  private String displayedEndDate;

  /**
   * {@code true} if this point is opened 24 hours/day.
   */
  private Boolean roundTheClock;

  /**
   * Daily opening time of the point.
   */
  private String startTime;

  /**
   * Daily closing time of the point.
   */
  private String endTime;

  /**
   * Daily opening time of the point to display (with arabic processing).
   */
  private String displayedStartTime;

  /**
   * Daily closing time of the point to display (with arabic processing).
   */
  private String displayedEndTime;

  /**
   * Average time to spend, hours.
   */
  private Integer averageTime;

  /**
   * Caption (displayed over the image).
   */
  private ImageCaption caption;

  /**
   * Latitude.
   */
  private String latitude;

  /**
   * Longitude.
   */
  private String longitude;

  /**
   * Extended image object, if necessary.
   */
  private Image image;

  /**
   * Tags (array of tag ids).
   */
  private String[] tagIds;

  /**
   * Categories / tags for this point of interest.
   */
  private List<CategoryTag> tags;

  /**
   * This is a copy constructor to clone this instance.
   *
   * @param source the TripPoint instance to be copied
   */
  public TripPoint(final TripPoint source) {
    setType(source.getType());
    setId(source.getId());
    setAppId(source.getAppId());
    setPath(source.getPath());
    setFavId(source.getFavId());
    setLink(source.getLink());

    final String thatImage = source.getImageSource();
    if (StringUtils.isNotBlank(thatImage)) {
      setImageSource(thatImage);
    }

    setTitle(source.getTitle());
    setSubtitle(source.getSubtitle());
    setShortDescription(source.getShortDescription());
    setDescription(source.getDescription());
    setCityId(source.getCityId());
    setCity(source.getCity());

    setStartDate(source.getStartDate());
    setEndDate(source.getEndDate());
    setDisplayedStartDate(source.getDisplayedStartDate());
    setDisplayedEndDate(source.getDisplayedEndDate());

    setRoundTheClock(source.getRoundTheClock());
    setStartTime(source.getStartTime());
    setEndTime(source.getEndTime());
    setDisplayedStartTime(source.getDisplayedStartTime());
    setDisplayedEndTime(source.getDisplayedEndTime());
    setAverageTime(source.getAverageTime());

    setCaption(source.getCaption());
    setLatitude(source.getLatitude());
    setLongitude(source.getLongitude());
    setImage(source.getImage());
    setTagIds(source.getTagIds());
    setTags(source.getTags());
  }



  /**
   * This is the factory constructor method to create a trip point from the path to visiting point
   * page.
   *
   * @param pointPath       path to activity or event page
   * @param language        the current locale
   * @param resolver        the current resource resolver to get event's data by path to the event
   * @param activityService activities service to get activity by path
   * @param settingsService slingSettings
   * @return created trip point instance
   * @throws TripException exception
   */
  public static TripPoint createForPath(@NotNull final String pointPath, final String language,
      @NotNull final ResourceResolver resolver, @NotNull final ActivityService activityService,
      final SlingSettingsService settingsService)
      throws TripException {
    final TripPoint result;

    try {
      if (StringUtils.contains(pointPath, Constants.ACTIVITIES_PARTIAL)) {
        // The path is for activity
        final Activity activity = activityService.getActivityByPath(pointPath, language);
        if (null == activity) {
          LOGGER.error("Could not get activity by path {}, language {}", pointPath, language);
          return null;
        }

        result = new TripPoint(activity, settingsService);

      } else {
        // Only activities and events can be added into trip plan for now, so this one is for events
        // TODO Rewrite EventsService with using MemHolder and get one event by path faster

        String absolutePointPath = CommonUtils.toAbsolutePath(pointPath, language);
        Resource pointResource = resolver.getResource(absolutePointPath);
        if (null == pointResource && !Constants.DEFAULT_LOCALE.equals(language)) {
          LOGGER.error("Check if the trip point page {} is rolled out to the locale {}", pointPath,
              language);
          // Try to use the same activity / event from english page
          absolutePointPath = CommonUtils.toAbsolutePath(pointPath, Constants.DEFAULT_LOCALE);
          pointResource = resolver.getResource(absolutePointPath);
        }

        throwExceptionIfTrue(Objects.isNull(pointResource),
                "Trip visiting point page not found by path " + pointPath);

        final Resource pointContentResource = pointResource.getChild(JcrConstants.JCR_CONTENT);
        throwExceptionIfTrue(Objects.isNull(pointContentResource),
                "Trip visiting point page is unpublished (no jcr:content) " + pointPath);

        if (!pointContentResource.isResourceType(Constants.EVENT_DETAIL_RES_TYPE)) {
          LOGGER.error("Unknown resource type {} for the trip point {}",
              pointContentResource.getResourceType(), pointContentResource.getPath());
          return null;
        }

        final EventDetail eventDetail = pointContentResource.adaptTo(EventDetail.class);
        throwExceptionIfTrue(Objects.isNull(eventDetail),
                "Adapting resource to EventDetail error " + pointContentResource);

        result = new TripPoint(eventDetail, settingsService);
      }
    } catch (Exception e) {
      final String errorMessage =
          "Trip visiting point page resource processing error. Path: " + pointPath;
      LOGGER.error(errorMessage, e);
      throw new TripException(errorMessage, e);
    }

    result.setPath(Optional.ofNullable(result.getPath())
            .filter(StringUtils::isNotEmpty).orElse(pointPath));
    result.setId(Optional.ofNullable(result.id).filter(StringUtils::isNotEmpty)
            .orElse(LinkUtils.getFavoritePath(result.getPath())));
    return result;
  }

  /**
   * Throw exception if condition is true.
   * @param condition condition
   * @param errorMessage errorMessage
   * @throws TripException exception
   */
  private static void throwExceptionIfTrue(boolean condition, String errorMessage)
          throws TripException {
    if (condition) {
      throw new TripException(errorMessage);
    }
  }

  /**
   * This is the constructor to create a trip point from the event instance.
   *
   * @param event the event instance to be added into this point
   * @param slingSettingsService slingSetting service
   */
  public TripPoint(final EventDetail event, final SlingSettingsService slingSettingsService) {
    this.apply(event, slingSettingsService);
  }

  /**
   * This is the constructor to create a trip point from the activity instance.
   *
   * @param activity the activity instance to be added into this point
   * @param slingSettingsService slingSetting service
   */
  public TripPoint(final Activity activity, final SlingSettingsService slingSettingsService) {
    this.apply(activity, slingSettingsService);
  }

  /**
   * Update this trip point from the provided activity object.
   *
   * @param activity activity object to put to this point
   * @param settingsService slingSetting service
   */
  private void apply(@NotNull final Activity activity, final SlingSettingsService settingsService) {
    setType(TripPlanConstants.TYPE_ACTIVITY);
    setId(activity.getId());
    setAppId(activity.getAppId());
    setPath(activity.getPath());
    setFavId(activity.getFavId());
    setLink(activity.getLink());

    // Update main image source

    // Update main image source (event card image is used for trip planner points)
    imageSource = DynamicMediaUtils.getScene7ImageWithDefaultImage(StringUtils.defaultIfBlank(
        Optional.ofNullable(activity.getImage()).map(Image::getS7fileReference)
            .orElse(activity.getFeaturedImage()), Constants.DEFAULT_IMAGE_PLACEHOLDER),
        TripPlanConstants.CROP_ACTIVITY_POINT_DSK_CARD,
        DynamicMediaUtils.isCnServer(settingsService));

    setTitle(activity.getTitle());
    setSubtitle(activity.getSubtitle());
    setShortDescription(activity.getShortDescription());
    setDescription(activity.getDescription());
    setCityId(activity.getCityId());
    setCity(activity.getCity());

    setRoundTheClock(activity.getRoundTheClock());
    setStartTime(activity.getStartTime());
    setEndTime(activity.getEndTime());
    setDisplayedStartTime(activity.getDisplayedStartTime());
    setDisplayedEndTime(activity.getDisplayedEndTime());
    setAverageTime(activity.getAverageTime());

    setCaption(activity.getCaption());
    setLatitude(activity.getLatitude());
    setLongitude(activity.getLongitude());
    setImage(activity.getImage());
    setTagIds(activity.getTagIds());
    setTags(activity.getTags());
  }

  /**
   * Update this trip point from the provided event object.
   *
   * @param event event (EventDetail) object to put to this point
   * @param settingsService slingSetting service
   */
  private void apply(final EventDetail event, final SlingSettingsService settingsService) {
    final String locale = CommonUtils.getLanguageForPath(event.getPath());

    setType(TripPlanConstants.TYPE_EVENT);
    setId(event.getId());
    setAppId(event.getAppId());
    setPath(event.getPath());
    setFavId(event.getFavId());
    setLink(event.getLink());

    // Update main image source (event card image is used for trip planner points)
    imageSource = DynamicMediaUtils.getScene7ImageWithDefaultImage(event.getS7featureEventImage(),
        TripPlanConstants.CROP_EVENT_POINT_DSK_CARD, DynamicMediaUtils.isCnServer(settingsService));
    if (StringUtils.isEmpty(imageSource)) {
      setImageSource(Constants.DEFAULT_IMAGE_PLACEHOLDER);
    }

    setTitle(event.getTitle());
    setSubtitle(event.getSubtitle());
    setShortDescription(event.getShortDescription());
    setDescription(event.getCopy());
    setCityId(event.getCityId());
    setCity(event.getCityObj());

    setRoundTheClock(event.getRoundTheClock());
    final String eventStartTime = event.getStartTime();
    final String eventEndTime = event.getEndTime();
    setStartTime(eventStartTime);
    setEndTime(eventEndTime);
    // Displayed start/end time with conversion to arabic numerals
    if (Constants.ARABIC_LOCALE.equals(locale)) {
      if (StringUtils.isNotBlank(eventStartTime)) {
        setDisplayedStartTime(CommonUtils.getArabicNumeralChar(eventStartTime));
      }
      if (StringUtils.isNotBlank(eventEndTime)) {
        setDisplayedEndTime(CommonUtils.getArabicNumeralChar(eventEndTime));
      }
    } else {
      setDisplayedStartTime(eventStartTime);
      setDisplayedEndTime(eventEndTime);
    }
    setAverageTime(event.getAverageTime());

    setStartDate(CommonUtils.dateToString(event.getStartDateCal()));
    setEndDate(CommonUtils.dateToString(event.getEndDateCal()));
    // Event displayed start/end dates in format "Tue 28, Aug 2020"
    String displayedEventStartDate =
        CommonUtils.dateToString(event.getStartDateCal(), Constants.FMT_WD_D_M_Y, locale);
    String displayedEventEndDate =
        CommonUtils.dateToString(event.getEndDateCal(), Constants.FMT_WD_D_M_Y, locale);
    if (Constants.ARABIC_LOCALE.equals(locale)) {
      if (StringUtils.isNotBlank(displayedEventStartDate)) {
        displayedEventStartDate = CommonUtils.getArabicNumeralChar(displayedEventStartDate);
      }
      if (StringUtils.isNotBlank(displayedEventEndDate)) {
        displayedEventEndDate = CommonUtils.getArabicNumeralChar(displayedEventEndDate);
      }
    }
    setDisplayedStartDate(displayedEventStartDate);
    setDisplayedEndDate(displayedEventEndDate);

    // Checkme Putting event booking link into caption
    final Link eventLink = event.getEventLink();
    if (eventLink != null && StringUtils.isNotBlank(eventLink.getCopy())) {
      final ImageCaption bookingCaption = new ImageCaption();
      bookingCaption.setCopy(eventLink.getCopy());

      final String bookingUrl = CommonUtils
          .firstNotBlank(eventLink.getUrlSlingExporter(), eventLink.getUrlWithExtension(),
              eventLink.getUrl());
      bookingCaption.setLink(bookingUrl);
      bookingCaption.setLinkType(LinkUtils.getLinkType(bookingUrl));
      setCaption(bookingCaption);
    }

    setLatitude(event.getLatitude());
    setLongitude(event.getLongitude());

    setTagIds(event.getCategoryTags());
    setTags(event.getTags());
  }
}
