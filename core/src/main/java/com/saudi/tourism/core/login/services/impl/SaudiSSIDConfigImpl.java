package com.saudi.tourism.core.login.services.impl;

import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This class contains Saudi Tourism SSID Login configurations.
 */
@Slf4j
@Component(immediate = true,
    service = SaudiSSIDConfig.class)
@Designate(ocd = SaudiSSIDConfigImpl.Configuration.class)
public class SaudiSSIDConfigImpl implements  SaudiSSIDConfig {

  /**
   * Client Id.
   */
  @Getter
  private String clientId;

  /**
   * Client secret.
   */
  @Getter
  private String clientSecret;

  /**
   * SSID Domain.
   */
  @Getter
  private String sSIDDomain;
  /**
   * SSID Base URL.
   */
  @Getter
  private String sSIDBaseUrl;

  /**
   * SSID Base URL.
   */
  @Getter
  private String sSIDIssuer;

  /**
   * SSID Base URL.
   */
  @Getter
  private String sSIDGetCallBackUrl;

  /**
   * SSID Base URL.
   */
  @Getter
  private String sSIDScope;

  /**
   * SSID response type.
   */
  @Getter
  private String sSIDResponseType;

  /**
   * sSIDOidcTokenEndpoint.
   */
  @Getter
  private String sSIDOidcTokenEndpoint;

  /**
   * sSIDGetUserProfileEndpoint.
   */
  @Getter
  private String sSIDGetUserProfileEndpoint;

  /**
   * sSIDGetLogoutEndpoint.
   */
  @Getter
  private String sSIDGetLogoutEndpoint;

  /**
   * sSIDGetLogoutSiteUrl.
   */
  @Getter
  private String sSIDGetLogoutSiteUrl;

  /**
   * sSIDUpdateUserProfileEndpoint.
   */
  @Getter
  private String sSIDUpdateUserProfileEndpoint;


  /**
   * sSIDLogoutRedirectUrl.
   */
  @Getter
  private String sSIDLogoutRedirectUrl;

  /**
   * sSIDTokenExpiryTime.
   */
  @Getter
  private String sSIDTokenExpiryTime;

  /**
   * sSIDUpdateUserProfileVersion.
   */
  @Getter
  private String sSIDUpdateUserProfileVersion;

  /**
   * sSIDMaintainancePageUrl.
   */
  @Getter
  private String sSIDMaintainancePageUrl;

  /**
   * loginRedirectEndpoint.
   */
  @Getter
  private String loginRedirectEndpoint;

  /**
   * logoutRedirectEndpoint.
   */
  @Getter
  private String logoutRedirectEndpoint;


  /**
   * GetProfileEndpoint.
   */
  @Getter
  private String getProfileEndpoint;

  /**
   * getFavoritesEndpoint.
   */
  @Getter
  private String getFavoritesEndpoint;

  /**
   * getTripsEndpoint.
   */
  @Getter
  private String getTripsEndpoint;

  /**
   * updateProfileEndpoint.
   */
  @Getter
  private String updateProfileEndpoint;

  /**
   * userInfoEndpoint.
   */
  @Getter
  private String userInfoEndpoint;

