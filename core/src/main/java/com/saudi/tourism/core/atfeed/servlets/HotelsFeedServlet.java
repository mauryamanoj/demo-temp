package com.saudi.tourism.core.atfeed.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.utils.GeneralUtils;
import com.saudi.tourism.core.services.HotelService;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * The type Get all hotels for a locale.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Hotels Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/hotels",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class HotelsFeedServlet extends AbstractATFeedServlet {

  /**
   * The Hotels service.
   */
  @Reference
  private transient HotelService hotelService;
  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String locale = Constants.DEFAULT_LOCALE;
      if (null != request.getRequestParameter(Constants.LOCALE)
            && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
        locale = request.getRequestParameter(Constants.LOCALE).getString();
      }
      String hotelsPath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();

      Query query = queryBuilder
          .createQuery(PredicateGroup.create(getPredicateQueryMap(hotelsPath)),
          request.getResourceResolver().adaptTo(Session.class));
      SearchResult searchResult = query.getResult();

      String csv = GeneralUtils.getHotelsFeedsCSV(request, searchResult);

      getOutputStream(response, csv);

      LOGGER.error("Success :{}, {}", "HotelsFeedServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "HotelsFeedServlet",
          e.getLocalizedMessage());
    }
  }

  /**
   * Querybuilder map for getting all hotels.
   *
   * @param path hotels path
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.HOTELS_RES_TYPE);
    map.put("2_property", JCR_TITLE);
    map.put("2_property.operation", "exists");
    map.put("p.limit", "-1");
    return map;
  }
}
