package com.saudi.tourism.core.models.components.packages;

import com.saudi.tourism.core.models.components.events.BaseRequestParams;
import lombok.Data;

import java.util.List;

/**
 * The Class EventsRequestParams.
 */
@Data
public class PackagesRequestParams extends BaseRequestParams {

  /**
   * The duration.
   */
  private List<String> duration;

  /**
   * The area.
   */
  private List<String> area;

  /**
   * The keyword.
   */
  private String keyword;

  /**
   * The category.
   */
  private List<String> category;

  /**
   * The path.
   */
  private String path;

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
   * The city.
   */
  private String city;

  /**
   * The models.
   */
  private String region;

  /**
   * The cityRegion.
   */
  private String cityRegion;

  /**
   * The freePaid.
   */
  private String freePaid;

  /**
   * The season.
   */
  private String season;

  /**
   * The target.
   */
  private List<String> target;

  /**
   * The date.
   */
  private String date;

  /**
   * The enddate.
   */
  private String enddate;

  /**
   * The minimum price.
   */
  private String minPrice;

  /**
   * The maximum price.
   */
  private String maxPrice;


}
