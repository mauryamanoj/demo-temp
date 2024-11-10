package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.image;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Image.
 */
public class Image {
  /**
   * Id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  @Setter
  private String id;

}
