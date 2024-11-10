package com.saudi.tourism.core.login.services.v2;

import java.io.IOException;
import javax.servlet.ServletException;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.login.models.LoginData;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentResponse;
import com.saudi.tourism.core.models.common.ResponseMessage;

/**
 * This contains all methods to get related to SSID.
 */
public interface SSIDService {

  /**
   * @return SSID Login Data.
   * @throws ServletException ServletException.
   * @throws IOException IOException.
   */
  LoginData generateLoginData() throws ServletException, IOException;

  /**
   * @param authCode Authorization code.
   * @param codeVerifier
   *
   * @return token response.
   *
   * @throws ServletException Servlet exception.
   * @throws IOException      IO Exception.
   */
  JsonObject getAuthorizationToken(String authCode, String codeVerifier) throws ServletException, IOException;

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