  /**
   * sSIDUpdateProfileWithLoyaltyEndpoint.
   */
  @Getter
  private String sSIDUpdateProfileWithLoyaltyEndpoint;

  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param saudiSsidConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(SaudiSSIDConfigImpl.Configuration saudiSsidConfig) {
    LOGGER.debug("Saudi SSID Login Configurations Activate/Modified");

    this.clientId = saudiSsidConfig.clientId();
    this.clientSecret = saudiSsidConfig.clientSecret();
    this.sSIDDomain = saudiSsidConfig.sSIDDomain();
    this.sSIDBaseUrl = saudiSsidConfig.sSIDBaseUrl();
    this.sSIDIssuer = saudiSsidConfig.sSIDIssuer();
    this.sSIDGetCallBackUrl = saudiSsidConfig.sSIDGetCallBackUrl();
    this.sSIDResponseType = saudiSsidConfig.sSIDResponseType();
    this.sSIDScope = saudiSsidConfig.sSIDScope();
    this.sSIDOidcTokenEndpoint = saudiSsidConfig.sSIDOidcTokenEndpoint();
    this.sSIDGetUserProfileEndpoint = saudiSsidConfig.sSIDGetUserProfileEndpoint();
    this.sSIDGetLogoutEndpoint = saudiSsidConfig.sSIDGetLogoutEndpoint();
    this.sSIDGetLogoutSiteUrl = saudiSsidConfig.sSIDGetLogoutSiteUrl();
    this.sSIDUpdateUserProfileEndpoint = saudiSsidConfig.sSIDUpdateUserProfileEndpoint();
    this.sSIDLogoutRedirectUrl = saudiSsidConfig.sSIDLogoutRedirectUrl();
    this.sSIDTokenExpiryTime = saudiSsidConfig.sSIDTokenExpiryTime();
    this.sSIDUpdateUserProfileVersion = saudiSsidConfig.sSIDUpdateUserProfileVersion();
    this.sSIDMaintainancePageUrl = saudiSsidConfig.sSIDMaintainancePageUrl();
    this.loginRedirectEndpoint = saudiSsidConfig.loginRedirectEndpoint();
    this.logoutRedirectEndpoint = saudiSsidConfig.logoutRedirectEndpoint();
    this.getProfileEndpoint = saudiSsidConfig.getProfileEndpoint();
    this.getFavoritesEndpoint = saudiSsidConfig.getFavoritesEndpoint();
    this.getTripsEndpoint = saudiSsidConfig.getTripsEndpoint();
    this.updateProfileEndpoint =  saudiSsidConfig.updateProfileEndpoint();
    this.userInfoEndpoint = saudiSsidConfig.userInfoEndpoint();
    this.sSIDUpdateProfileWithLoyaltyEndpoint = saudiSsidConfig.sSIDUpdateProfileWithLoyaltyEndpoint();
  }

  /**
    * The interface Configuration.
    */
  @ObjectClassDefinition(name = "Saudi SSID Configuration") @interface Configuration {

    /**
     * Retrieve the MapBox API key.
     *
     * @return String MapBox API key
     */
    @AttributeDefinition(name = "SSID clientId", type = AttributeType.PASSWORD)
    String clientId() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID clientSecret.
     *
     * @return String clientSecret
     */
    @AttributeDefinition(name = "SSID clientSecret", type = AttributeType.PASSWORD)
    String clientSecret() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID Domain.
     *
     * @return String ssidDomain Field
     */
    @AttributeDefinition(name = "SSID API Domain", type = AttributeType.STRING)
    String sSIDDomain() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID Base URL.
     *
     * @return String sSIDDomain Field
     */
    @AttributeDefinition(name = "SSID API Base URL", type = AttributeType.STRING)
    String sSIDBaseUrl() default StringUtils.EMPTY;


    /**
     * Retrieve the SSID Base URL.
     *
     * @return String sSIDDomain Field
     */
    @AttributeDefinition(name = "SSID Issuer", type = AttributeType.STRING)
    String sSIDIssuer() default StringUtils.EMPTY;

   /**
   * Retrieve the SSID CallBack URL.
   *
   * @return String sSIDGetCallBackURL.
   */
    @AttributeDefinition(name = "SSID Callback URL", type = AttributeType.STRING)
    String sSIDGetCallBackUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID Scope.
     *
     * @return String sSIDScope.
     */
    @AttributeDefinition(name = "SSID Scope", type = AttributeType.STRING)
    String sSIDScope() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID Response type.
     *
     * @return String sSIDResponseType.
     */
    @AttributeDefinition(name = "SSID Response Type", type = AttributeType.STRING)
    String sSIDResponseType() default StringUtils.EMPTY;

    /**
     * Retrieve the sSIDOidcTokenEndpoint.
     *
     * @return String sSIDOidcTokenEndpoint.
     */
    @AttributeDefinition(name = "SSID OIDC Token Endpoint", type = AttributeType.STRING)
    String sSIDOidcTokenEndpoint() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID user profile endpoint.
     *
     * @return String sSIDGetUserProfileEndpoint.
     */
    @AttributeDefinition(name = "SSID GetUser Profile Endpoint", type = AttributeType.STRING)
    String sSIDGetUserProfileEndpoint() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID logout endpoint.
     *
     * @return String sSIDGetLogoutEndpoint.
     */
    @AttributeDefinition(name = "SSID Get Logout Endpoint", type = AttributeType.STRING)
    String sSIDGetLogoutEndpoint() default StringUtils.EMPTY;


