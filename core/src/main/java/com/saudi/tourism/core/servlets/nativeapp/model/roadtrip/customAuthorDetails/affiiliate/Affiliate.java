package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.affiiliate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Affiliate POJO.
 */
public class Affiliate {

  /**
   * hot .
   */
  @SerializedName("hot")
  @Expose
  @Setter
  @Getter
  private Integer hot;
}
