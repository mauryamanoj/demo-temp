package com.saudi.tourism.core.login.servlets;

import static com.saudi.tourism.core.login.servlets.SSIDGetUserInfoServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDGetUserInfoServlet.SERVLET_PATH;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import lombok.extern.slf4j.Slf4j;

/**
 * The Callback servlet.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH })
@Slf4j
public class SSIDGetUserInfoServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v1/ssid/userInfo";
  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "User Information Servlet";

  /**
   * SSID service.
   */
  @Reference
  private transient SSIDService ssidService;

  @Override
  protected void doPost(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    ResponseMessage respObj = null;
    try {
      final String token = request.getHeader("token");
      if (StringUtils.isNotBlank(token)) {
        respObj = ssidService.getUserInfo(token);
        if (null != respObj && StringUtils.isNotBlank(respObj.getMessage())) {
          LOGGER.debug("SSID user info response: " + respObj.getMessage());
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              respObj);
        } else {
          throw new IOException("User data is not sent from SSID");
        }
      } else {
        throw new ServletException("Required parameters for user details not sent");
      }
    } catch (Exception e) {
      LOGGER.error("Exception with get user info is {}", e);
      if (null != respObj && StringUtils.isNotBlank(respObj.getStatus())) {
        CommonUtils.writeJSON(response, Integer.valueOf(respObj.getStatus()),
            respObj);
      } else {
        CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
      }
    }
  }
}
