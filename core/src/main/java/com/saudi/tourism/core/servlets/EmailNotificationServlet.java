package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.EmailNotificationService;
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

import static com.saudi.tourism.core.servlets.EmailNotificationServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This servlet provides an API for email notification template.
 */


@Component(service = Servlet.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
        SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        // TODO FIXME Remove this
        SLING_SERVLET_PATHS + Constants.EQUAL + EmailNotificationServlet.SERVLET_PATH})
public class EmailNotificationServlet extends SlingAllMethodsServlet {


  /**
   * logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationServlet.class);

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;

  /**
   * Using EMAILNotificationService.
   */
  @Reference
  private transient EmailNotificationService emailNotificationService;

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "EMAIL notification API (Servlet)";

  /**
   * URL path for this servlet.
   */

  public static final String SERVLET_PATH = "/bin/api/v1/email";


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
        templateData = emailNotificationService.getEmailTemplate(userService, response, locale);
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
