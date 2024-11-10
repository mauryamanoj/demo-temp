package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
 * This is used to get experience suggestions from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v1/experiencecategories . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
        + "/bin/api/v1/experiencecategories" })
@Slf4j
public class ExperienceCategoriesServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;
  /**
   * The RoadTrip Data service.
   */
  @Reference
  private transient ExperienceService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          service.getExperienceCategories(RestHelper.getParameters(request)));

      LOGGER.error("Success :{}, {}", Constants.EXPERIENCECATEGORIES_SERVLET, response);

    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.EXPERIENCECATEGORIES_SERVLET,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

}
