package com.saudi.tourism.core.atfeed.services;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.models.app.location.AppCity;

public interface GeneralQueryService {
  /**
   * to get search Result.
   * @param request
   * @param trendingPath
   * @return query .
   */
  SearchResult findRegionModel(SlingHttpServletRequest request, String trendingPath);
  /**
   *
   * @param request
   * @return locale.
   */
  String getLocale(SlingHttpServletRequest request);
  /**
   *
   * @param resolver
   * @param path
   * @return cities
   */
  Map<String, AppCity> getCities(ResourceResolver resolver, String path);

  /**
   *
   * @param request
   * @param response
   * @return csv
   * @throws IOException
   * @throws ServletException
   */
  String getAttractionFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException;

  /**
  *
  * @param request
  * @param response
  * @return csv
  * @throws IOException
  * @throws ServletException
  */
  String getItinerariesFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException;
  /**
  *
  * @param request
  * @param response
  * @return csv
  * @throws IOException
  * @throws ServletException
  */
  String getPackageCSVFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException;
  /**
  *
  * @param request
  * @param response
  * @return csv
  * @throws IOException
  * @throws ServletException
  */
  String getPartnerPromotionFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException;

  /**
  *
  * @param request
  * @param response
  * @return csv
  * @throws IOException
  * @throws ServletException
  */
  String getTransportModeFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException;

  /**
   * to get search Result.
   * @param request
   * @param discoverRegionsPath
   * @return query .
   */
  SearchResult findDiscover(SlingHttpServletRequest request, String discoverRegionsPath);
}
