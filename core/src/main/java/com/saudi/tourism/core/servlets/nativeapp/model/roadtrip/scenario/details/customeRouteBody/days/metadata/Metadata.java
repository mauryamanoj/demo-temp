package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.metadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.metadata.route.Route;
import lombok.Getter;
import lombok.Setter;

/**
 * MetaData of custom routeBody Days Pojo.
 */
public class Metadata {
  /**
   * day.
   */
  @SerializedName("day")
  @Expose
  @Getter
  @Setter
  private Integer day;

  /**
   * route .
   */
  @SerializedName("route")
  @Expose
  @Getter
  @Setter
  private Route route;

  /**
   * startTime .
   */
  @SerializedName("start_time")
  @Expose
  @Getter
  @Setter
  private String startTime;

  /**
   * Duration.
   */
  @SerializedName("duration")
  @Expose
  @Getter
  @Setter
  private Integer duration;

}
