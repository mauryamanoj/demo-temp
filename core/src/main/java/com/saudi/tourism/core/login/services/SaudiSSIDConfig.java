package com.saudi.tourism.core.login.services;


/**
 * Interface for Saudi SSID Configurations Service to expose only required methods.
 */
public interface SaudiSSIDConfig {

  /**
   * Retrieve SSID ClientID.
   *
   * @return String clientID.
   */
  String getClientId();

  /**
   * Retrieve the SSID ClientSecret.
   *
   * @return String clientSecret
   */
  String getClientSecret();

  /**
   * Retrieve the SSID Domain.
   *
   * @return String sSIDDomain
   */
  String getSSIDDomain();
  /**
   /**
   * Retrieve the SSID base URL.
   *
   * @return String ssid base url.
   */
  String getSSIDBaseUrl();

  /**
   /**
   * Retrieve the SSID issuer.
   *
   * @return String SSID issuer.
   */
  String getSSIDIssuer();

  /**
   * Retrieve the SSID CallBack URL.
   *
   * @return String sSIDGetCallBackUrl.
   */
  String getSSIDGetCallBackUrl();

  /**
   * Retrieve the SSID Scope.
   *
   * @return String sSIDScope.
   */
  String getSSIDScope();


  /**
   * Retrieve the SSID response type.
   *
   * @return String sSIDResponseType.
   */
  String getSSIDResponseType();

  /**
   * Retrieve the SSID authTokenEndpoint.
   *
   * @return String sSIDAuthTokenEndpoint
   */
  String getSSIDOidcTokenEndpoint();

  /**
   * Retrieve the SSID userProfileEndpoint.
   *
   * @return String sSIDUserProfileEndpoint
   */
  String getSSIDGetUserProfileEndpoint();

  /**
   * Retrieve the SSID logoutEndpoint.
   *
   * @return String sSIDLogoutEndpoint
   */
  String getSSIDGetLogoutEndpoint();

  /**
   * Retrieve the SSID logoutSiteUrl.
   *
   * @return String sSIDLogoutSiteUrl
   */
  String getSSIDGetLogoutSiteUrl();

  /**
   * Retrieve the SSID updateUserProfileEndpoint.
   *
   * @return String sSIDUpdateUserProfileEndpoint
   */
  String getSSIDUpdateUserProfileEndpoint();


  /**
   * Retrieve the SSID logoutRedirectUrl.
   *
   * @return String sSIDLogoutRedirectUrl
   */
  String getSSIDLogoutRedirectUrl();

  /**
   * Retrieve the SSID Token Expiry.
   *
   * @return String sSIDTokenExpiryTime
   */
  String getSSIDTokenExpiryTime();

  /**
   * Retrieve the SSID Update profile version.
   *
   * @return String sSIDUpdateUserProfileVersion
   */
  String getSSIDUpdateUserProfileVersion();

  /**
   * Retrieve the SSID Maintainance Page Url.
   *
   * @return String sSIDMaintainancePageUrl
   */
  String getSSIDMaintainancePageUrl();


  /**
   * Retrieve Login Redirect Endpoint.
   *
   * @return String loginRedirectEndpoint
   */
  String getLoginRedirectEndpoint();

  /**
   * Retrieve Logout Redirect Endpoint.
   *
   * @return String logoutRedirectEndpoint
   */
  String getLogoutRedirectEndpoint();

  /**
   * Get Profile Endpoint.
   *
   * @return String GetProfileEndpoint
   */
  String getGetProfileEndpoint();

  /**
   * Get Favorites Endpoint.
   *
   * @return String GetFavoritesEndpoint
   */
  String getGetFavoritesEndpoint();

  /**
   * Get Trips Endpoint.
   *
   * @return String GetTripsEndpoint
   */
  String getGetTripsEndpoint();

  /**
   * Update Profile Endpoint.
   *
   * @return String UpdateProfileEndpoint
   */
  String getUpdateProfileEndpoint();


  /**
   * User Info Endpoint.
   *
   * @return String userInfoEndpoint
   */
  String getUserInfoEndpoint();

  /**
   * Update profile with loyalty endpoint.
   *
   * @return String sSIDUpdateProfileWithLoyaltyEndpoint
   */
  String getSSIDUpdateProfileWithLoyaltyEndpoint();

}
