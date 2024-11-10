package com.saudi.tourism.core.models.components.login;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;

/** Login Model. */
@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApiModel {
  /** Sling settings service. */
  @OSGiService
  private SaudiSSIDConfig saudiSSIDConfig;

  /** Login link. */
  @Expose
  private String ssidLoginUrl;

  /** domain. */
  @Expose
  private String domain;

  /** Client ID. */
  @Expose
  private String clientId;

  /** SSID User Info API path. */
  @Expose
  private String ssidUserInfo;

  /** SSID Logout Info API path. */
  @Expose
  private String ssidLogoutUrl;

  /** SSID Get Profile Endpoint. */
  @Expose
  private String getProfile;

  /** SSID Get Trips Endpoint. */
  @Expose
  private String getTripPlansEndpoint;

  /** SSID Update Profile Endpoint. */
  @Expose
  private String updateProfile;


  /** SSID Get Favorites Endpoint. */
  @Expose
  private String getFavorites;

  @PostConstruct
  public void init() {
    ssidLoginUrl = saudiSSIDConfig.getLoginRedirectEndpoint();
    domain = saudiSSIDConfig.getSSIDDomain();
    clientId = saudiSSIDConfig.getClientId();
    ssidUserInfo = saudiSSIDConfig.getUserInfoEndpoint();
    ssidLogoutUrl = saudiSSIDConfig.getLogoutRedirectEndpoint();
    getProfile = saudiSSIDConfig.getGetProfileEndpoint();
    getTripPlansEndpoint = saudiSSIDConfig.getGetTripsEndpoint();
    updateProfile = saudiSSIDConfig.getUpdateProfileEndpoint();
    getFavorites = saudiSSIDConfig.getGetFavoritesEndpoint();
  }
}
