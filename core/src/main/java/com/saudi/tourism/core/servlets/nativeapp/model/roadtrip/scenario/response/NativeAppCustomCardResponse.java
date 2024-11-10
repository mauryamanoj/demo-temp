package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Custom Json Response POJO .
 */
public class NativeAppCustomCardResponse {
  /**
   * id.
   */
  @Getter
  @Setter
  private String id;
  /**
   * start_name_city.
   */
  @Setter
  @JsonProperty("start_city_name")
  @Expose
  private String startCityName;
  /**
   * transport.
   */
  @Getter
  @Setter
  private String transport;

  /**
   * author.
   */
  @Getter
  @Setter
  private String author;
  /**
   * duration.
   */
  @Getter
  @Setter
  private Integer duration;

  /**
   * car_distance.
   */
  @Getter
  @Setter
  @SerializedName("distance")
  private Integer distance;

  /**
   * startCity.
   */
  @Setter
  @JsonProperty("start_city")
  @Expose
  private String startCity;

  /**
   * name .
   */
  @Getter
  @Setter
  private String name;

  /**
   * Categories List .
   */
  @Getter
  @Setter
  @SerializedName("categories")
  private List<String> categories;

  /**
   * CategoriesListi18n .
   */
  @Getter
  @Setter
  @SerializedName("categories_i18n")
  private List<String> categoriesI18n;

  /**
   * Images Object.
   */
  @Getter
  @Setter
  @JsonProperty("images")
  private List<ImageResponse> image;
  /**
   * ScenarioId.
   */
  @Getter
  @Setter
  @JsonProperty("scenario_id")
  private String scenarioId;

  /**
   * Guidance.
   */
  @Getter
  @Setter
  @JsonProperty("guidance")
  private String guidance;
}
