package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.CustomDays;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.metadata.Metadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * RouteBodyCustom POJO Class.
 */
public class RouteBody {

  /**
   * MetaData POJO .
   */
  @SerializedName("metadata")
  @Expose
  @Setter
  @Getter
  private Metadata metadata;

  /**
   * Description POJO .
   */
  @SerializedName("description")
  @Expose
  @Setter
  @Getter
  private String description;

  /**
   * startCityCoordinates .
   */
  @SerializedName("start_city_coordinates")
  @Expose
  @Setter
  @Getter
  private List<Float> startCityCoordinates = null;

  /**
   * pageViews.
   */
  @Setter
  @SerializedName("page_views")
  @Expose
  @Getter
  private Integer pageViews;

  /**
   * days.
   */

  @SerializedName("days")
  @Expose
  @Getter
  @Setter
  private List<List<CustomDays>> days = null;
  /**
   * categories.
   */
  @SerializedName("categories")
  @Expose
  @Setter
  @Getter
  private List<String> categories = null;

  /**
   * photo.
   */
  @SerializedName("photo")
  @Expose
  @Setter
  @Getter
  private String photo;

  /**
   * Highlights.
   */
  @SerializedName("highlights")
  @Expose
  @Setter
  @Getter
  private String highlights;

  /**
   * Start City Name.
   */
  @SerializedName("start_city_name")
  @Expose
  @Setter
  @Getter
  private String startCityName;

  /**
   * Id.
   */
  @SerializedName("id")
  @Expose
  @Setter
  @Getter
  private String id;

  /**
   * author_business.
   */
  @SerializedName("author_business")
  @Expose
  @Setter
  @Getter
  private String authorBusiness;

  /**
   * author_name.
   */
  @SerializedName("author_name")
  @Expose
  @Setter
  @Getter
  private String authorName;

  /**
   * author_Image.
   */
  @SerializedName("author_image")
  @Expose
  @Setter
  @Getter
  private String authorImage;

  /**
   * tags.
   */
  @SerializedName("tags")
  @Expose
  @Setter
  @Getter
  private List<String> tags = null;

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Setter
  @Getter
  private String name;

  /**
   * start city .
   */
  @Setter
  @SerializedName("start_city")
  @Expose
  @Getter
  private String startCity;


}
