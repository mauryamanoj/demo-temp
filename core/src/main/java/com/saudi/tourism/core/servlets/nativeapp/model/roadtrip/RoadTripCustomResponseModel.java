package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.Scenario;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.user.User;
import lombok.Getter;

import java.util.List;

/**
 * This Model is for Custom Response of the RoadTrip for native Apps .
 */
public class RoadTripCustomResponseModel {
  /**
   * Scenario .
   */
  @Getter
  @SerializedName("scenarios")
  @Expose
  private List<Scenario> scenarios;

  /**
   * users .
   */
  @Getter
  @SerializedName("users")
  @Expose
  private List<User> users;
  /**
   * total count.
   */
  @Getter
  @Expose
  @SerializedName("count")
  private Integer count;
}
