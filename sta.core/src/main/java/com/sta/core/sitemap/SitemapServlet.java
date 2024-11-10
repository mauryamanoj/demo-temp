package com.sta.core.sitemap;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

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
           property = { Constants.SERVICE_DESCRIPTION + "=" + "Sitemap rebuild servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + "="
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + "="
                   + "/bin/api/v1/sitemap/build"})
public class SitemapServlet extends SlingSafeMethodsServlet {

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The sitemap service used to build sitemaps.
   */
  @Reference
  private SitemapService sitemapService;

  /**
   * The sitemap service used to generate the sitemaps.
   */
  @Reference
  private UmrahSitemapService umrahSitemapService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String locale = request.getParameter("locale");

    if (StringUtils.isNotEmpty(locale)) {
      try {
        sitemapService.createSitemap(locale);
        umrahSitemapService.createSitemap(locale);
        response.getWriter().write("Successfully generated sitemap for " + locale);
        response.setStatus(HttpStatus.SC_OK);
      } catch (ServiceUserException e) {
        response.getWriter().write("Please configure a sitemapUser for sitemap to generate");
        response.setStatus(HttpStatus.SC_OK);
      }

    } else {
      response.sendError(HttpStatus.SC_BAD_REQUEST);
      response.getWriter().write("Provide a valid locale as parameter");
    }
  }
}
