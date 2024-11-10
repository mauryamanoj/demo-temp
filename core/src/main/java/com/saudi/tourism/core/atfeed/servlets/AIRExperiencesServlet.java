package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.atfeed.utils.AIRCsvExporter;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * This is used to get all experiences from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/air/v1/experience . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "AIRExperience Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/experience",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + AIRConstants.CSV })
@Slf4j

public class AIRExperiencesServlet extends SlingSafeMethodsServlet {
  private static final long serialVersionUID = 1L;
       /**
       * The RoadTrip Data service.
       */
  @Reference
  private transient ExperienceService service;
  /**
  * The Resource Resolver factory.
  */
  @Reference
  private transient ResourceResolverFactory resolverFactory;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String locale = Constants.DEFAULT_LOCALE;
    try {
      if (null != request.getRequestParameter(Constants.LOCALE)
           && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
        locale = request.getRequestParameter(Constants.LOCALE).getString();
      }
      AIRCsvExporter.writeCSV(response, StatusEnum.SUCCESS.getValue(),
                  service.getAllExperiences(RestHelper.getParameters(request)), resolverFactory);
      LOGGER.debug("Success :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET, response);
    } catch (HTTPException h) {
      LOGGER.error("HTTPException Error",
          new Object[] {locale, h});
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET,
          new Object[] {locale, e});
    }
  }
}
