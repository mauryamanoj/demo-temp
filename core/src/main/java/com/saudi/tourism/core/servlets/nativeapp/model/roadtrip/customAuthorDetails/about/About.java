package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.about;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * About.
 */
public class About {
  /**
   * text.
   */
  @SerializedName("text")
  @Expose
  @Getter
  @Setter
  private String text;
}
