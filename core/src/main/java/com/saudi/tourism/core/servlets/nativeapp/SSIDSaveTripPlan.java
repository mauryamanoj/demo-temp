package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.Gson;
import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.servlets.GetUserDetailsServlet;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlan;
import com.saudi.tourism.core.services.v2.SSIDTripPlanService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * SSID Save Trip plan Servlet to create and update object with Single API.
 */
@Component(service = Servlet.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SSIDSaveTripPlan.DESCRIPTION,
    SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    SLING_SERVLET_PATHS + Constants.EQUAL + SSIDSaveTripPlan.SERVLET_PATH})
@Slf4j
public class SSIDSaveTripPlan extends SlingAllMethodsServlet {
  /**
   * Description.
   */
  public static final String DESCRIPTION = "Save Trip plan for Native app to create and update user data API";

  /**
   * URL path for this servlet.
   */
  public static final String SERVLET_PATH = "/bin/api/v2/save-trip";

  /**
   * The Trip Plan Service.
   */
  @Reference
  private transient SSIDTripPlanService ssidTripPlanService;

  /**
   * @param request  post request .
   * @param response
   * @throws ServletException ServletException .
   * @throws IOException      IOEXception .
   */
  @Override
  protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {
      final UserIDToken userId = GetUserDetailsServlet.getUserID(request);
      if (Objects.nonNull(userId.getUser())) {
        TripPlan tripPlan = new Gson().fromJson(request.getReader(), TripPlan.class);
        if (Objects.isNull(tripPlan)) {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(), "Invalid Request Body");
          return;
        }
        List<TripPlan> createTripList = ssidTripPlanService.createUpdateTripPlanNativeApp(tripPlan, userId);
        if (Objects.isNull(createTripList)) {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
              "Internal Server Error ");
        } else {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(
                  createTripList));
        }
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.FORBIDDEN_AUTH_ERROR.getValue(),
            "Invalid User ID or token ");
      }
    } catch (MiddlewareException | SSIDFunctionnalException e) {
      LOGGER.error("Error in SSID Update user details", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in SSID Save trip plan"));


    } catch (RepositoryException e) {
      LOGGER.error("Repository Exception :{}", e.getMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "Repository Exception");
    } catch (Exception e) {
      LOGGER.error("Exception :{}", e.getMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "Internal Server Exception");
    }
  }
}
