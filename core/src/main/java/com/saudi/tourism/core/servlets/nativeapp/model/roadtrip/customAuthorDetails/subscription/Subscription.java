package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.subscription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

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
  @Setter
  private String status;
}

