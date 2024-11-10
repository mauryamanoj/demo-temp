package com.saudi.tourism.core.login.servlets.v3;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.login.services.v2.SSIDService;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CookieHelper;
import com.saudi.tourism.core.utils.SameSite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.login.servlets.v3.CallbackServlet.SERVLET_PATH;
import static com.saudi.tourism.core.login.servlets.v3.CallbackServlet.DESCRIPTION;

/**
 * The Callback V3 servlet.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH })
@Slf4j
public class CallbackServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v3/user/callback";
  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "User Callback Servlet";


  /**
   * SSID service.
   */
  @Reference
  private transient SSIDService ssidService;

  /**
   * SSID config.
   */
  @Reference
  private transient SaudiSSIDConfig config;

  /**
   * Default cookie expiration time.
   */
  static final Integer DEFAULT_EXPIRATION_TIME = 604800;

  /**
   * Access token.
   */
  private static final String ACCESS_TOKEN = "access_token";

  /**
   * Id token.
   */
  private static final String ID_TOKEN = "id_token";

  @Override
  protected void doGet(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    final Cookie currentUrl = request.getCookie("current_url");
    final Cookie codeVerifier = request.getCookie("code_verifier");
    try {
      final RequestParameter authCode = request.getRequestParameter("code");
      final RequestParameter state = request.getRequestParameter("state");
      if (null !=  authCode && null !=  state && null != codeVerifier) {
        final JsonObject resp = ssidService.getAuthorizationToken(authCode.toString(), codeVerifier.getValue());
        String accessToken = "";
        String idToken = "";
        if (null != resp && null != resp.get(ACCESS_TOKEN) && null != resp.get(ID_TOKEN)) {
          accessToken = resp.get(ACCESS_TOKEN).getAsString();
          idToken = resp.get(ID_TOKEN).getAsString();
        } else {
          throw new IOException("Access Token not sent from SSID in Call back servlet");
        }
        addCookie(response, ACCESS_TOKEN, accessToken, resp, config);
        addCookie(response, ID_TOKEN, idToken, resp, config);
        if (null != currentUrl) {
          response.sendRedirect(currentUrl.getValue());
        } else {
          if (StringUtils.isNotBlank(config.getSSIDGetLogoutSiteUrl())) {
            response.sendRedirect(config.getSSIDGetLogoutSiteUrl());
          }
          LOGGER.info("Default login redirect URL is : " + config.getSSIDGetLogoutSiteUrl());
        }
      } else {
        throw new ServletException("Required parameters code or state missing from SSID");
      }
    } catch (Exception exception) {
      LOGGER.error("Exception is {}", exception);
      String maintainanceUrl = config.getSSIDMaintainancePageUrl();
      if (StringUtils.isNotBlank(maintainanceUrl)) {
        String locale = "en";
        if (null != currentUrl && StringUtils.isNotBlank(currentUrl.getValue())
            && currentUrl.getValue().split(".com/").length > 0) {
          locale = currentUrl.getValue().split(".com/")[1].split("/")[0];
        }
        String redirectErrPage = maintainanceUrl.replace("{locale}", locale);
        response.sendRedirect(redirectErrPage);
      }
    }
  }

  /**
   * Add cookie to the response.
   *
   * @param response
   * @param name
   * @param value
   * @param resp
   * @param config
   */
  private void addCookie(SlingHttpServletResponse response, String name, String value, JsonObject resp,
                          SaudiSSIDConfig config) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setMaxAge(getCookieExpiryTime(resp, config));
    response.addHeader("Set-Cookie", CookieHelper.createSetCookieHeader(cookie, SameSite.STRICT));
  }

  /**
   * Get cookie expiry time.
   *
   * @param resp
   * @param config
   * @return cookie expiry time
   */
  private int getCookieExpiryTime(JsonObject resp, SaudiSSIDConfig config) {
    if (StringUtils.isNotBlank(config.getSSIDTokenExpiryTime())) {
      return Integer.parseInt(config.getSSIDTokenExpiryTime());
    } else if (resp.has("expires_in")) {
      return resp.get("expires_in").getAsInt();
    } else {
      return DEFAULT_EXPIRATION_TIME;
    }
  }
}
