package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import com.saudi.tourism.core.models.common.ObjectResponse;
import com.saudi.tourism.core.models.components.tripplan.CreateTripPlanFilter;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlan;
import com.saudi.tourism.core.services.SSIDTripPlanService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

import static com.saudi.tourism.core.models.components.tripplan.TripPlannerUtils.checkGetByPath;
import static com.saudi.tourism.core.servlets.SSIDTripPlanServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This Servlet provides web and app API for Trip Plans.
 */
@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
               SLING_SERVLET_PATHS + Constants.EQUAL + SSIDTripPlanServlet.SERVLET_PATH})
@Slf4j
public class SSIDTripPlanServlet extends BaseAllMethodsServlet {

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "Trip Plan API (Servlet)";

  /**
   * URL path for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v2/trip-plan";

  /**
   * The Event service.
   */
  @Reference
  private transient SSIDTripPlanService tripPlanService;

  /**
   * Servlet's main method (GET).
   *
   * @param request  the request
   * @param response the response
   * @throws IOException if response is already committed or another servlet exception
   * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doGet(SlingHttpServletRequest,
   * SlingHttpServletResponse)
   */
  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request,
      @NotNull SlingHttpServletResponse response) throws IOException {

    try {
      final Convert<CreateTripPlanFilter> parameters =
          new Convert<>(request, CreateTripPlanFilter.class);
      final Map<String, Object> parametersMap = parameters.getMapData();
      final CreateTripPlanFilter filter = parameters.getRequestData();

      // Check if the path parameter was passed in the request
      //  (.../trip-plan?path=/path/to/trip/itinerary/page
      //  or .../trip-plan.json/path/to/trip/itinerary/page)
      String path = checkGetByPath(request, filter);

      // If the path parameter was provided, don't check other params
      if (StringUtils.isBlank(path)) {
        if (!Boolean.TRUE.equals(filter.getWithData())) {
          // For empty trip plan only locale is required
          checkNecessaryParameters(request, response, parametersMap, Constants.PN_LOCALE);
        } else {
          // For default trip plan request locale and city are required
          checkNecessaryParameters(request, response, parametersMap, Constants.PN_LOCALE,
              Constants.CITY);
        }
      }

      final UserIDToken userId = SSIDGetUserDetailsServlet.getUserID(request);
      final TripPlan tripPlan = tripPlanService.createTripPlan(filter, userId);
      if (response.isCommitted()) {
        return;
      }

      // TODO Switch from Gson to ObjectMapper and remove custom Gson serializers for Trip Plan
      //  (they are too hard to support)
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), RestHelper.getObjectMapper()
          .writeValueAsString(new ObjectResponse<>(MessageType.SUCCESS.getType(), tripPlan))
      );

    } catch (Exception e) {
      outError(request, response, e, MESSAGE_ERROR_IN, DESCRIPTION);
    }
  }

  /**
   * Post servlet method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws IOException if response is already committed or another servlet exception
   */
  @Override
  protected void doPost(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws IOException {
    this.doGet(request, response);
  }
}