    /**
     * Retrieve the SSID logout site url.
     *
     * @return String sSIDGetLogoutSiteUrl.
     */
    @AttributeDefinition(name = "SSID Get Logout Site Url", type = AttributeType.STRING)
    String sSIDGetLogoutSiteUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID updateUserProfileEndpoint.
     *
     * @return String sSIDUpdateUserProfileEndpoint
     */
    @AttributeDefinition(name = "SSID Update User Profile Endpoint", type = AttributeType.STRING)
    String sSIDUpdateUserProfileEndpoint() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID logoutRedirectUrl.
     *
     * @return String sSIDLogoutRedirectUrl
     */
    @AttributeDefinition(name = "SSID Logout Redirect Url", type = AttributeType.STRING)
    String sSIDLogoutRedirectUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID sSIDTokenExpiryTime.
     *
     * @return String sSIDTokenExpiryTime
     */
    @AttributeDefinition(name = "SSID Token Expiry Time in Secs", type = AttributeType.STRING)
    String sSIDTokenExpiryTime() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID sSIDUpdateUserProfileVersion.
     *
     * @return String sSIDUpdateUserProfileVersion
     */
    @AttributeDefinition(name = "SSID Update Profile version", type = AttributeType.STRING)
    String sSIDUpdateUserProfileVersion() default StringUtils.EMPTY;

    /**
     * Retrieve the SSID sSIDMaintainancePageUrl.
     *
     * @return String sSIDMaintainancePageUrl
     */
    @AttributeDefinition(name = "SSID Maintainance Page Url", type = AttributeType.STRING)
    String sSIDMaintainancePageUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the loginRedirectEndpoint.
     *
     * @return String loginRedirectEndpoint
     */
    @AttributeDefinition(name = "Login Redirect Endpoint", type = AttributeType.STRING)
    String loginRedirectEndpoint() default "/bin/api/v1/ssid/login-url";

    /**
     * Retrieve the logoutRedirectEndpoint.
     *
     * @return String logoutRedirectEndpoint
     */
    @AttributeDefinition(name = "Logout Redirect Endpoint", type = AttributeType.STRING)
    String logoutRedirectEndpoint() default "/bin/api/v1/ssid/logout";

    /**
     * Get Profile Endpoint.
     *
     * @return String getProfileEndpoint
     */
    @AttributeDefinition(name = "Get Profile Endpoint", type = AttributeType.STRING)
    String getProfileEndpoint() default "/bin/api/v2/user/get.profile";

    /**
     * Get Favorites Endpoint.
     *
     * @return String getFavoritesEndpoint
     */
    @AttributeDefinition(name = "Get Favorites Endpoint", type = AttributeType.STRING)
    String getFavoritesEndpoint() default "/bin/api/v2/user/get.favorites";

    /**
     * Get Trips Endpoint.
     *
     * @return String getTripsEndpoint
     */
    @AttributeDefinition(name = "Get Trips Endpoint", type = AttributeType.STRING)
    String getTripsEndpoint() default "/bin/api/v2/user/get.trips";

    /**
     * Update Profile Endpoint.
     *
     * @return String updateProfileEndpoint
     */
    @AttributeDefinition(name = "Update Profile Endpoint", type = AttributeType.STRING)
    String updateProfileEndpoint() default "/bin/api/v2/user/update.profile";


    /**
     * User Info Endpoint.
     *
     * @return String userInfoEndpoint
     */
    @AttributeDefinition(name = "User Info Endpoint", type = AttributeType.STRING)
    String userInfoEndpoint() default "/bin/api/v1/ssid/userInfo";

    /**
     * Update profile with loyalty endpoint.
     *
     * @return String sSIDUpdateProfileWithLoyaltyEndpoint.
     */
    @AttributeDefinition(name = "SSID Update profile with loyalty endpoint", type = AttributeType.STRING)
    String sSIDUpdateProfileWithLoyaltyEndpoint() default StringUtils.EMPTY;
  }
}
