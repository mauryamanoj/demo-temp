package com.saudi.tourism.core.login.models;

import lombok.Getter;
import lombok.Setter;

/**
 * OIDc Configurations.
 */
public class OidcConfig {

  /**
   * issuer.
   */
  @Getter
  @Setter
  private String issuer;

  /**
   * platform.
   */
  @Getter
  private String platform = "SSID";

  /**
   * client id.
   */
  @Getter
  @Setter
  private String clientId;

  /**
   * client secret code.
   */
  @Getter
  @Setter
  private String clientSecret;

  /**
   * call back Url.
   */
  @Getter
  @Setter
  private String callbackUrl;

  /**
   * response Type.
   */
  @Getter
  @Setter
  private String responseType;

  /**
   * logout url.
   */
  @Getter
  @Setter
  private String state;

  /**
   * code Verify.
   */
  @Getter
  @Setter
  private String codeVerify;

  /**
   * logout url.
   */
  @Getter
  @Setter
  private String scope;

  /**
   * authorization Url.
   */
  @Getter
  @Setter
  private String authorizationUrl;

  /**
   * token Url.
   */
  @Getter
  @Setter
  private String tokenUrl;

  /**
   * user info Url.
   */
  @Getter
  @Setter
  private String userinfoUrl;

  /**
   * jwks url.
   */
  @Getter
  @Setter
  private String jwksUrl;

  /**
   * logout url.
   */
  @Getter
  @Setter
  private String logoutUrl;
}
