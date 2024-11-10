package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.atfeed.utils.GeneralUtils;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * This is used to get card experience from the node.
 * Sample URL : http://localhost:4502/bin/api/air/v1/card-deals
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Deal Cards Feed Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/card-deals",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class PartnerDealsFeedServlet extends AbstractATFeedServlet {
  private static final long serialVersionUID = 15L;
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
      String locale = Constants.DEFAULT_LOCALE;
      if (null != request.getRequestParameter(Constants.LOCALE)
           && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
        locale = request.getRequestParameter(Constants.LOCALE).getString();
      }
      String searchLocation = getHomeLocation(locale);
      SearchResult searchResult = generalQueryService.findRegionModel(request, searchLocation);

      String csv = GeneralUtils.getPartnerCSV(request, searchResult);

      getOutputStream(response, csv);

      LOGGER.debug("Success :{}, {}", "CarRentalsFeedServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "CarRentalsFeedServlet",
          e.getLocalizedMessage());
    }
  }

}
