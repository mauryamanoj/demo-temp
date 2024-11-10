package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.CategoriesI18n;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.image.Image;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.localnames.LocalNames;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.StartCityName;
import lombok.Getter;
import java.util.List;

/**
 * Scenario .
 */
public class Scenario {
  /**
   * start_city.
   */
  @Getter
  @SerializedName("start_city")
  @Expose
  private String startCity;

  /**
   * transport.
   */
  @Getter
  @SerializedName("transport")
  @Expose
  private String transport;

  /**
   * start_city_name.
   */
  @Getter
  @SerializedName("start_city_name")
  @Expose
  private StartCityName startCityName;

  /**
   * duration.
   */
  @Getter
  @SerializedName("duration")
  @Expose
  private Integer duration;

  /**
   * uid.
   */
  @Getter
  @SerializedName("uid")
  @Expose
  private String uid;

  /**
   * sum_driving_distance.
   */
  @Getter
  @SerializedName("sum_distance")
  @Expose
  private Integer sumDistance;

  /**
   * sum_walking_distance.
   */
  @Getter
  @SerializedName("sum_walking_distance")
  @Expose
  private Integer sumWalkingDistance;

  /**
   * sum_bicycling_distance .
   */
  @Getter
  @SerializedName("sum_bicycling_distance")
  @Expose
  private Integer sumBicyclingDistance;

  /**
   * categories.
   */
  @Getter
  @SerializedName("categories")
  @Expose
  private List<String> categories;

  /**
   * categories_i18n.
   */
  @Getter
  @SerializedName("categories_i18n")
  @Expose
  private List<CategoriesI18n> categoriesI18n;

  /**
   * Localnames.
   */
  @Getter
  @SerializedName("localnames")
  @Expose
  private LocalNames localNames;

  /**
   * Images .
   */
  @Getter
  @SerializedName("images")
  @Expose
  private List<Image> images;

  /**
   * ScenarioId.
   */
  @Getter
  @Expose
  @SerializedName("scenario_id")
  private String scenarioId;

  /**
   * guidance.
   */
  @Getter
  @Expose
  @SerializedName("guidance")
  private String guidance;
}
