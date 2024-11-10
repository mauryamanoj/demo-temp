package com.saudi.tourism.core.login.servlets;

import java.io.IOException;
import javax.servlet.Servlet;

import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.login.servlets.SSIDLoyaltyEnrollmentServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDLoyaltyEnrollmentServlet.SERVLET_PATH;

/**
 * SSID Loyalty Enrollment Servlet.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_POST,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + SERVLET_PATH})
@Slf4j
public class SSIDLoyaltyEnrollmentServlet extends BaseAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v2/user/loyalty-enrollment";

  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "SSID Loyalty Enrollment Servlet";


  /**
   * The Login UserService.
   */
  @Reference
  private transient SSIDService ssidService;

  @Override
  protected void doPost(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    try {
      if (StringUtils.isBlank(request.getHeader("token"))) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            "Missing token");
        return;
      }

      final var token = request.getHeader("token");

      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper()
              .writeValueAsString(ssidService.enrollToLoyaltyProgram(token)));

    } catch (Exception e) {
      LOGGER.error("Error in SSID Loyalty Enrollment", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in " + DESCRIPTION));
    }
  }
}
