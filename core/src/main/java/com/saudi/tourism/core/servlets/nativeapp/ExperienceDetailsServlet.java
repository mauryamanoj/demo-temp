package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
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

/**
 * This is used to get experience details from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v2/experiencedetails . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
        + "/bin/api/v2/experiencedetails" })
@Slf4j
public class ExperienceDetailsServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;

  /** The Constant statusCode. */
  static final int STATUS_CODE = 404;

  /**
   * The RoadTrip Data service.
   */
  @Reference
  private transient ExperienceService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String experienceId = request.getParameter("experienceId");
    try {
      if (StringUtils.isBlank(experienceId)) {
        throw new RuntimeException("Missing experienceId parameter");
      }
      JsonElement experienceDetails = (JsonElement) service.getExperienceDetails(
          RestHelper.getParameters(request), experienceId);
      JsonObject experienceJson = experienceDetails.getAsJsonObject();
      if (experienceJson.has("status") && experienceJson.get("status").getAsString().equals("Failed")) {
        experienceJson.remove("status");
        response.setStatus(STATUS_CODE);
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", STATUS_CODE);
        jsonObject.addProperty("message", "Not found");
        response.getWriter().write(gson.toJson(jsonObject));
      } else if (experienceJson.has("status") && experienceJson.get("status").getAsString().equals("Success")) {
        experienceJson.remove("status");
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            experienceJson.toString());
      }
      LOGGER.error("Success :{}, {}", Constants.EXPERIENCEDETAILS_SERVLET, response);
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.EXPERIENCEDETAILS_SERVLET,
          e.getLocalizedMessage());
      response.setStatus(StatusEnum.BAD_REQUEST.getValue());
      response.setHeader("Access-Control-Allow-Methods", "GET");
      response.setContentType("application/json; charset=UTF-8");
      Gson gson = new Gson();
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("code", STATUS_CODE);
      jsonObject.addProperty("message", "Not found");
      response.getWriter().write(gson.toJson(jsonObject));
    }
  }

}
