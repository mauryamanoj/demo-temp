package com.saudi.tourism.core.beans.bestexperience;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Experience Data.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperienceData {

  /**
   * ID.
   */
  private String id;

  /**
   * name.
   */
  private String name;

  /**
   * Slug.
   */
  @JsonIgnore
  private String slug;

  /**
   * CityId.
   */
  @SerializedName("city_id")
  @JsonIgnore
  private String cityId;

  /**
   * city.
   */
  private String city;

  /**
   * Latitude.
   */
  @JsonIgnore
  private String latitude;

  /**
   * Longitude.
   */
  @JsonIgnore
  private String longitude;

  /**
   * Primary Destination.
   */
  @SerializedName("primary_destination")
  @JsonIgnore
  private String primaryDestination;

  /**
   * Secondary Destination.
   */
  @SerializedName("secondary_destination")
  @JsonIgnore
  private String secondaryDestination;

  /**
   * Images.
   */
  @JsonIgnore
  private List<String> images;

  /**
   * Included.
   */
  @JsonIgnore
  private List<Map<String, String>> included;

  /**
   * Excluded.
   */
  @JsonIgnore
  private List<Map<String, String>> excluded;

  /**
   * Age from.
   */
  @SerializedName("age_from")
  @JsonIgnore
  private String ageFrom;

  /**
   * Age to.
   */
  @SerializedName("age_to")
  @JsonIgnore
  private String ageTo;

  /**
   * Refund Policy.
   */
  @SerializedName("refund_policy")
  @JsonIgnore
  private String refundPolicy;

  /**
   * Refund Policy Details.
   */
  @SerializedName("refund_policy_details")
  @JsonIgnore
  private Map<String, String> refundPolicyDetails;

  /**
   * Latitude, Longitude.
   */
  @JsonIgnore
  private String latlng;

  /**
   * description.
   */
  private String description;

  /**
   * list_price as double.
   */
  @SerializedName("list_price")
  @JsonIgnore
  private double listPriceDouble;

  /**
   * List Price.
   */
  private int listPrice;

  /**
   * Background Image.
   */
  @SerializedName("background_image")
  private String backgroundImage;

  /**
   * startDate.
   */
  @SerializedName("start_date")
  private String startDate;

  /**
   * endDate.
   */
  @SerializedName("end_date")
  private String endDate;

  /**
   * is_dmc.
   */
  @SerializedName("is_dmc")
  @JsonIgnore
  private String isDmc;

  /**
   * package_url.
   */
  @SerializedName("package_url")
  private String packageUrl;

  /**
   * discountedPrice.
   */
  @SerializedName("price_start_from")
  @JsonIgnore
  private String originalPrice;

  /**
   * finalPrice.
   */
  @SerializedName("discounted_price")
  @JsonIgnore
  private String finalPrice;

  /**
   * priceTier.
   */
  @SerializedName("price_type")
  @JsonIgnore
  private String priceTier;

  /**
   * Category Slug.
   */
  @SerializedName("category_slug")
  @JsonIgnore
  private String categorySlug;

  /**
   * Category Name.
   */
  @SerializedName("category_name")
  @JsonIgnore
  private String categoryName;

  /**
   * get list price.
   * @return list price
   */
  public int getListPrice() {
    return (int) Math.round(listPriceDouble);
  }
}
