package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.subscription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Subscription POJO.
 */
public class Subscription {
  /**
   * Status.
   */
  @SerializedName("status")
  @Expose
  @Getter
  private String status;
}

