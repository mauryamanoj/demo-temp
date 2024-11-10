package com.saudi.tourism.core.models.components.events;

import lombok.Data;

import java.util.List;

/**
 * The Class EventsRequestParams.
 */
@Data
public class EventsRequestParams extends BaseRequestParams {

  /**
   * The keyword.
   */
  private String keyword;

  /**
   * The list of category.
   */
  private List<String> category;

  /**
   * The path.
   */
  private String path;

  /**
   * The list of city.
   */
  private List<String> city;

  /**
   * The list if region.
   */
  private List<String> region;

  /**
   * The cityRegion.
   */
  private String cityRegion;

  /**
   * The freePaid.
   */
  private String freePaid;

  /**
   * The list of season.
   */
  private List<String> season;

  /**
   * The list of target.
   */
  private List<String> target;

  /**
   * The date.
   */
  private String startDate;

  /**
   * The enddate.
   */
  private String endDate;

  /**
   * The source.
   */
  private String source;

  /**
   * The currencies.
   */
  private String currencies;

  /**
   * The lattitude.
   */
  private String lattitude;

  /**
   * The longitude.
   */
  private String longitude;

  /**
   * The budget.
   */
  private String budget;

  /**
   * The duration.
   */
  private String duration;

  /**
   * The area.
   */
  private String area;

  /**
   * The list of sortBy.
   */
  private List<String> sortBy;

  /**
   * The sortCity.
   */
  private String sortCity;
  /**
   * The show on map.
   */
  private String showOnMap;

  /**
   * The featured.
   */
  private String featured;

  /**
   * isTop.
   */
  private String isTop;

  /**
   * The venue.
   */
  private List<String> venue;

  /**
   * id of the Event.
   */
  private String id;

  /**
   * channel.
   */
  private String channel;
}
