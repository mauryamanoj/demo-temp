package com.saudi.tourism.core.models.components.events;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The Class FilterModel.
 */
@Data
public class EventFilterModel implements Serializable {

  /**
   * The cities.
   */
  private List<AppFilterItem> city;
  /**
   * The regions.
   */
  private List<AppFilterItem> region;
  /**
   * The categories.
   */
  private List<AppFilterItem> category;
  /**
   * The targets.
   */
  private List<AppFilterItem> target;
  /**
   * The freePaid items.
   */
  private List<AppFilterItem> freePaid;
  /**
   * The seasons.
   */
  private List<AppFilterItem> season;

  /**
   * The date periods.
   */
  private List<AppFilterItem> date;

  /**
   * The sortBy.
   */
  private List<AppFilterItem> sortBy;

  /**
   * The show pn map.
   */
  private List<AppFilterItem> showOnMap;

  /**
   * The venue.
   */
  private List<AppFilterItem> venue;
}
