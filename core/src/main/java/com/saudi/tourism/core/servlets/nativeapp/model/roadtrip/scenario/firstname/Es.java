package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.firstname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * JSON Structure ES.
 */
public class Es {

  /**
   * text.
   */
  @SerializedName("text")
  @Expose
  @Getter
  private String text;

}
