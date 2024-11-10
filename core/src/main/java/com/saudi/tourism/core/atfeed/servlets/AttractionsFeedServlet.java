package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.QueryBuilder;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * The type Get all attractions for a locale.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Attractions Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/attractions",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class AttractionsFeedServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";
  /** Property name of image of destination. */
  public static final String FEATURE_IMAGE = "featureImage";
  /** Entity ID split from content path. */
  public static final int ENTITY_ID_SPLIT = 45;
  /** Entity Page split from content path. */
  public static final int ENTITY_PAGE_SPLIT = 21;

  /**Discover regions category. */
  private static final String ATTRACTIONS = "attractions";

  /**ADD. */
  private static final String ADD = "+";
  /**
   * Cities service to get all the info about filter cities.
   */
  @Reference
  private transient RegionCityService citiesService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * The Activities Service.
   */
  @Reference
  private GeneralQueryService generalQueryService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String csv = generalQueryService.getAttractionFeed(request, response);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "DestinationsFeedServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "DestinationsFeedServlet",
          e.getLocalizedMessage());
    }
  }
}
