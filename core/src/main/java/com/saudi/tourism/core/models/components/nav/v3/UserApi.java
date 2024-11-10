package com.saudi.tourism.core.models.components.nav.v3;

import com.google.gson.annotations.Expose;
import lombok.Value;

/** SSID User API config. */
@Value
class UserApi {

  /** User API root path. */
  @Expose
  private String path = "/bin/api/v2/user/";

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
  private String ssidLoginUrl = "/bin/api/v1/ssid/login-url";

  /**
   * SSID logout url API endpoint.
   */
  @Expose
  private String ssidLogoutUrl = "/bin/api/v1/ssid/logout";

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
