package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata.route.bounds.Bounds;
import lombok.Getter;

import java.util.List;

/**
 * Route Pojo.
 */
public class Route {
  /**
   * duration.
   */
  @SerializedName("duration")
  @Expose
  @Getter
  private Integer duration;

  /**
   * vehicleTypes.
   */
  @SerializedName("vehicleTypes")
  @Expose
  @Getter
  private List<String> vehicleTypes = null;

  /**
   * distance.
   */
  @SerializedName("distance")
  @Expose
  @Getter
  private Integer distance;

  /**
   * polyline .
   */
  @SerializedName("polyline")
  @Expose
  @Getter
  private String polyline;

  /**
   * Bounds.
   */
  @SerializedName("bounds")
  @Expose
  @Getter
  private Bounds bounds;
}