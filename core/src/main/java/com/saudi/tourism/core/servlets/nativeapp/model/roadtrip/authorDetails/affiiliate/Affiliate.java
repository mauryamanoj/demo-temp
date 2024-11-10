package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.affiiliate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Affiliate POJO.
 */
public class Affiliate {

  /**
   * hot .
   */
  @SerializedName("hot")
  @Expose
  @Getter
  private Integer hot;
}
