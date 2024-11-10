package com.saudi.tourism.core.models.components.contentfragment.attraction;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerImage;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerVideo;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.BannerImageAwareModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.TimingsAwareModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import com.saudi.tourism.core.services.RunModeService;
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
 * This is the Sling Model class for the Content Fragment component for Attraction. The {@link
 * Model} annotation allows us to register the class as a Sling Model.
 */
@Model(
    adaptables = {Resource.class, ContentFragment.class},
    adapters = AttractionCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AttractionCFModel implements ContentFragmentAwareModel, BannerImageAwareModel, TimingsAwareModel {
  /** Attraction ContentFragment Model. */
  @Self
  private transient ContentFragment contentFragment;

  /** Current resource. */
  @Self
  private transient Resource resource;

  /** Resource Resolver. */
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

  /** Id. */
  private String id;

  /** Title. */
  private String title;

  /** Subtitle. */
  private String subtitle;

  /** aboutDescription. */
  private String aboutDescription;

  /** Author Image. */
  private Image authorImage;

  /** banner images. */
  private List<BannerImage> bannerImages;

  /** banner videos. */
  private List<BannerVideo> bannerVideos;

  /**
   * Latitude.
   */
  private String lat;

  /**
   * Longitude.
   */
  private String lng;

  /**
   * Hide Favorite.
   */
  private Boolean hideFavorite;

  /**
   * Hide Favorite.
   */
  private Boolean hideShareIcon;

  /**
   * Location.
   */
  private DestinationCFModel destination;

  /**
   * Ticket Type.
   */
  private String ticketType;

  /**
   * Ticket Price.
   */
  private String ticketPrice;


  /**
   * Ticket Price Suffix.
   */
  private String ticketPriceSuffix;


  /**
   * Ticket Details.
   */
  private String ticketDetails;


  /**
   * Ticket CTA Link.
   */
  private Link ticketsCtaLink;

  /**
   * Free / Paid.
   */
  private String freePaid;

  /**
   * favId.
   */
  private String favId;

  /**
   * Page Link.
   */
  private Link pageLink;

  /**
   * Categories Tags.
   */
  private String[] categoriesTags;

  /**
   * Page Path.
   */
  @JsonIgnore
  private String internalPagePath;

  /**
   * Created Date.
   */
  @ValueMapValue(name = "jcr:created")
  private transient Calendar createdDate;

  /**
   * Last Replicated Date.
   */
  @ValueMapValue(name = "jcr:content/cq:lastReplicated")
  private transient Calendar lastReplicatedDate;

  /**
   * Published Date.
   */
  private transient Calendar publishedDate;

  /**
   * sameTimeAcrossWeek.
   */
  private Boolean sameTimeAcrossWeek;

  /**
   * Daily Start Time.
   */
  private Calendar dailyStartTime;
  /**
   * Daily End Time.
   */
  private Calendar dailyEndTime;

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
    id = contentFragment.adaptTo(Resource.class).getPath();
    title = getElementValue(contentFragment, "title", String.class);
    subtitle = getElementValue(contentFragment, "subtitle", String.class);
    aboutDescription = getElementValue(contentFragment, "aboutDescription", String.class);
    lat = getElementValue(contentFragment, "lat", String.class);
    lng = getElementValue(contentFragment, "lng", String.class);
    hideFavorite = getElementValue(contentFragment, "hideFavorite", Boolean.class);
    hideShareIcon = getElementValue(contentFragment, "hideShareIcon", Boolean.class);
    ticketType = getElementValue(contentFragment, "ticketType", String.class);
    ticketPrice = getElementValue(contentFragment, "ticketPrice", String.class);
    ticketPriceSuffix = getElementValue(contentFragment, "ticketPriceSuffix", String.class);
    ticketDetails = getElementValue(contentFragment, "ticketDetails", String.class);
    freePaid = getElementValue(contentFragment, "freePaid", String.class);
    categoriesTags = getElementValue(contentFragment, "categories", String[].class);
    sameTimeAcrossWeek = ObjectUtils.defaultIfNull(getElementValue(contentFragment,
        "sameTimeAcrossWeek", Boolean.class), false);
    dailyStartTime = getElementValue(contentFragment, "dailyStartTime", Calendar.class);
    dailyEndTime = getElementValue(contentFragment, "dailyEndTime", Calendar.class);
    final var timingsObjects = getElementValue(contentFragment, "timings", String[].class);
    if (ArrayUtils.isNotEmpty(timingsObjects)) {
      timings = buildTimings(timingsObjects);
    }

    ageTag = getElementValue(contentFragment, "agesValue", String.class);

    if (runModeService.isPublishRunMode()) {
      // on publish, use createdDate (lastReplicatedCal is not available).
      publishedDate = createdDate;
    }
    if (!runModeService.isPublishRunMode()) {
      // on author, use lastReplicated date if exists, else use createdDate.
      publishedDate = Optional.ofNullable(lastReplicatedDate).orElse(createdDate);
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
    internalPagePath = getElementValue(contentFragment, "pagePath", String.class);
    var pagePath = getElementValue(contentFragment, "pagePath", String.class);
    if (StringUtils.isNotEmpty(pagePath)) {
      //should be done before rewriting pagePath
      favId = LinkUtils.getFavoritePath(pagePath);
      pagePath = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), pagePath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
      pageLink = new Link();
      pageLink.setUrl(pagePath);

    }

    var locationCFPath = getElementValue(contentFragment, "locationValue", String.class);
    if (StringUtils.isNotEmpty(locationCFPath)) {
      final var destinationCF = resourceResolver.getResource(locationCFPath);

      if (Objects.nonNull(destinationCF)) {
        destination = destinationCF.adaptTo(DestinationCFModel.class);
      }
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
