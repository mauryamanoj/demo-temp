package com.saudi.tourism.core.login.servlets;


import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.saudi.tourism.core.login.servlets.SSIDLoginUrlServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDLoginUrlServlet.SERVLET_PATH;

/**
 * The Get login url servlet for SSID.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
          + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
          + SERVLET_PATH})
@Slf4j
public class SSIDLoginUrlServlet extends SlingSafeMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v1/ssid/login-url";

  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Get SSID Login URL Servlet";

  /**
   * SSID service.
   */
  @Reference
  private transient SSIDService ssidService;

  /**
   * Saudi SSID Login Configurations.
   */
  @Reference
  private SaudiSSIDConfig saudiSSIDConfig;

  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
    final RequestParameter language = request.getRequestParameter("lang");
    final RequestParameter referral = request.getRequestParameter("referral");

    String locale = "en";
    if (null != language) {
      locale = language.toString();
    }

    // Start building the redirect URL with the language parameter
    StringBuilder redirectUrl = new StringBuilder(ssidService.getSSIDLoginUrl() + "&lang=" + locale);

    // Check if the referral parameter exists and is not empty, then append it
    if (referral != null && StringUtils.isNotBlank(referral.getString())) {
      redirectUrl.append("&referral=")
          .append(URLEncoder.encode(referral.getString(), StandardCharsets.UTF_8.toString()));
    }

    try {
      response.sendRedirect(redirectUrl.toString());
      response.getWriter().write("SSID login url is generated and forwarded successfully");
    } catch (Exception ex) {
      LOGGER.error("Exception occurred while generating SSID login URL :", ex);
      String maintainanceUrl = saudiSSIDConfig.getSSIDMaintainancePageUrl();
      if (StringUtils.isNotBlank(maintainanceUrl)) {
        String redirectErrPage = maintainanceUrl.replace("{locale}", locale);
        response.sendRedirect(redirectErrPage);
      }
    }
  }
}
