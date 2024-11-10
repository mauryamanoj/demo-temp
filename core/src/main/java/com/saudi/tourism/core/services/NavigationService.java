package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * This defines all methods to execute business of navigation header/footer.
 */
public interface NavigationService {

  /**
   * Gets the navigation header.
   * @param site sitemap.
   * @param request          the request
   * @param resourceResolver the resource resolver
   * @param language         the language
   * @param path             the path
   * @return the navigation header
   */
  NavigationHeader getNavigationHeader(SlingHttpServletRequest request,
      ResourceResolver resourceResolver, String language, String path, String site);

}
