package com.saudi.tourism.core.servlets;

import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.HealthService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Health Servlet .
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
    resourceTypes = "sauditourism/components/content/healthRegulations",
    methods = HttpConstants.METHOD_POST,
    extensions = "json",
    selectors = "health")
public class HealthServlet extends SlingAllMethodsServlet {

  /**
   * Serial UID .
   */
  private static final long serialVersionUID = 1L;

  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(HealthServlet.class);

  /**
   * The Health service.
   */
  @Reference
  private HealthService crs;

  /**
   * Post Method.
   */
  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/json");
    String address = request.getParameter("address");
    JsonObject requirement = crs.health(request, response, address);
    response.getWriter().print(requirement);
    response.getWriter().close();
  }
}
