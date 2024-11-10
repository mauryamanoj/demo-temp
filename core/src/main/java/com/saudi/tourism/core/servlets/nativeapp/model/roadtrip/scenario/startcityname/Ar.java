package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Pojo Structure of JSON.
 */
public class Ar {

  /**
   * text.
   */
  @Getter
  @SerializedName("text")
  @Expose
  private String text;
}
