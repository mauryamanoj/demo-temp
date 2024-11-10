package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.geo.image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

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
  @Setter
  private Integer t;

  /**
   * id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  @Setter
  private String id;
}
