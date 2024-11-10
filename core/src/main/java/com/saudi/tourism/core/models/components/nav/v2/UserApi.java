package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import com.saudi.tourism.core.login.servlets.SSIDGetUserInfoServlet;
import com.saudi.tourism.core.login.servlets.SSIDLoginUrlServlet;
import com.saudi.tourism.core.login.servlets.SSIDLogoutServlet;
import lombok.Value;

/** SSID User API config. */
@Value
class UserApi {

  /** User API root path. */
  @Expose
  private String path = SSIDGetUserDetailsServlet.SERVLET_PATH.split("get")[0];

  /** Get user's profile API endpoint. */
  @Expose
  private String getProfile = "get.profile";

  /** Get user's trips API endpoint. */
  @Expose
  private String getTripPlansEndpoint = "get.trips";

  /** Update user's profile API endpoint. */
  @Expose
  private String updateProfile = "update.profile";

  /**
   * SSID login url API endpoint.
   */
  @Expose
  private String ssidLoginUrl = SSIDLoginUrlServlet.SERVLET_PATH;

  /**
   * SSID logout url API endpoint.
   */
  @Expose
  private String ssidLogoutUrl = SSIDLogoutServlet.SERVLET_PATH;

  /**
   * SSID user info  API endpoint.
   */
  @Expose
  private String ssidUserInfo = SSIDGetUserInfoServlet.SERVLET_PATH;

  /** SSID domain. */
  @Expose
  private String domain;

  /** SSID clientId. */
  @Expose
  private String clientId;

  UserApi(String domain, String clientId) {
    this.domain = domain;
    this.clientId = clientId;
  }
}
