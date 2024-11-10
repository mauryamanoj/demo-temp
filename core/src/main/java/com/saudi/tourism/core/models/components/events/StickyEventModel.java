package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.gson.CalendarAdapter;
import com.saudi.tourism.core.utils.gson.TimeAMPMAdapter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.net.URISyntaxException;
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

/** Sticky event detail. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class StickyEventModel extends EventBase {

  /**
   * Template Type.
   */
  @ValueMapValue
  private String templateType;

  /**
   * Event Register / Booking link.
   */
  @ChildResource
  @Expose
  private Link eventLink;

  /**
   * Working Time Text.
   */
  @ValueMapValue(name = "workingTimeText")
  @Expose
  private String workingTimeText;

  /**
   * Start time Calendar object.
   */
  @ValueMapValue(name = "startTime")
  @Expose
  @JsonAdapter(TimeAMPMAdapter.class)
  private Calendar startTimeCal;

  /**
   * End time Calendar Object.
   */
  @ValueMapValue(name = "endTime")
  @Expose
  @JsonAdapter(TimeAMPMAdapter.class)
  private Calendar endTimeCal;

  /**
   * Round the clock.
   */
  @ValueMapValue
  @Expose
  private Boolean roundTheClock;

  /**
   * The Calendar start date.
   */
  @ValueMapValue
  @Expose
  private Calendar calendarStartDate;

  /**
   * The Calendar end date.
   */
  @ValueMapValue
  @Expose
  private Calendar calendarEndDate;

  /**
   * Latitude.
   */
  @ValueMapValue
  @Expose
  private String latitude;
  /**
   * noSeasonTitle.
   */
  @ValueMapValue
  private String noSeasonTitle;

  /**
   * Longitude.
   */
  @ValueMapValue
  @Expose
  private String longitude;

  /**
   * Map Type.
   */
  @ValueMapValue
  @Expose
  private String mapType;

  /**
   * Zoom.
   */
  @ValueMapValue
  @Expose
  private String zoom;

  /**
   * Location name.
   */
  @ValueMapValue
  @Expose
  private String locationName;

  /**
   * Price Text.
   */
  @ValueMapValue
  @Expose
  private String priceText;

  /**
   * Duration Text.
   */
  @ValueMapValue
  @Expose
  private String durationText;

  /**
   * Activities Text.
   */
  @ValueMapValue
  @Expose
  private String activitiesText;

  /**
   * Suitable For Text.
   */
  @ValueMapValue
  @Expose
  private String suitableForText;

  /**
   * Price Value.
   */
  @ValueMapValue
  @Expose
  private String priceValue;

  /**
   * Accessibility Text.
   */
  @ValueMapValue
  @Expose
  private String accessibilityText;

  /**
   * Accessibility Value.
   */
  @ValueMapValue
  @Expose
  private String accessibiltyValue;

  /**
   * Location Title.
   */
  @ValueMapValue
  @Expose
  private String locationTitle;

  /**
   * Address info.
   */
  @ValueMapValue
  @Expose
  private String addressInfo;

  /**
   * Get direction Cta text.
   */
  @ValueMapValue
  @Expose
  private String getDirectionCtaText;
  /**
   * Get direction Cta Analytics text.
   */
  @ValueMapValue
  @Expose
  private String getAnalyticsDirectionCtaText;

  /**
   * Get direction Cta link.
   */
  @ValueMapValue
  private String getDirectionCtaLink;

  /**
   * eventLinkCopy.
   */
  @ValueMapValue
  @Named("eventLink-copy")
  @JsonIgnore
  private String eventLinkCopy;

  /**
   * eventLinkUrl.
   */
  @ValueMapValue
  @Named("eventLink-url")
  @JsonIgnore
  private String eventLinkUrl;

  /**
   * is Halayalla link.
   */
  @ValueMapValue
  private String isHalaYallaLink;

  /**
   * eventLinktargetInNewWindow.
   */
  @ValueMapValue
  @Named("eventLink-targetInNewWindow")
  @JsonIgnore
  private boolean eventLinkTargetInNewWindow;

  /**
   * targetGroupTags.
   */
  @ValueMapValue
  @Named("targetGroupTags")
  @JsonIgnore
  private String[] targetGroups;

  /**
   * SuitableFor list.
   */
  @Expose
  private List<String> suitableForList;

  /**
   * targetGroupTags.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] activities;

  /**
   * Target group tags names.
   */
  @Expose
  private List<String> activitiesList;

  /**
   * The Feature event image.
   */
  @ValueMapValue
  @Named(PN_FEATURE_EVENT_IMAGE)
  private String featureEventImage;

  /**
   * The Dynamic Media Feature event image.
   */
  @ValueMapValue
  @Named("s7featureEventImage")
  private String s7featureEventImage;

  /**
   * The Feature event mobile image.
   */
  private String featureEventMobileImage;

  /**
   * The event path for Favorites.
   */
  private String favoritePath;

  /**
   * The event id for Favorites.
   */
  private String favoriteId;

  /**
   * Event Summary Title.
   */
  @Expose
  private String eventSummaryTitle;


  /**
   * i18nProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  @JsonIgnore
  private ResourceBundleProvider i18nProvider;
  /**
   * currentResource.
   */
  @Self
  @JsonIgnore
  private Resource currentResource;

  /**
   * The Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * SaudiTourism config.
   */
  @JsonIgnore
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Google Map Api Key.
   */
  @Expose
  private String googleMapsKey;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void initStickyEventModel() {
    setImageInfo();
    ResourceResolver resolver = currentResource.getResourceResolver();
    String path =
        Optional.ofNullable(currentResource.getParent())
            .map(Resource::getPath)
            .orElse(StringUtils.EMPTY);
    this.favoritePath = LinkUtils.getAuthorPublishUrl(
            resolver, path, settingsService.getRunModes().contains(Externalizer.PUBLISH));
    this.favoriteId = LinkUtils.getFavoritePath(path);
    final String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    final Locale locale = new Locale(lang);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
    if (Objects.nonNull(getFreePaid())) {
      String trans = CommonUtils.getI18nString(i18nBundle, getFreePaid());
      if (StringUtils.isNotBlank(trans)) {
        setFreePaid(trans);
      }
    }

    if (!StringUtils.isAllBlank(eventLinkCopy, eventLinkUrl)) {
      this.eventLink = new Link(eventLinkUrl, eventLinkCopy, eventLinkTargetInNewWindow);
    }
    TagManager tagManager = resolver.adaptTo(TagManager.class);
    suitableForList = Optional.ofNullable(targetGroups)
      .map(Arrays::stream).orElse(Stream.empty())
      .map(tagManager::resolve).filter(Objects::nonNull).map(tag -> tag.getTitle(locale))
      .collect(Collectors.toList());
    activitiesList = Optional.ofNullable(activities)
      .map(Arrays::stream).orElse(Stream.empty())
      .map(tagManager::resolve).filter(Objects::nonNull).map(tag -> tag.getTitle(locale))
      .collect(Collectors.toList());

    if (Boolean.TRUE.equals(roundTheClock)) {
      // If it's around the clock
      // then we will set
      // startTimeCal = 12:00 AM
      // endTimeCal = 12:00 AM
      final Calendar now = Calendar.getInstance();
      now.set(Calendar.HOUR, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.AM_PM, Calendar.AM);
      startTimeCal = now;
      endTimeCal = now;
    }

    // If the author didn't set the following fields
    // priceText, durationText, workingTimeText,
    // activitiesText, suitableForText & accessibilityText
    // We will set a default traduction from i18n
    if (StringUtils.isEmpty(priceText)) {
      priceText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_PRICE);
    }
    if (StringUtils.isEmpty(durationText)) {
      durationText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_DURATION);
    }
    if (StringUtils.isEmpty(workingTimeText)) {
      workingTimeText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_WORKING_HOURS);
    }
    if (StringUtils.isEmpty(activitiesText)) {
      activitiesText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_CATEGORY);
    }
    if (StringUtils.isEmpty(suitableForText)) {
      suitableForText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_AUDIENCE);
    }
    if (StringUtils.isEmpty(accessibilityText)) {
      accessibilityText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_ACCESSIBILTY);
    }

    eventSummaryTitle = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_DETAIL_EVENT);
  }

  /**
   * Set Image Information.
   */
  private void setImageInfo() {
    if (Objects.nonNull(s7featureEventImage)
        && Objects.nonNull(saudiTourismConfigs)
        && s7featureEventImage.startsWith(saudiTourismConfigs.getScene7Domain())
        && !s7featureEventImage.contains(Constants.SCENE7_DOMAIN_FRAGMENT)) {
      // add crop if and only if s7banner image start with scene7 domain
      boolean isCnServer = DynamicMediaUtils.isCnServer(settingsService);
      this.featureEventImage = DynamicMediaUtils
        .getScene7ImageWithDefaultImage(s7featureEventImage, Constants.EVENT_DESKTOP_CROP, false,
          isCnServer);
      this.featureEventMobileImage = DynamicMediaUtils
        .getScene7ImageWithDefaultImage(s7featureEventImage, Constants.EVENT_MOBILE_CROP, false,
          isCnServer);
    } else {
      // If no s7 image use featureEventImage(dam image)
      this.featureEventImage = Optional.ofNullable(featureEventImage)
        .filter(imageUrl -> imageUrl.contains(Constants.SCENE7_DOMAIN_FRAGMENT))
        .map((String imageUrl) -> {
          try {
            URIBuilder uriBuilder = new URIBuilder(imageUrl);
            uriBuilder.setScheme(Constants.HTTPS_SCHEME);
            uriBuilder.addParameter("scl", "1");
            return uriBuilder.toString();
          } catch (URISyntaxException e) {
            LOGGER.error("Unable to build URI from \"{}\": ", imageUrl, e);
            return imageUrl;
          }
        })
        .orElse(featureEventImage);
      this.featureEventMobileImage = featureEventImage;
    }
    if (Objects.nonNull(saudiTourismConfigs)) {
      googleMapsKey = saudiTourismConfigs.getGoogleMapsKey();
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Calendar.class, new CalendarAdapter());
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
