package com.saudi.tourism.core.servlets.nativeapp.v3;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;

import static com.saudi.tourism.core.servlets.nativeapp.v3.ExperienceDetailsServlet.EXPERIENCEDETAILS_SERVLET;

/**
 * This is used to get experience details from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v3/experiencedetails . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + EXPERIENCEDETAILS_SERVLET,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
        + "/bin/api/v3/experiencedetails" })
@Slf4j
public class ExperienceDetailsServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;

  /** The Constant statusCode. */
  static final int STATUS_CODE = 404;

  /** Servlet description. */
  static final String EXPERIENCEDETAILS_SERVLET = "Experiences Details servlet v3";

  /**
   * The RoadTrip Data service.
   */
  @Reference
  private transient ExperienceService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String experienceId = request.getParameter("experienceId");
    String locale = "en";
    if (null != request.getParameter("lang")) {
      locale = request.getParameter("lang");
    }

    try {
      if (StringUtils.isBlank(experienceId)) {
        throw new RuntimeException("Missing experienceId parameter");
      }
      JsonObject experienceJson = service.getVenueDetails(
          locale, experienceId);
      if (Objects.isNull(experienceJson)) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            "No Experiences found");

      } else {
        experienceJson.remove("status");
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            experienceJson.toString());
      }

    } catch (Exception e) {
      LOGGER.error("Error in {}", EXPERIENCEDETAILS_SERVLET, e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "InternalServerError While fetching Experiences");

    }
  }

}
