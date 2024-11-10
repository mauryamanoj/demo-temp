package com.saudi.tourism.core.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * WinterCardsData for setting response data .
 */
@Getter
@Setter
public class WinterCardsData {

  /**
   * variable name .
   */
  @Expose
  private String name;

  /**
   * variable id .
   */
  @Expose
  private String id;

  /**
   * variable price .
   */
  @Expose
  private String price;
  /**
   * bookOnline .
   */
  @Expose
  private String bookOnline;

  /**
   * variable discountPrice .
   */
  @Expose
  private String discountPrice;

  /**
   * variable priceType .
   */
  @Expose
  @SerializedName("price_type")
  private String priceType;

  /**
   * variable contactName .
   */
  @Expose
  @SerializedName("contact_name")
  private String contactName;

  /**
   * variable city .
   */
  @Expose
  private String city;

  /**
   * variable images .
   */
  @Expose
  private List<String> images;

  /**
   * variable backgroundImage .
   */
  @Expose
  private String backgroundImage;

  /**
   * variable description .
   */
  @Expose
  private String description;

  /**
   * variable duration .
   */
  @Expose
  private String duration;

  /**
   * Variable primaryDestination.
   */
  @Expose
  @SerializedName("primary_destination")
  private String primaryDestination;

  /**
   * variable duration unit .
   */
  @Expose
  @SerializedName("duration_unit")
  private String durationUnit;

  /**
   * Variable categorySlug.
   */
  @Expose
  @SerializedName("category_slug")
  private String categorySlug;

  /**
   * Variable detailPagePath.
   */
  @Expose
  private String detailPagePath;

}
