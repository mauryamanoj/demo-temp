package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata.route.Route;
import lombok.Getter;

/**
 * Metadata of routeBoy of days.
 */
public class Metadata {
  /**
   * day.
   */
  @SerializedName("day")
  @Expose
  @Getter
  private Integer day;

  /**
   * route .
   */
  @SerializedName("route")
  @Expose
  @Getter
  private Route route;

  /**
   * startTime .
   */
  @SerializedName("start_time")
  @Expose
  @Getter
  private String startTime;

  /**
   * Duration.
   */
  @SerializedName("duration")
  @Expose
  @Getter
  private Integer duration;

}
