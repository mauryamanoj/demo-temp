package com.saudi.tourism.core.login.services;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentResponse;
import com.saudi.tourism.core.models.common.ResponseMessage;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This contains all methods to get related to SSID.
 */
public interface SSIDService {

  /**
   * @return SSID Login URL.
   * @throws ServletException ServletException.
   * @throws IOException IOException.
   */
  String getSSIDLoginUrl() throws ServletException, IOException;

  /**
   *
   * @param authCode Authorization code.
   * @return token response.
   * @throws ServletException Servlet exception.
   * @throws IOException IO Exception.
   */
  JsonObject getAuthorizationToken(String authCode) throws ServletException, IOException;

  /**
   *
   * @param accessToken accesstoken from SSID.
   * @return userinfo.
   * @throws ServletException Servlet exception.
   * @throws IOException IO Exception.
   */
  ResponseMessage getUserInfo(String accessToken) throws ServletException, IOException;

  /**
   * @param token accesstoken from SSID.
   * @return logout Url.
   *
   * @throws ServletException Servlet exception.
   */
  String getLogoutUrl(String token) throws ServletException;

  /**
   * Enroll user to loyalty program.
   *
   * @param userIDToken user id token
   * @return enrollment status.
   */
  LoyaltyEnrollmentResponse enrollToLoyaltyProgram(String userIDToken);

}
