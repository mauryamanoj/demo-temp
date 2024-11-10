package com.saudi.tourism.core.models.nativeApp.page;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.app.location.AppCity;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.saudi.tourism.core.utils.Constants.CITIES_PATH_FORMAT;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_MINUTES_IN_HOUR;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS_IN_MINUTE;
import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;
import static com.saudi.tourism.core.utils.Constants.RT_APP_CITY_PAGE;
import static com.saudi.tourism.core.utils.Constants.RT_APP_REGION_PAGE;


/**
 * This Servlet is responsible for provide the combined values of Region and Cities .
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION
      + Constants.EQUAL + "This Servlet is combined of APP Region Cities Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
    + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
    + "/bin/api/v2/app/regions-cities"
})
public class AppRegionCitiesServlet extends SlingAllMethodsServlet {

  /**
   *
   */

  private final Logger logger = LoggerFactory.getLogger(AppRegionCitiesServlet.class);
  /**
   * The cache.
   */
  @Reference
  private transient Cache memCache;
  /**
   * Memory cache key.
   */
  public static final String CITY_MEM_CACHE_KEY_PREFIX = "app-city-dic:";

  /**
   * Region Memory cache key.
   */
  public static final String REGION_MEM_CACHE_KEY_PREFIX = "app-region-dic:";

  /**
   * Path to regions path.
   */
  private static final String REGION_PATH_FORMAT = ROOT_APP_CONTENT_PATH + "/%s/regions";

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
    String locale = CommonUtils.getLocale(request);
    String cityMemKey = CITY_MEM_CACHE_KEY_PREFIX + locale;
    String reigonMemKey = REGION_MEM_CACHE_KEY_PREFIX + locale;
    String cityPath = String.format(CITIES_PATH_FORMAT, locale);
    String regionPath = String.format(REGION_PATH_FORMAT, locale);

    List<AppCity> cityList = (List<AppCity>) memCache.computeIfAbsent(cityMemKey,
        k -> loadCities(request.getResourceResolver(), cityPath),
          (long) NUMBER_OF_SECONDS * NUMBER_OF_MINUTES_IN_HOUR * NUMBER_OF_SECONDS_IN_MINUTE);

    List<AppRegion> regionList = (List<AppRegion>) memCache.computeIfAbsent(reigonMemKey,
        k -> loadRegions(request.getResourceResolver(), regionPath),
          (long) NUMBER_OF_SECONDS * NUMBER_OF_MINUTES_IN_HOUR * NUMBER_OF_SECONDS_IN_MINUTE);

    // todo mem cache for this .

    List<AppRegion> finalList = addCitiesByRegions(regionList, cityList);
    CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(finalList));
  }

  /**
   * Load regions.
   *
   * @param resolver resolver
   * @param path     path
   * @return list of cities
   */
  private List<AppCity> loadCities(ResourceResolver resolver, String path) {
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page root = pageManager.getPage(path);
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    List<AppCity> list = new ArrayList<>();
    Set<String> idSet = new HashSet<>();
    for (@NotNull Iterator<Page> it = root.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_CITY_PAGE)) {
        AppCity appCity = page.getContentResource().adaptTo(AppCity.class);
        if (Objects.nonNull(appCity) && !idSet.contains(appCity.getId())) {
          list.add(appCity);
          idSet.add(appCity.getId());
        }
      }
    }
    Comparator<AppCity> comparator = Comparator.comparing(AppCity::getRank);
    list.sort(comparator);
    return list;
  }

  /**
   * Load regions.
   *
   * @param resolver resolver
   * @param path     path
   * @return list of regions
   */
  private List<AppRegion> loadRegions(ResourceResolver resolver, String path) {
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page root = pageManager.getPage(path);
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    List<AppRegion> list = new ArrayList<>();
    for (@NotNull Iterator<Page> it = root.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_REGION_PAGE)) {
        list.add(page.getContentResource().adaptTo(AppRegion.class));
      }
    }
    return list;
  }

  /**
   * This method is add cities under the respective regions on the basis of the region select in the cities page.
   *
   * @param regionList regionList
   * @param cityList cityList
   * @return regionListByCities
   */
  private List<AppRegion> addCitiesByRegions(List<AppRegion> regionList, List<AppCity> cityList) {
    List<AppRegion> regionListByCities = new ArrayList<>();
    for (AppRegion regions : regionList) {
      String id = regions.getId();
      String region = id.substring(id.lastIndexOf("/") + 1);
      List<AppCity> cityRegionList = new ArrayList();
      for (AppCity appCity : cityList) {
        String regionId = appCity.getRegionId();
        if (region.equals(regionId)) {
          cityRegionList.add(appCity);
        }
      }
      regions.setCities(cityRegionList);
      regionListByCities.add(regions);
    }
    return regionListByCities;
  }
}


