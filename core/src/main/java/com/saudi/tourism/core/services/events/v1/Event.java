package com.saudi.tourism.core.services.events.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Event.
 */
@Builder
@Data
@With
public class Event {

  /**
   * Type.
   */
  private String type;

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
  private List<Image> bannerImages;

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
   * Season Id.
   */
  private String seasonId;

  /**
   * City Id.
   */
  private String cityId;

  /**
   * Image 360.
   */
  private String image360;

  /**
   * Event Description.
   */
  private String eventDescription;

  /**
   * Ticket CTA Link .
   */
  private String ticketCTALink;

  /**
   * Daily Start Time .
   */
  private Calendar dailyStartTime;

  /**
   * Daily End Time.
   */
  private Calendar dailyEndTime;

  /**
   * Last Modified.
   */
  private Calendar lastModified;

  /**
   * Created Date.
   */
  private Calendar createdDate;

  /**
   * Timings.
   */
  private List<OpeningHoursValue> timings;

  /**
   * Is Weekend Event.
   */
  private Boolean weekendEvent;

  /**
   * Same Time Across Week.
   */
  private Boolean sameTimeAcrossWeek;

  /**
   * Featured In Map.
   */
  private Boolean featuredInMap;

  /**
   * Target Group Tags.
   */
  private List<String> targetGroupTags;

  /**
   * Event Hide Favorite.
   */
  private Boolean hideFavorite;

  /**
   * Event Hide Favorite.
   */
  private Boolean hideShareIcon;

  /**
   * Event season.
   */
  private Season season;

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
   * Event location.
   */
  private Destination destination;

  /**
   * Event period.
   */
  @Getter
  @JsonIgnore
  private transient Long periodDays;

  /**
   * Page Link.
   */
  private Link pageLink;

  /**
   * favId.
   */
  private String favId;
  /**
   * event id.
   */
  private String id;

  /**
   * Categories Tags.
   */
  private List<Category> categories;

  /**
   * Categories Tags.
   */
  @JsonIgnore
  private transient List<String> categoriesTags;

  /**
   * Event page path.
   */
  @JsonIgnore
  private String pagePath;

  /**
   * Published Date.
   */
  @Getter
  @JsonIgnore
  private transient Calendar publishedDate;

  public static final Event fromCFModel(final EventCFModel model) {
    if (model == null) {
      return null;
    }

    return Event.builder()
                .type("event")
                .title(model.getTitle())
                .subtitle(model.getSubtitle())
                .titleImage(model.getTitleImage())
                .authorImage(model.getAuthorImage())
                .bannerImages(CollectionUtils.emptyIfNull(model.getBannerImages())
                                             .stream()
                                             .map(i -> i.getImage())
                                             .collect(Collectors.toList()))
                .timings(CollectionUtils.emptyIfNull(model.getTimings())
                                        .stream()
                                        .collect(Collectors.toList()))
                .lat(model.getLat())
                .lng(model.getLng())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .dailyStartTime(model.getDailyStartTime())
                .dailyEndTime(model.getDailyEndTime())
                .lastModified(model.getLastModified())
                .createdDate(model.getCreatedDate())
                .hideFavorite(model.getHideFavorite())
                .hideShareIcon(model.getHideShareIcon())
                .ticketType(model.getTicketType())
                .ticketPrice(model.getTicketPrice())
                .ticketPriceSuffix(model.getTicketPriceSuffix())
                .ticketDetails(model.getTicketDetails())
                .ticketsCtaLink(model.getTicketsCtaLink())
                .pageLink(model.getPageLink())
                .favId(model.getFavId())
                .freePaid(model.getFreePaid())
                .eventType(model.getEventType())
                .periodDays(model.getPeriodDays())
                .season(Season.fromCFModel(model.getSeason()))
                .destination(Destination.fromCFModel(model.getDestination()))
                .publishedDate(model.getPublishedDate())
                .seasonId(model.getSeasonId())
                .cityId(model.getCityId())
                .eventDescription(model.getEventDescription())
                .ticketCTALink(model.getTicketCTALink())
                .image360(model.getImage360())
                .weekendEvent(model.getWeekendEvent())
                .featuredInMap(model.getFeaturedInMap())
                .sameTimeAcrossWeek(model.getSameTimeAcrossWeek())
                .id(model.getId())
                .pagePath(model.getLongWebpath())
                .categoriesTags(
                  Optional.ofNullable(model.getCategoriesTags())
                          .map(Arrays::stream)
                          .orElseGet(Stream::empty)
                          .collect(Collectors.toList()))
                .targetGroupTags(Optional.ofNullable(model.getTargetGroupTags())
                                         .map(Arrays::stream)
                                         .orElseGet(Stream::empty)
                                         .collect(Collectors.toList()))
                .build();
  }
}
