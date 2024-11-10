package com.saudi.tourism.core.models.components.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SSIDRestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.saudi.tourism.core.models.components.servlets.ValidateCaptchaServlet.DESCRIPTION;
import static com.saudi.tourism.core.models.components.servlets.ValidateCaptchaServlet.SERVLET_PATH;

/**
 * The Message Type servlet.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH })
@Slf4j
public class ValidateCaptchaServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/contactus/validateCaptcha";
  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Validate Captcha Servlet";

  /** The Constant ENDPOINT. */
  static final String ENDPOINT = "https://www.google.com/recaptcha/api/siteverify?"
      + "secret=6LdwwdUbAAAAAJyhu4K7bqrm1RlX8axQNmWm-MMF&response=";

  @Override
  protected void doPost(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    try {
      String token = request.getRequestParameter("token").getString();
      String url = ENDPOINT + token;
      ResponseMessage resMsg = SSIDRestHelper.executeMethodPost(url, null, null, false, false);
      JsonObject responseMsg = new JsonParser().parse(resMsg.getMessage()).getAsJsonObject();
      LOGGER.info("resMsg is {}", responseMsg.toString());
      response.getWriter().write(responseMsg.toString());
    } catch (Exception exception) {
      LOGGER.error("Exception is {}", exception);
    }
  }
}
