package com.saudi.tourism.core.login.servlets;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.login.services.SSIDService;
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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.saudi.tourism.core.login.servlets.CallbackServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.CallbackServlet.SERVLET_PATH;

/**
 * The Callback servlet.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH })
@Slf4j
public class CallbackServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v2/user/callback";
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

  @Override
  protected void doGet(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    final Cookie currentUrl = request.getCookie("current_url");
    try {
      final RequestParameter authCode = request.getRequestParameter("code");
      final RequestParameter state = request.getRequestParameter("state");
      if (null !=  authCode && null !=  state) {
        final JsonObject resp = ssidService.getAuthorizationToken(authCode.toString());
        String accessToken = "";
        if (null != resp && null != resp.get("access_token")) {
          accessToken = resp.get("access_token").getAsString();
        } else {
          throw new IOException("Access Token not sent from SSID in Call back servlet");
        }
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setPath("/");
        cookie.setSecure(true);
        if (StringUtils.isNotBlank(config.getSSIDTokenExpiryTime())) {
          cookie.setMaxAge(Integer.parseInt(config.getSSIDTokenExpiryTime()));
        } else {
          if (null != resp.get("expires_in")) {
            cookie.setMaxAge(resp.get("expires_in").getAsInt());
          } else {
            cookie.setMaxAge(DEFAULT_EXPIRATION_TIME);
          }
        }
        response.addHeader("Set-Cookie", CookieHelper.createSetCookieHeader(cookie, SameSite.STRICT));
        HttpSession session = request.getSession();
        session.setAttribute("access_token", accessToken);
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
}
