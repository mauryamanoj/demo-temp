package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.metadata.route.bounds;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Bounds POJO.
 */
public class Bounds {
  /**
   * north.
   */
  @SerializedName("north")
  @Expose
  @Getter
  private Float north;

  /**
   * south .
   */
  @SerializedName("south")
  @Expose
  private Float south;

  /**
   * east.
   */
  @SerializedName("east")
  @Expose
  @Getter
  private Float east;

  /**
   * west.
   */
  @SerializedName("west")
  @Expose
  @Getter
  private Float west;
}
