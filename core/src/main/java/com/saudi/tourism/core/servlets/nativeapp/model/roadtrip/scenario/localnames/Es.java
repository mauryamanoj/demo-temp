package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.localnames;

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
  @Getter
  @SerializedName("text")
  @Expose
  private String text;
}
