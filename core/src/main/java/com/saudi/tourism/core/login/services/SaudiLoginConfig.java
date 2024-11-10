package com.saudi.tourism.core.login.services;


/**
 * Interface for Saudi Login Configurations Service to expose only required methods.
 */
public interface SaudiLoginConfig {

  /**
   * Retrieve Auth0 ClientID.
   *
   * @return String ClientID.
   */
  String getClientId();

  /**
   * Retrieve the Auth0 ClientSecret.
   *
   * @return String ClientSecret
   */
  String getClientSecret();

  /**
   * Retrieve the Auth0 Audience.
   *
   * @return String audience
   */
  String getAudience();
  /**
   * Retrieve the Auth0 Domain.
   *
   * @return String auth0Domain
   */
  String getAuth0Domain();
  /**
  /**
   * Retrieve the Auth0 Domain.
   *
   * @return String auth0Domain
   */
  String getAuth0BaseUrl();
  /**
   * Retrieve the Auth0 OauthTokenEndpoint.
   *
   * @return String auth0OauthTokenEndpoint
   */
  String getAuth0OauthTokenEndpoint();
  /**
   * Retrieve the Auth0 OauthTokenEndpoint.
   *
   * @return String auth0OauthTokenEndpoint
   */
  String getAuth0GetUserEndpoint();


}
