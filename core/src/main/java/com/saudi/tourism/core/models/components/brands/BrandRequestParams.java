package com.saudi.tourism.core.models.components.brands;

import com.saudi.tourism.core.models.components.events.BaseRequestParams;
import lombok.Data;

import java.util.List;

/**
 * The Class BrandRequestParams.
 */
@Data
public class BrandRequestParams extends BaseRequestParams {

  /**
   * The keyword.
   */
  private String keyword;

  /**
   * The list of category.
   */
  private List<String> category;
  /**
   * The list of category.
   */
  private List<String> brandCategory;

  /**
   * The list of category.
   */
  private List<String> brandCity;


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
   * The latitude.
   */
  private String latitude;

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
   * The isNew.
   */
  private boolean isNew = false;

  /**
   * The isPopular.
   */
  private boolean isPopular = false;

  /**
   * The IsClose.
   */
  private boolean isClose = false;

}
