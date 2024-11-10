package com.saudi.tourism.core.beans;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;
/**
 * Bean for the API response.
 */
@Getter
public class WinterCardsExperienceResponse {

  /**
   * Variable data.
   */
  private List<Data> data;

  /**
   * Class Data.
   */
  @Getter
  public class Data {
    /**
     * Variable id.
     */
    private String id;

    /**
     * Variable name.
     */
    private String name;

    /**
     * Variable slug.
     */
    private String slug;

    /**
     * Variable city.
     */
    private String city;

    /**
     * Variable primaryDestination.
     */
    @SerializedName("primary_destination")
    private String primaryDestination;

    /**
     * Variable secondaryDestination.
     */
    @SerializedName("secondary_destination")
    private String secondaryDestination;

    /**
     * Variable listPrice.
     */
    @SerializedName("list_price")
    private String listPrice;

    /**
     * Variable discountedPrice.
     */
    @SerializedName("discounted_price")
    private String discountedPrice;

    /**
     * Variable backgroundImage.
     */
    @SerializedName("background_image")
    private String backgroundImage;

    /**
     * Variable backgroundImages.
     */
    @SerializedName("background_images")
    private List<String> backgroundImages;

    /**
     * Variable bookOnline.
     */
    @SerializedName("book_online")
    private String bookOnline;

    /**
     * Variable minCapacity.
     */
    @SerializedName("min_capacity")
    private String minCapacity;

    /**
     * Variable maxCapacity.
     */
    @SerializedName("max_capacity")
    private String maxCapacity;

    /**
     * Variable duration.
     */
    private String duration;

    /**
     * Variable durationUnit.
     */
    @SerializedName("duration_unit")
    private String durationUnit;

    /**
     * Variable categoryName.
     */
    @SerializedName("category_name")
    private String categoryName;

    /**
     * Variable categorySlug.
     */
    @SerializedName("category_slug")
    private String categorySlug;

    /**
     * Variable contactName.
     */
    @SerializedName("contact_name")
    private String contactName;

    /**
     * Variable priceType.
     */
    @SerializedName("price_type")
    private String priceType;

    /**
     * Variable priceTypeCapacity.
     */
    @SerializedName("price_type_capacity")
    private String priceTypeCapacity;
  }
}
