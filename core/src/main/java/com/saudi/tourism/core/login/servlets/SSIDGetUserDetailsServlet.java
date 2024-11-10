package com.saudi.tourism.core.login.servlets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.SSIDLoginUserService;
import com.saudi.tourism.core.login.services.impl.SSIDLoginUserServiceImpl;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet.DESCRIPTION;
import static com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet.SERVLET_PATH;

/**
 * The type Get user info from auth0.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_POST,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + SERVLET_PATH})
@Slf4j
public class SSIDGetUserDetailsServlet extends BaseAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v2/user/get";
  /**
   * This servlet description.
   */
  static final String DESCRIPTION = "Get User Servlet V2";

  /**
   * "tripPlan" param name (for filtering favorites).
   */
  public static final String PN_TRIP_PLAN = "tripPlan";

  /**
   * List of allowed filtering parameters per request type.
   */
  private static final Map<String, Set<String>> ALLOWED_PARAMS = ImmutableMap.of(
      //@formatter:off
      SSIDLoginUserServiceImpl.TRIPS, ImmutableSet.of(Constants.PN_LOCALE, Constants.PN_ID),
      SSIDLoginUserServiceImpl.FAVORITES, ImmutableSet.of(Constants.PN_LOCALE, PN_TRIP_PLAN,
          Constants.CITY)
      //@formatter:on
  );

  /**
   * The Login UserService.
   */
  @Reference
  private transient SSIDLoginUserService loginUserService;

  @Override
  protected void doPost(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    if (StringUtils.isBlank(request.getHeader("token"))) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "Missing token");
      return;
    }
    String type = request.getRequestPathInfo().getExtension();

    UserIDToken userIDToken = getUserID(request);
    try {

      final Map<String, Object> filterParams = RestHelper.getParameters(request);
      if (filterParams.size() > 0) {
        // Trip plans will be filtered by id, or favorites will be checked for "tripPlan: true" or
        // for specific city etc.
        checkAllowedParameters(request, response, type, filterParams);

        // If the locale param was passed in json params, update it in the user token object
        final String filterParamsLocale = (String) filterParams.get(Constants.PN_LOCALE);
        if (StringUtils.isNotBlank(filterParamsLocale) && Constants.DEFAULT_LOCALE
            .equals(userIDToken.getLocale())) {
          userIDToken.setLocale(filterParamsLocale);
        }
      }

      // Else - output without filtering
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          loginUserService.getUserDetailsFull(userIDToken, type, filterParams));

    } catch (Exception e) {
      LOGGER.error("Error in SSID Get user details", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in " + DESCRIPTION));
    }
  }

  /**
   * Gets user id.
   *
   * @param request the request
   * @return the user id
   */
  @NotNull public static UserIDToken getUserID(final SlingHttpServletRequest request) {
    UserIDToken userIDToken = new UserIDToken();

    String userID = request.getHeader("userID");
    if (Objects.isNull(userID)) { // CDN converting userID to userid
      userID = request.getHeader("userid");
    }
    userIDToken.setToken(request.getHeader("token"));
    String locale =
        StringUtils.defaultIfBlank(request.getParameter("locale"), Constants.DEFAULT_LOCALE);
    userIDToken.setLocale(locale);
    userIDToken.setUser(userID);
    return userIDToken;
  }

  /**
   * Checks that specified parameters are applicable for this request.
   *
   * @param request       servlet request
   * @param response      servlet response
   * @param type          this user details request type (favorites, trips etc.)
   * @param parametersMap map of parameters from convert instance
   */
  protected void checkAllowedParameters(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response, final String type,
      final Map<String, Object> parametersMap) {

    final Set<String> allowedParams = ALLOWED_PARAMS.get(type);
    if (allowedParams != null) {
      final Collection<String> unknownParams =
          CollectionUtils.subtract(parametersMap.keySet(), allowedParams);

      if (!unknownParams.isEmpty()) {
        LOGGER.debug("Found unknown parameters: {}, allowed parameters for this request are: {}",
            unknownParams, allowedParams);
      }

      // Remove all unknown params
      for (String key : unknownParams) {
        parametersMap.remove(key);
      }
    }
  }
}
