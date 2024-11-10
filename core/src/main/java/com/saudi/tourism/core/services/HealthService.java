package com.saudi.tourism.core.services;

import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * HealthService.
 */
public interface HealthService {

  /**
   * Method Health .
   *
   * @param request request
   * @param response response
   * @param address address
   * @return json
   */
  JsonObject health(SlingHttpServletRequest request, SlingHttpServletResponse response, String address);
}
