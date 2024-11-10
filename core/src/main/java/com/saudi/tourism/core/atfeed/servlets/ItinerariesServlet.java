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
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Itineraries Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/itineraries",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class ItinerariesServlet extends AbstractATFeedServlet {

  /**
   * Cities service to get all the info about filter cities.
   */
  @Reference
  private transient RegionCityService citiesService;
  /**
   * The Activities Service.
   */
  @Reference
  private GeneralQueryService generalQueryService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String csv = generalQueryService.getItinerariesFeed(request, response);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "ItinerariesServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "ItinerariesServlet",
          e.getLocalizedMessage());
    }
  }


}
