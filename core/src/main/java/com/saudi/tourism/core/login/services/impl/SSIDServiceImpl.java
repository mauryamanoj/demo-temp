package com.saudi.tourism.core.login.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentResponse;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentStatus;
import com.saudi.tourism.core.login.models.OidcConfig;
import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.SSIDRestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * SSID Login Service.
 */
@Slf4j
@Component(service = SSIDService.class,
    immediate = true)
public class SSIDServiceImpl implements SSIDService {

  /**
   * Saudi SSID Login Configurations.
   */
  @Reference
  private SaudiSSIDConfig saudiSSIDConfig;

  /**
   * InMemory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * Settings service.
   */
  @Reference
  private SlingSettingsService settingsService;

  /**
   * Cache state in milli secs.
   */
  static final long CACHE_MILLI_SECONDS = 3600000;

  @Override
  public String getSSIDLoginUrl() throws ServletException, IOException {
    if (null != saudiSSIDConfig) {
      String issuer = saudiSSIDConfig.getSSIDIssuer();
      String clientId = saudiSSIDConfig.getClientId();
      String clientSecret = saudiSSIDConfig.getClientSecret();
      String callbackUrl = saudiSSIDConfig.getSSIDGetCallBackUrl();
      String responseType = saudiSSIDConfig.getSSIDResponseType();
      String state = UUID.randomUUID().toString();
      String codeVerify = UUID.randomUUID().toString();
      String scope = saudiSSIDConfig.getSSIDScope();
      if (StringUtils.isNotBlank(issuer) && StringUtils.isNotBlank(clientId)
          && StringUtils.isNotBlank(clientSecret) && StringUtils.isNotBlank(callbackUrl)
          && StringUtils.isNotBlank(responseType) && StringUtils.isNotBlank(scope)) {
        OidcConfig config = new OidcConfig();
        config.setIssuer(issuer);
        config.setClientId(clientId);
        config.setClientSecret(clientSecret);
        config.setCallbackUrl(callbackUrl);
        config.setResponseType(responseType);
        config.setState(state);
        config.setCodeVerify(codeVerify);
        config.setScope(scope);
        String authUrl = OidcHelper.generateAuthenticationUrl(config, isProdRunMode());
        if (StringUtils.isNotBlank(authUrl) && authUrl.contains("state")) {
          memCache.add(state, codeVerify, CACHE_MILLI_SECONDS);
        }
        return authUrl;
      } else {
        throw new ServletException("One or more configurations required for saudiSSIDConfig are not configured");
      }
    } else {
      throw new ServletException("saudiSSIDConfig object is null");
    }
  }

  @Override
  public JsonObject getAuthorizationToken(final String authCode) throws ServletException, IOException {
    String clientId = saudiSSIDConfig.getClientId();
    String clientSecret = saudiSSIDConfig.getClientSecret();
    if (StringUtils.isBlank(clientId) || StringUtils.isBlank(clientSecret)) {
      throw new ServletException("Required parameters clientId or clientSecret is not configured");
    }
    StringBuffer sb = new StringBuffer();
    sb.append("grant_type=authorization_code");
    sb.append("&client_id=").append(clientId);
    sb.append("&client_secret=").append(clientSecret);
    sb.append("&code=").append(authCode);
    HttpEntity entity = new StringEntity(sb.toString(), ContentType.APPLICATION_FORM_URLENCODED);
    ResponseMessage res = SSIDRestHelper.executeMethodPost(saudiSSIDConfig.getSSIDBaseUrl()
        + saudiSSIDConfig.getSSIDOidcTokenEndpoint(), entity, null, false, isProdRunMode());
    if (null != res && null != res.getMessage() && res.getStatusCode() == SC_OK) {
      JsonObject resp = new JsonParser().parse(res.getMessage()).getAsJsonObject();
      LOGGER.info("SSID callback response: " + resp.toString());
      return resp;
    } else {
      throw new IOException("Response from SSID: " + res);
    }
  }

