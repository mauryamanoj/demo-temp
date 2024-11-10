package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.metadata.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.metadata.route.bounds.Bounds;
import lombok.Getter;
import lombok.Setter;

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
  @Setter
  private Integer duration;

  /**
   * vehicleTypes.
   */
  @SerializedName("vehicleTypes")
  @Expose
  @Getter
  @Setter
  private List<String> vehicleTypes = null;

  /**
   * distance.
   */
  @SerializedName("distance")
  @Expose
  @Getter
  @Setter
  private Integer distance;

  /**
   * polyline .
   */
  @SerializedName("polyline")
  @Expose
  @Getter
  @Setter
  private String polyline;

  /**
   * Bounds.
   */
  @SerializedName("bounds")
  @Expose
  @Getter
  @Setter
  private Bounds bounds;
}
