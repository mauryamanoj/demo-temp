package com.saudi.tourism.core.login.servlets;

import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.LoginUserService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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

import static com.saudi.tourism.core.login.servlets.DeleteFavTripServlet.SERVLET_PATH;

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
public class DeleteFavTripServlet extends SlingAllMethodsServlet {

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/user/delete";
  /**
   * The Login UserService.
   */
  @Reference
  private transient LoginUserService loginUserService;

  @Override
  protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String type = request.getRequestPathInfo().getExtension();
    UserIDToken userIDToken = GetUserDetailsServlet.getUserID(request);

    try {
      String jsonRequestText = IOUtils.toString(request.getReader());

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), loginUserService
          .deleteFavTrip(UpdateUserDetailsServlet.handlePath(jsonRequestText, type), userIDToken,
              type));

    } catch (Exception e) {
      LOGGER.error("Error in user profile update module:{}", e.getLocalizedMessage(), e);
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }
}
