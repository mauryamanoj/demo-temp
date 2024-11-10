/**
 *
 */
package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.utils.GeneralUtils;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * This is used to get trending cards from the node. Sample URL :
 * http://localhost:4502/bin/api/air/v1/trending
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Trending feed servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/trending",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv" })

@Slf4j
public class TrendingCardsActivitiesServlet extends AbstractATFeedServlet {

  private static final long serialVersionUID = 21L;

  /** Nested folder path. */
  private static final String NESTED_FOLDER_PATH = "do";

  /**
   * The service for obtaining resource resolver.
   */
  @Reference
  private UserService resolverService;

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

  /**
   * The Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * The Settings Service.
   */
  @Reference
  private SlingSettingsService settingsService;

  /**
   * The Region City Service.
   */
  @Reference
  private RegionCityService regionCityService;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {
      String locale = generalQueryService.getLocale(request);
      String searchLocation = getSearchLocation(locale, NESTED_FOLDER_PATH);
      SearchResult searchResult = generalQueryService.findRegionModel(request, searchLocation);
      String csv = GeneralUtils.getFeedsCSV(request, searchResult, i18nProvider, settingsService, regionCityService);
      OutputStream outputStream = response.getOutputStream();
      response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
      response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.FEEDS_RESPONSE_HEADER);
      outputStream.write(csv.getBytes("UTF-8"));
      outputStream.flush();
      outputStream.close();
      LOGGER.debug("Success :{}, {}", "TrendingFeedServlet", response);

    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error", h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "TrendingFeedServlet", e.getLocalizedMessage(), e.getStackTrace());
    }
  }

}
