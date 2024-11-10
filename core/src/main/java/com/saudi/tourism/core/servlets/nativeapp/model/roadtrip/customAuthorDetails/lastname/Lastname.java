package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.lastname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Lastname POJO.
 */
public class Lastname {
  /**
   * text.
   */
  @SerializedName("text")
  @Expose
  @Getter
  @Setter
  private String text;
}
