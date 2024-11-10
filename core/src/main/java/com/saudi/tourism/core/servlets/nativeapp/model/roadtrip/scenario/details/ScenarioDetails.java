package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.RouteBodyCustom;
import lombok.Getter;

import java.util.List;

/**
 * Scenario Details.
 */
public class ScenarioDetails {

  /**
   * List Object Of RouteBodyCustom .
   */
  @SerializedName("route_body")
  @Expose
  @Getter
  private List<RouteBodyCustom> routeBody;
}
