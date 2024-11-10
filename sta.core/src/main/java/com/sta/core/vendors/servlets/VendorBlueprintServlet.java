package com.sta.core.vendors.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.sta.core.vendors.VendorsService;

import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The servlet is used to manually trigger sitemap generation for certain
 * sector.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + "=" + "Vendor Blueprint servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + "="
                   + "/bin/author/api/v1/vendor/blueprint"})
public class VendorBlueprintServlet extends SlingSafeMethodsServlet {

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The vendorsService.
   */
  @Reference
  private VendorsService vendorsService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String locale = request.getParameter("locale");

    if (StringUtils.isNotEmpty(locale)) {
      vendorsService.createVendorPages();
      response.getWriter().write("Successfully generated Vendor Blueprint for " + locale);
      response.setStatus(HttpStatus.SC_OK);


    } else {
      response.sendError(HttpStatus.SC_BAD_REQUEST);
      response.getWriter().write("Provide a valid locale as parameter");
    }
  }
}