  @Override
  public ResponseMessage getUserInfo(final String accessToken) throws ServletException, IOException {
    final String ssidIssuer = saudiSSIDConfig.getSSIDBaseUrl();
    final String userInfoEndpointUrl = saudiSSIDConfig.getSSIDGetUserProfileEndpoint();
    if (StringUtils.isBlank(ssidIssuer) || StringUtils.isBlank(userInfoEndpointUrl)) {
      throw new ServletException("Required parameters ssidIssuer or userInfoEndpointUrl is not configured");
    }
    ResponseMessage resMsg = SSIDRestHelper
        .executeMethodGet(ssidIssuer + userInfoEndpointUrl, accessToken, true, isProdRunMode());
    return resMsg;
  }

  @Override
  public String getLogoutUrl(String token) throws ServletException {
    final String ssidBaseUrl = saudiSSIDConfig.getSSIDBaseUrl();
    final String clientId = saudiSSIDConfig.getClientId();
    final String logoutEndpointUrl = saudiSSIDConfig.getSSIDGetLogoutEndpoint();
    final String logoutSiteUrl = saudiSSIDConfig.getSSIDLogoutRedirectUrl();
    if (StringUtils.isBlank(ssidBaseUrl) || StringUtils.isBlank(clientId)
        || StringUtils.isBlank(logoutEndpointUrl) || StringUtils.isBlank(logoutSiteUrl)) {
      throw new ServletException("Required parameters ssidIssuer or clientId or logoutEndpointUrl "
          + "or logoutSiteUrl not configured");
    }
    return ssidBaseUrl + logoutEndpointUrl + "?client_id=" + clientId + "&logout_url=" + logoutSiteUrl
          + "&access_token=" + token;
  }

  @Override
  public LoyaltyEnrollmentResponse enrollToLoyaltyProgram(final String token) {
    try {
      final var userInfo = getUserInfo(token);
      if (Objects.isNull(userInfo) || StringUtils.isEmpty(userInfo.getMessage())) {
        LOGGER.error("No UserInfo returned from SSID while enabling Loyalty Program");
        return LoyaltyEnrollmentResponse.builder()
          .status(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED)
          .message("No UserInfo returned from SSID while enabling Loyalty Program")
          .build();
      }

      final var jsonObject = JsonParser.parseString(userInfo.getMessage()).getAsJsonObject();

      String loyaltyId = null;
      String ssid = null;
      if (jsonObject.has("loyaltyId")) {
        loyaltyId = jsonObject.get("loyaltyId").getAsString();
      }

      if (jsonObject.has("ssid")) {
        ssid = jsonObject.get("ssid").getAsString();
      }

      if (StringUtils.isEmpty(ssid)) {
        return LoyaltyEnrollmentResponse.builder()
          .status(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED)
          .message("SID not returned from SSID")
          .build();
      }

      if (StringUtils.isNotEmpty(loyaltyId)) {
        return LoyaltyEnrollmentResponse.builder()
          .status(LoyaltyEnrollmentStatus.ALREADY_ENROLLED)
          .build();
      }

      final String ssidIssuer = saudiSSIDConfig.getSSIDBaseUrl();
      final String updateProfileWithLoyaltyEndpointUrl = saudiSSIDConfig.getSSIDUpdateProfileWithLoyaltyEndpoint();

      final HttpEntity entity = MultipartEntityBuilder
          .create()
          .addTextBody("ssid", ssid)
          .build();

      final var response = SSIDRestHelper
          .executeMethodPatch(ssidIssuer + updateProfileWithLoyaltyEndpointUrl, entity, token, true);

      if (ObjectUtils.notEqual(response.getStatusCode(), HttpStatus.SC_OK)) {
        LOGGER.error("Technical error while enabling Loyalty Program");
        return LoyaltyEnrollmentResponse.builder()
          .status(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED)
          .build();
      }

      return LoyaltyEnrollmentResponse.builder()
        .status(LoyaltyEnrollmentStatus.SUCCESSFULLY_ENROLLED)
        .build();

    } catch (Exception e) {
      LOGGER.error("Technical error while enabling Loyalty Program", e);
    }
    return LoyaltyEnrollmentResponse.builder()
      .status(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED)
      .message("Technical error while enabling Loyalty Program")
      .build();
  }

  /**
   * check if prod run mode.
   * @return true/false
   */
  private boolean isProdRunMode() {
    if (null != settingsService && null != settingsService.getRunModes()
        && settingsService.getRunModes().contains("prod")) {
      return true;
    }
    return false;
  }
}
