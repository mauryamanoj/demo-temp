package com.saudi.tourism.core.login.services.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.saudi.tourism.core.login.PKCEUtil;
import com.saudi.tourism.core.login.models.OidcConfig;
import com.saudi.tourism.core.login.models.OidcInfo;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.SSIDRestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Oidc Helper class.
 */
@Slf4j
public final class OidcHelper {

  /**
   * OIDC config url.
   */
  private static String oidcConfigUrl = "/.well-known/openid-configuration";

  /**
   *
   * @param config oidc Config.
   * @param isProd is Prod run mode.
   * @return SSID authenticationUrl.
   * @throws IOException IOException.
   */
  public static String generateNewAuthenticationUrl(OidcConfig config,
                                                 boolean isProd) throws IOException, NoSuchAlgorithmException {
    if (verify(config, isProd)) {
      Map<String, Object> params = new HashMap<>();
      params.put("response_type", encode(config.getResponseType()));
      params.put("client_id", encode(config.getClientId()));
      params.put("redirect_uri", encode(config.getCallbackUrl()));
      params.put("scope", encode(config.getScope()));
      params.put("state", encode(config.getState()));
      params.put("code_challenge", PKCEUtil.generateCodeChallenge(config.getCodeVerify()));
      params.put("code_challenge_method", encode("S256"));
      final String urlParams  = params.entrySet().stream().map(Object::toString)
          .collect(Collectors.joining("&"));
      return config.getAuthorizationUrl().concat("?").concat(urlParams);
    }
    return null;
  }

  /**
   *
   * @param config oidc Config.
   * @param isProd is Prod run mode.
   * @return SSID authenticationUrl.
   * @throws IOException IOException.
   */
  public static String generateAuthenticationUrl(OidcConfig config,
                                                 boolean isProd) throws IOException {
    if (verify(config, isProd)) {
      Map<String, Object> params = new HashMap<>();
      params.put("response_type", encode(config.getResponseType()));
      params.put("client_id", encode(config.getClientId()));
      params.put("redirect_uri", encode(config.getCallbackUrl()));
      params.put("scope", encode(config.getScope()));
      params.put("state", encode(config.getState()));
      params.put("code_challenge", Base64.encodeUrlSafe(SecureUtil.sha256()
          .digest(config.getCodeVerify())));
      params.put("code_challenge_method", encode("S256"));
      final String urlParams  = params.entrySet().stream().map(Object::toString)
          .collect(Collectors.joining("&"));
      return config.getAuthorizationUrl().concat("?").concat(urlParams);
    }
    return null;
  }

  /**
   *
   * @param value value.
   * @return encoded value.
   * @throws UnsupportedEncodingException Encode Exception.
   */
  private static String encode(final String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
  }

  /**
   *
   * @param config oidc config.
   * @param isProd is Prod runmode.
   * @return verified true/false.
   * @throws IOException IOexception.
   */
  private static boolean verify(OidcConfig config, boolean isProd) throws IOException {
    final String oidPropsUrl = config.getIssuer() + oidcConfigUrl;
    ResponseMessage response = SSIDRestHelper.executeMethodGet(oidPropsUrl, StringUtils.EMPTY, false, isProd);
    if (null != response && StringUtils.isNotBlank(response.getMessage())) {
      Gson g = new Gson();
      OidcInfo oidcInfo = g.fromJson(response.getMessage(), OidcInfo.class);
      if (null != oidcInfo) {
        config.setAuthorizationUrl(oidcInfo.getAuthorizationEndpoint());
        config.setTokenUrl(oidcInfo.getTokenEndpoint());
        config.setUserinfoUrl(oidcInfo.getUserinfoEndpoint());
        config.setJwksUrl(oidcInfo.getJwksUri());
        config.setLogoutUrl(oidcInfo.getLogoutUrl());
        return true;
      }
    }
    LOGGER.error("OIDC config call response: " + response);
    return false;
  }

  /**
   * Prevents developer from initiating instance.
   */
  private OidcHelper() {
  }
}
