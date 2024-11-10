package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response.NativeAppCustomCardResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * CustomAuthorDetails POJO.
 */
public class CustomAuthorDetails {

  /**
   * Users ArrayList .
   */
  @SerializedName("users")
  @Expose
  @Getter
  @Setter
  private List<CustomUserDetails> users = null;

  /**
   * Scenario_User .
   */
  @Getter
  @Expose
  @SerializedName("scenarios_user")
  @Setter
  private List<NativeAppCustomCardResponse> scenarios;
}
