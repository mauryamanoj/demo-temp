package com.saudi.tourism.core.models.components.contentfragment.event;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.Externalizer;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerImage;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerVideo;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.BannerImageAwareModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.TimingsAwareModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This is the Sling Model class for the Content Fragment component for Event. The {@link Model}
 * annotation allows us to register the class as a Sling Model.
 */
@Model(
    adaptables = {Resource.class, ContentFragment.class},
    adapters = EventCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EventCFModel implements ContentFragmentAwareModel, BannerImageAwareModel, TimingsAwareModel {
  /**
   * Event ContentFragment Model.
   */
  @Self
  private transient ContentFragment contentFragment;

  /**
   * Current resource.
   */
  @Self
  private transient Resource resource;

  /**
   * Resource Resolver.
   */
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @Inject
  private transient SlingSettingsService settingsService;

  /**
   * Run Mode Service.
   */
  @Inject
  private transient RunModeService runModeService;

  /**
   * Created Date.
   */
  @ValueMapValue(name = "jcr:created")
  private transient Calendar createdDate;

  /**
   * jcr:lastModified.
   */
  @ValueMapValue(name = "jcr:content/jcr:lastModified")
  private transient Calendar lastModified;

  /**
   * Last Replicated Date.
   */
  @ValueMapValue(name = "jcr:content/cq:lastReplicated")
  private transient Calendar lastReplicatedDate;

  /**
   * Event title.
   */
  private String title;

  /**
   * Event subtitle.
   */
  private String subtitle;

  /**
   * Event Image.
   */
  private Image titleImage;

  /**
   * Event Author Image.
   */
  private Image authorImage;

  /**
   * Event banner images.
   */
  private List<BannerImage> bannerImages;

  /**
   * Event banner videos.
   */
  private List<BannerVideo> bannerVideos;

  /**
   * Event Latitude.
   */
  private String lat;

  /**
   * Event Longitude.
   */
  private String lng;

  /**
   * Event Start Date.
   */
  private Calendar startDate;

  /**
   * Event End Date.
   */
  private Calendar endDate;

  /**
   * Event coming soon label.
   */
  private String comingSoonLabel;

  /**
   * Event expired label.
   */
  private String expiredLabel;

  /**
   * Event Hide Favorite.
   */
  private Boolean hideFavorite;

  /**
   * Event Hide Favorite.
   */
  private Boolean hideShareIcon;

  /**
   * Event Location.
   */
  private DestinationCFModel destination;

  /**
   * Event season.
   */
  private SeasonCFModel season;

  /**
   * Event Ticket Type.
   */
  private String ticketType;

  /**
   * Event Ticket Price.
   */
  private String ticketPrice;

  /**
   * Event Ticket Price Suffix.
   */
  private String ticketPriceSuffix;

  /**
   * Event Ticket Details.
   */
  private String ticketDetails;

  /**
   * Event Ticket CTA Link.
   */
  private Link ticketsCtaLink;

  /**
   * Event Free / Paid.
   */
  private String freePaid;

  /**
   * Event Type.
   */
  private String eventType;

  /**
   * Event period.
   */
  private Long periodDays;

  /**
   * Categories Tags.
   */
  private String[] categoriesTags;

  /**
   * Event path.
   */
  private String path;

  /**
   * Event web path.
   */
  private String longWebpath;

  /**
   * Published Date.
   */
  private transient Calendar publishedDate;

  /**
   * Page Link.
   */
  private Link pageLink;
  /**
   * favId.
   */
  private String favId;
  /**
   * Ticket CTA Link.
   */
  private String ticketCTALink;
  /**
   * Event Description.
   */
  private String eventDescription;
  /**
   * Image360.
   */
  private String image360;
  /**
   * Daily Start Time.
   */
  private Calendar dailyStartTime;
  /**
   * Daily End Time.
   */
  private Calendar dailyEndTime;
  /**
   * Event Id.
   */
  private String id;
  /**
   * Season ID.
   */
  private String seasonId;
  /**
   * City Id.
   */
  private String cityId;
  /**
   * target Group Tags.
   */
  private String[] targetGroupTags;
  /**
   * same Time Across Week.
   */
  private Boolean sameTimeAcrossWeek;
  /**
   * featured InMap.
   */
  private Boolean featuredInMap;
  /**
   * weekend Event.
   */
  private Boolean weekendEvent;
  /**
   * Timings.
   */
  private List<OpeningHoursValue> timings;

  /**
   * Age Tag.
   */
  private String ageTag;

  @PostConstruct
  void init() {
    path = resource.getPath();
    title = getElementValue(contentFragment, "title", String.class);
    subtitle = getElementValue(contentFragment, "subtitle", String.class);
    lat = getElementValue(contentFragment, "lat", String.class);
    lng = getElementValue(contentFragment, "lng", String.class);
    startDate = getElementValue(contentFragment, "startDate", Calendar.class);
    endDate = getElementValue(contentFragment, "endDate", Calendar.class);
    hideFavorite = getElementValue(contentFragment, "hideFavorite", Boolean.class);
    hideShareIcon = getElementValue(contentFragment, "hideShareIcon", Boolean.class);
    ticketType = getElementValue(contentFragment, "ticketType", String.class);
    ticketPrice = getElementValue(contentFragment, "ticketPrice", String.class);
    ticketPriceSuffix = getElementValue(contentFragment, "ticketPriceSuffix", String.class);
    ticketDetails = getElementValue(contentFragment, "ticketDetails", String.class);
    freePaid = getElementValue(contentFragment, "freePaid", String.class);
    eventType = getElementValue(contentFragment, "eventType", String.class);
    periodDays = CommonUtils.calculateDaysBetween(startDate, endDate);
    categoriesTags = getElementValue(contentFragment, "categories", String[].class);
    targetGroupTags = getElementValue(contentFragment, "agesValue", String[].class);
    weekendEvent = ObjectUtils.defaultIfNull(getElementValue(contentFragment, "weekendEvent", Boolean.class),
      false);
    ticketCTALink = getElementValue(contentFragment, "ticketCTALink", String.class);
    eventDescription = getElementValue(contentFragment, "aboutDescription", String.class);
    sameTimeAcrossWeek = ObjectUtils.defaultIfNull(getElementValue(contentFragment, "sameTimeAcrossWeek",
        Boolean.class), false);
    featuredInMap = ObjectUtils.defaultIfNull(getElementValue(contentFragment, "featuredInMap", Boolean.class),
      false);
    dailyStartTime = getElementValue(contentFragment, "dailyStartTime", Calendar.class);
    dailyEndTime = getElementValue(contentFragment, "dailyEndTime", Calendar.class);
    image360 = getElementValue(contentFragment, "image360", String.class);
    id = contentFragment.adaptTo(Resource.class).getPath();

    var pagePath = getElementValue(contentFragment, "pagePath", String.class);

    longWebpath = pagePath;

    if (StringUtils.isNotEmpty(pagePath)) {
      //should be done before rewriting pagePath
      favId = LinkUtils.getFavoritePath(pagePath);
      pagePath = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), pagePath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
      pageLink = new Link();
      pageLink.setUrl(pagePath);
    }

    if (runModeService.isPublishRunMode()) {
      // on publish, use createdDate (lastReplicatedCal is not available).
      publishedDate = createdDate;
    }
    if (!runModeService.isPublishRunMode()) {
      // on author, use lastReplicated date if exists, else use createdDate.
      publishedDate = Optional.ofNullable(lastReplicatedDate).orElse(createdDate);
    }

    var destinationPath = getElementValue(contentFragment, "locationValue", String.class);
    if (StringUtils.isNotEmpty(destinationPath)) {
      cityId = StringUtils.substringAfterLast(destinationPath, "/");
    }

    var seasonPath = getElementValue(contentFragment, "season", String.class);
    if (StringUtils.isNotEmpty(seasonPath)) {
      seasonId = seasonPath.split("/")[seasonPath.split("/").length - 1];
    }

    final var timingsObjects = getElementValue(contentFragment, "timings", String[].class);
    if (ArrayUtils.isNotEmpty(timingsObjects)) {
      timings = buildTimings(timingsObjects);
    }

    var ticketCtaLinkPath = getElementValue(contentFragment, "ticketCTALink", String.class);
    var ticketCtaLabel = getElementValue(contentFragment, "ticketCTALabel", String.class);
    if (StringUtils.isNotEmpty(ticketCtaLinkPath)) {
      ticketCtaLinkPath = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), ticketCtaLinkPath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
      ticketsCtaLink = new Link();
      ticketsCtaLink.setText(ticketCtaLabel);
      ticketsCtaLink.setUrl(ticketCtaLinkPath);
    }

    var locationCFPath = getElementValue(contentFragment, "locationValue", String.class);

    ageTag = getElementValue(contentFragment, "agesValue", String.class);

    if (StringUtils.isNotEmpty(locationCFPath)) {
      final var destinationCF = resourceResolver.getResource(locationCFPath);

      if (Objects.nonNull(destinationCF)) {
        destination = destinationCF.adaptTo(DestinationCFModel.class);
      }
    }

    var seasonCFPath = getElementValue(contentFragment, "season", String.class);
    if (StringUtils.isNotEmpty(seasonCFPath)) {
      final var seasonCF = resourceResolver.getResource(seasonCFPath);

      if (Objects.nonNull(seasonCF)) {
        season = seasonCF.adaptTo(SeasonCFModel.class);
      }
    }

    var titleImagePath = getElementValue(contentFragment, "titleImage", String.class);
    var s7titleImagePath = getElementValue(contentFragment, "s7titleImage", String.class);
    var altTitleImage = getElementValue(contentFragment, "altTitleImage", String.class);
    if (StringUtils.isNotEmpty(titleImagePath)) {
      titleImage = new Image();
      titleImage.setFileReference(titleImagePath);
      titleImage.setS7fileReference(s7titleImagePath);
      titleImage.setAlt(altTitleImage);
    }

    var authorImagePath = getElementValue(contentFragment, "image", String.class);
    var s7authorImagePath = getElementValue(contentFragment, "s7image", String.class);
    var altAuthorImage = getElementValue(contentFragment, "alt", String.class);
    if (StringUtils.isNotEmpty(authorImagePath)) {
      authorImage = new Image();
      authorImage.setFileReference(authorImagePath);
      authorImage.setS7fileReference(s7authorImagePath);
      authorImage.setAlt(altAuthorImage);
    }

    final var bannerImagesObjects = getElementValue(contentFragment, "images", String[].class);
    if (ArrayUtils.isNotEmpty(bannerImagesObjects)) {
      bannerImages = buildBannerImages(bannerImagesObjects);
      bannerVideos = buildBannerVideos(bannerImagesObjects);
    }
  }
}
