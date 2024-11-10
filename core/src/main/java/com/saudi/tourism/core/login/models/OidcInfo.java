package com.saudi.tourism.core.login.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Oidc Info class.
 */
public class OidcInfo {

  /**
   * oidc issuer.
   */
  @Getter
  @Setter
  private String issuer;

  /**
   * Autherization endpoint.
   */
  @Getter
  @Setter
  @SerializedName("authorization_endpoint")
  private String authorizationEndpoint;

  /**
   * Token endpoint.
   */
  @Getter
  @Setter
  @SerializedName("token_endpoint")
  private String tokenEndpoint;

  /**
   * Userinfo endpoint.
   */
  @Getter
  @Setter
  @SerializedName("userinfo_endpoint")
  private String userinfoEndpoint;

  /**
   * End session endpoint.
   */
  @Getter
  @Setter
  @SerializedName("end_session_endpoint")
  private String endSessionEndpoint;

  /**
   * Jwks Uri.
   */
  @Getter
  @Setter
  @SerializedName("jwks_uri")
  private String jwksUri;

  /**
   * Logout url.
   */
  @Getter
  @Setter
  @SerializedName("log_out")
  private String logoutUrl;
}
