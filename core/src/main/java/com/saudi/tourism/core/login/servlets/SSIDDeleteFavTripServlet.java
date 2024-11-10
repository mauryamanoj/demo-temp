package com.saudi.tourism.core.login.servlets;

import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.SSIDLoginUserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.saudi.tourism.core.login.servlets.SSIDDeleteFavTripServlet.SERVLET_PATH;

/**
 * The type Update user info from auth0.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Delete Fav/Trip Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_DELETE,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + SERVLET_PATH})
@Slf4j
public class SSIDDeleteFavTripServlet extends SlingAllMethodsServlet {

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v2/user/delete";
  /**
   * The Login UserService.
   */
  @Reference
  private transient SSIDLoginUserService loginUserService;

  @Override
  protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    if (StringUtils.isBlank(request.getHeader("token"))) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "Missing token");
      return;
    }
    String type = request.getRequestPathInfo().getExtension();
    UserIDToken userIDToken = SSIDGetUserDetailsServlet.getUserID(request);

    try {
      String jsonRequestText = IOUtils.toString(request.getReader());

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), loginUserService
          .deleteFavTrip(SSIDUpdateUserDetailsServlet.handlePath(jsonRequestText, type), userIDToken,
              type));

    } catch (Exception e) {
      LOGGER.error("Error in SSID delete user favorites or trips", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in Delete User Servlet v2"));
    }
  }
}
