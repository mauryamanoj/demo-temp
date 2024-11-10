package com.saudi.tourism.core.models.components.events;


import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Single Event Card.
 */
@Getter
@Setter
public class EventsCard {

  /**
   * season details.
   */
  @Expose
  private String title;
  /**
   * season details.
   */
  @Expose
  private String description;

  /**
   * Image.
   */
  @Expose
  private Image image;

  /**
   * end date.
   */
  @Expose
  private String altText;

  /**
   * event image paths.
   */
  @Expose
  private List<Image> eventImages;

  /**
   * event image paths.
   */
  @Expose
  private int eventCount;
  /**
   * events text.
   */
  @Expose
  private String eventsText;
  /**
   * season id.
   */
  @Expose
  private String seasonsId;
  /**
   * filters page.
   */
  @Expose
  private String filtersPath;

  /**
   * start year.
   */
  @Expose
  private int startYear;

  /**
   * Start Month Display Name.
   */
  @Expose
  private String startMonthDisplayName;

  /**
   * start date.
   */
  @Expose
  private String startDate;

  /**
   * end year.
   */
  @Expose
  private int endYear;

  /**
   * End Month Display Name.
   */
  @Expose
  private String endMonthDisplayName;

  /**
   * end date.
   */
  @Expose
  private String endDate;

}
