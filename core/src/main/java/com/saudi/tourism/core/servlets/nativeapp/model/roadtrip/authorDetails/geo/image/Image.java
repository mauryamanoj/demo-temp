package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Image.
 */
public class Image {

  /**
   * t.
   */
  @SerializedName("t")
  @Expose
  @Getter
  private Integer t;

  /**
   * id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  private String id;
}
