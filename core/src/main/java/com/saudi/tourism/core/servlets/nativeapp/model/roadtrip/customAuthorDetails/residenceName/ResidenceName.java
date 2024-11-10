package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.residenceName;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * ResidenceName POJO.
 */
public class ResidenceName {
  /**
   * text.
   */
  @SerializedName("text")
  @Expose
  @Getter
  @Setter
  private String text;
}
