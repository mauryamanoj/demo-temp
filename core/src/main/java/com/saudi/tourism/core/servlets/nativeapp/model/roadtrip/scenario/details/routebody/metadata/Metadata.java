package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.metadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Metadata of RouteBodyCustom.
 */
public class Metadata {

  /**
   * type.
   */
  @SerializedName("type")
  @Expose
  @Getter
  private String type;

  /**
   * length.
   */
  @SerializedName("length")
  @Expose
  @Getter
  private Integer length;

  /**
   * duration.
   */
  @SerializedName("duration")
  @Expose
  @Getter
  private Integer duration;
}
