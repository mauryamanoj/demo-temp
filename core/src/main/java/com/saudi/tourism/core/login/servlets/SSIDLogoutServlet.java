package com.saudi.tourism.core.login.servlets;


import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

import static com.saudi.tourism.core.login.servlets.SSIDLogoutServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDLogoutServlet.SERVLET_PATH;

/**
 * The Get logout servlet for SSID.
 */
@Component(service = Servlet.class,
      property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
          ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
            + HttpConstants.METHOD_GET,
          ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + SERVLET_PATH})
@Slf4j
public class SSIDLogoutServlet extends SlingSafeMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v1/ssid/logout";

  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Get SSID Logout Servlet";

  /**
   * SSID login service.
   */
  @Reference
  private transient SSIDService ssidService;

  /**
   * SSID config.
   */
  @Reference
  private transient SaudiSSIDConfig config;

  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    final Cookie accessToken = request.getCookie("access_token");
    final Cookie currentUrl = request.getCookie("current_url");
    try {
      if (null != accessToken) {
        // if access token is present then redirect to ssid logout url.
        response.sendRedirect(ssidService.getLogoutUrl(
            accessToken.getValue()));
      } else {
        // if access token is expired then redirect to current page or default site url.
        if (null != currentUrl) {
          response.sendRedirect(currentUrl.getValue() + "?status=200");
        } else {
          response.sendRedirect(config.getSSIDGetLogoutSiteUrl() + "?status=200");
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Exception occurred could not logout :", ex);
      if (null != request.getCookie("current_url")) {
        String maintainanceUrl = config.getSSIDMaintainancePageUrl()
            .replace("{locale}", CommonUtils.getLanguageForPath(currentUrl.getValue()));
        response.sendRedirect(maintainanceUrl);
      }
      response.setStatus(StatusEnum.INTERNAL_SERVER_ERROR.getValue());
    }
  }
}
