package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.author.Author;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.category.Category;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.Day;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.description.Description;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.highlights.Highlights;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.metadata.Metadata;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.startcityname.StartCityName;
import lombok.Getter;

import java.util.List;

/**
 * RouteBodyCustom POJO Class.
 */
public class RouteBodyCustom {

  /**
   * MetaData POJO .
   */
  @SerializedName("metadata")
  @Expose
  @Getter
  private Metadata metadata;

  /**
   * Description POJO .
   */
  @SerializedName("description")
  @Expose
  @Getter
  private Description description;

  /**
   * startCityCoordinates .
   */
  @SerializedName("start_city_coordinates")
  @Expose
  @Getter
  private List<Float> startCityCoordinates = null;

  /**
   * pageViews.
   */
  @Getter
  @SerializedName("page_views")
  @Expose
  private Integer pageViews;

  /**
   * days.
   */
  @SerializedName("days")
  @Expose
  @Getter
  private List<List<Day>> days = null;

  /**
   * categories.
   */
  @SerializedName("categories")
  @Expose
  @Getter
  private List<Category> categories = null;

  /**
   * photo.
   */
  @SerializedName("photo")
  @Expose
  @Getter
  private String photo;

  /**
   * Highlights.
   */
  @SerializedName("highlights")
  @Expose
  @Getter
  private Highlights highlights;

  /**
   * Start City Name.
   */
  @SerializedName("start_city_name")
  @Expose
  @Getter
  private StartCityName startCityName;

  /**
   * Id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  private String id;

  /**
   * author.
   */
  @SerializedName("author")
  @Expose
  @Getter
  private Author author;

  /**
   * tags.
   */
  @SerializedName("tags")
  @Expose
  @Getter
  private List<String> tags = null;

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Getter
  private Name name;

  /**
   * start city .
   */
  @Getter
  @SerializedName("start_city")
  @Expose
  private String startCity;

}
