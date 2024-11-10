
package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.WinterCardsService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

/**
 * The Winter 2021 Cards Data Servlet.
 */
@Component(immediate = true, service = Servlet.class, property = {
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/test" })
@Slf4j
public class WinterCardsServlet extends SlingAllMethodsServlet {

  /**
   * The Winter 2021 Cards Data service.
   */
  @Reference
  private transient WinterCardsService service;

  @Override
  protected void doGet(final SlingHttpServletRequest request,
                       final SlingHttpServletResponse response) throws IOException {
    String apiResponse = StringUtils.EMPTY;
    PrintWriter writer = response.getWriter();
    Map<String, Object> requestObject = RestHelper.getParameters(request);
    String type = request.getParameter("type");
    if (Objects.equals(type, "category")) {
      apiResponse = service.getExperienceCategories(requestObject);
    } else if (Objects.equals(type, "experience")) {
      apiResponse = service.getExperience(requestObject);
    }
    writer.write(apiResponse);
  }
}
