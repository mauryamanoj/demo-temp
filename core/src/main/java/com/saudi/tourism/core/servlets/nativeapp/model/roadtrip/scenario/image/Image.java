package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Image Class POJO.
 */
public class Image {
  /**
   * uid.
   */
  @SerializedName("uid")
  @Expose
  @Getter
  private String uid;

  /**
   * id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  private String id;
  /**
   * day.
   */
  @SerializedName("day")
  @Expose
  @Getter
  private Integer day;
}
