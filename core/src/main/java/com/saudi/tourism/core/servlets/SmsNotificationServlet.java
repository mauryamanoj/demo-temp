package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.SMSNotificationService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.saudi.tourism.core.servlets.SmsNotificationServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This servlet provides an API for sms notification template.
 */


@Component(service = Servlet.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
        SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        SLING_SERVLET_PATHS + Constants.EQUAL + SmsNotificationServlet.SERVLET_PATH})
public class SmsNotificationServlet extends SlingAllMethodsServlet {


  /**
   * logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(SmsNotificationServlet.class);

  /**
   * Using SMSNotificationService.
   */
  @Reference
  private transient SMSNotificationService smsNotificationService;

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "SMS notification API (Servlet)";

  /**
   * URL path for this servlet.
   */

  public static final String SERVLET_PATH = "/bin/api/v1/sms";


  /**
   * Initialize variable.
   */
  private String templatePath = null;

  @SuppressWarnings("CheckStyle")
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String templateData = null;
    String locale = request.getParameter("locale");
    try {
      if (StringUtils.isNotBlank(locale)) {
        templateData = smsNotificationService.getSmsTemplate(userService, response, locale);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), templateData);
      }
    } catch (Exception e) {
      LOG.error("Error in getting module:{}, {}", templatePath,
          e.getLocalizedMessage(), e);
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
              MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }
  }
}
