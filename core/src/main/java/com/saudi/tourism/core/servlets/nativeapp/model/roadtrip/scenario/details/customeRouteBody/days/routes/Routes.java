package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.routes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Routes POJO .
 */
public class Routes {

  /**
   * driving .
   */
  @SerializedName("DRIVING")
  @Expose
  @Getter
  @Setter
  private Integer driving;

  /**
   * Walking.
   */
  @SerializedName("WALKING")
  @Expose
  @Getter
  @Setter
  private Integer walking;
}
