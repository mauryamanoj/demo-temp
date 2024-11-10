package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.localnames;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * LocalNames.
 */
public class LocalNames {

  /**
   * Ru.
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private String ru;

  /**
   * En.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private String en;

}
