package com.saudi.tourism.core.models.components.events;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Single Event Card.
 */
@Getter
@Setter
public class MultiEventsCard {

  /**
   * season details.
   */
  private String title;
  /**
   * season details.
   */
  private String description;
  /**
   * start date.
   */
  private String fileRefImage;

  /**
   * end date.
   */
  private String s7Image;
  /**
   * end date.
   */
  private String altText;

  /**
   * event image paths.
   */
  private List<String> eventImages;

  /**
   * event image paths.
   */
  private int eventCount;
  /**
   * season id.
   */
  private String seasonsId;

  /**
   * filters page.
   */
  private String filtersPath;

  /**
   * start year.
   */
  private int startYear;

  /**
   * Start Month Display Name.
   */
  private String startMonthDisplayName;

  /**
   * start date.
   */
  private String startDate;

  /**
   * end year.
   */
  private int endYear;

  /**
   * End Month Display Name.
   */
  private String endMonthDisplayName;

  /**
   * end date.
   */
  private String endDate;

}
