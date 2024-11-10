package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ScenarioDetails POJO .
 */
public class ScenarioDetails {

  /**
   * List Object Of RRouteBodyouteBodyCustom .
   */
  @SerializedName("route_body")
  @Expose
  @Setter
  @Getter
  private List<RouteBody> routeBody;

}
