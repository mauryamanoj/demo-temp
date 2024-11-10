package com.saudi.tourism.core.login.servlets;

import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
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

import static com.saudi.tourism.core.login.servlets.SSIDLogoutRedirectServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDLogoutRedirectServlet.SERVLET_PATH;

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
public class SSIDLogoutRedirectServlet extends SlingSafeMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v1/ssid/redirectLogout";

  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Get SSID Logout Redirect Servlet";

  /**
   * SSID config.
   */
  @Reference
  private transient SaudiSSIDConfig config;

  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {
      final Cookie currentUrl = request.getCookie("current_url");
      final int status = response.getStatus();
      if (status == HttpStatus.SC_OK) {
        if (null != currentUrl) {
          String currValue = currentUrl.getValue();
          String locale = "";
          if (currValue.split(".com/").length > 1) {
            locale = currValue.split(".com/")[1].split("/")[0];
          }
          String redirectUrl = "";
          String protocol = "";
          String domain = "";
          if (currValue.split("//").length > 1) {
            protocol = currValue.split("//")[0];
            domain = currValue.split("//")[1].split("/")[0];
            redirectUrl = protocol + "//" + domain + "/" + locale;
          } else {
            if (StringUtils.isNotBlank(config.getSSIDGetLogoutSiteUrl())) {
              redirectUrl = config.getSSIDGetLogoutSiteUrl();
            } else {
              throw new IOException("SSID Logout Url config is empty");
            }
          }
          response.sendRedirect(redirectUrl + "?status="
              + HttpStatus.SC_OK);
        } else {
          if (StringUtils.isNotBlank(config.getSSIDGetLogoutSiteUrl())) {
            response.sendRedirect(config.getSSIDGetLogoutSiteUrl() + "?status="
                + HttpStatus.SC_OK);
          } else {
            throw new IOException("SSID Logout Url config is empty");
          }
        }
      } else {
        if (null != currentUrl) {
          String maintainanceUrl = config.getSSIDMaintainancePageUrl()
              .replace("{locale}", CommonUtils.getLanguageForPath(currentUrl.getValue()));
          LOGGER.error("Status parameter from SSID after logout is: " + status);
          response.sendRedirect(maintainanceUrl
              + "?status=" + status);
        } else {
          if (StringUtils.isNotBlank(config.getSSIDGetLogoutSiteUrl())) {
            response.sendRedirect(config.getSSIDGetLogoutSiteUrl() + "?status="
                + status);
          } else {
            throw new IOException("SSID Logout Url config is empty");
          }
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Exception occurred could not redirect after logout :", ex);
      response.sendRedirect(config.getSSIDMaintainancePageUrl());
      response.setStatus(StatusEnum.INTERNAL_SERVER_ERROR.getValue());
    }
  }
}
