package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.Scenario;
import lombok.Getter;

import java.util.List;

/**
 * AuthorDetails POJO.
 */
public class AuthorDetails {

  /**
   * User.
   */
  @SerializedName("users")
  @Expose
  @Getter
  private List<User> users = null;

  /**
   * ScenarioUsers.
   */
  @Getter
  @Expose
  @SerializedName("scenarios_user")
  private List<Scenario> scenarios;

}
