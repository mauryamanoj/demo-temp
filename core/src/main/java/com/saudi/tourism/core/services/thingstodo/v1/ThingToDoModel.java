package com.saudi.tourism.core.services.thingstodo.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Calendar;
import java.util.List;

/** Thing ToDo Model. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
public class ThingToDoModel {

  /**
   * Type.
   */
  private String type;

  /**
   * Id.
   */
  private String id;

  /**
   * Title.
   */
  private String title;

  /**
   * Subtitle.
   */
  private String subtitle;

  /**
   * Description.
   */
  private String description;

  /** Author Image. */
  private Image authorImage;

  /** Banner images. */
  private List<Image> bannerImages;

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
  @Builder.Default
  private Boolean hideFavorite = false;

  /**
   * Hide Share Icon.
   */
  @Builder.Default
  private Boolean hideShareIcon = false;

  /**
   * Location.
   */
  private Destination destination;

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
   * Page Link.
   */
  private Link pageLink;

  /**
   * favId.
   */
  private String favId;

  /**
   * Categories Tags.
   */
  @JsonIgnore
  private transient List<String> categoriesTags;

  /**
   * Published Date.
   */
  @JsonIgnore
  private transient Calendar publishedDate;

  /**
   * Categories Tags.
   */
  private  List<Category> categories;

  /**
   * Season.
   */
  @JsonIgnore
  private Season season;

  /**
   * Internal Page Path.
   */
  @JsonIgnore
  private String pagePath;

  /**
   * Start Date.
   */
  private Calendar startDate;

  /**
   * End Date.
   */
  private Calendar endDate;

  /**
   * Period Days.
   */
  @Getter
  @JsonIgnore
  private transient Long periodDays;

  /**
   * Type Value.
   */
  private String typeValue;

  /**
   * sameTimeAcrossWeek.
   */
  @Builder.Default
  private Boolean sameTimeAcrossWeek = false;

  /**
   * Daily Start Time .
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
   * ageTag.
   */
  @JsonIgnore
  private String ageTag;

  /**
   * age.
   */
  private String age;

  /**
   * idealFor.
   */
  private List<Category> idealFor;

  /**
   * Destination.
   */
  @Builder
  @Data
  public static class Destination {
    /**
     * Destination id.
     */
    @JsonIgnore
    private String id;

    /**
     * Destination Title.
     */
    private String title;

    /**
     * Destination path.
     */
    private String path;

    public static final Destination fromCFModel(final DestinationCFModel model) {
      if (model == null) {
        return null;
      }

      return Destination.builder()
        .id(model.getDestinationId())
        .title(model.getTitle())
        .path(model.getResource().getPath())
        .build();
    }
  }


  /**
   * Season.
   */
  @Builder
  @Data
  public static class Season {
    /**
     * Season Title.
     */
    private String title;

    /**
     * Season path.
     */
    private String path;

    public static final Season fromCFModel(final SeasonCFModel model) {
      if (model == null) {
        return null;
      }

      return Season.builder()
        .title(model.getTitle())
        .path(model.getResource().getPath())
        .build();
    }
  }
}
