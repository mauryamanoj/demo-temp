package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.metadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * POJO of metadata of RouteBodyCustom .
 */
public class Metadata {

  /**
   * type.
   */
  @SerializedName("type")
  @Expose
  @Setter
  @Getter
  private String type;

  /**
   * length.
   */
  @SerializedName("length")
  @Expose
  @Setter
  @Getter
  private Integer length;

  /**
   * duration.
   */
  @SerializedName("duration")
  @Expose
  @Setter
  @Getter
  private Integer duration;
}
