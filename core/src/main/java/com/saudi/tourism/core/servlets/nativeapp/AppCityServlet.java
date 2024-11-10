package com.saudi.tourism.core.servlets.nativeapp;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.app.location.AppCity;
import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.CITIES_PATH_FORMAT;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_MINUTES_IN_HOUR;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS_IN_MINUTE;
import static com.saudi.tourism.core.utils.Constants.RT_APP_CITY_PAGE;

/**
 * Servlet for get cities.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Cities Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/app/cities" })
@Slf4j
public class AppCityServlet extends SlingSafeMethodsServlet {
  /**
   * The cache.
   */
  @Reference
  private transient Cache memCache;

  /**
   * The cache.
   */
  @Reference
  private transient PackageService packageService;

  /**
   * Memory cache key.
   */
  public static final String MEM_CACHE_KEY_PREFIX = "app-city-dic:";

  /** The Constant statusCode. */
  static final int STATUS_CODE = 404;

  /**
   * Get cities.
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException servlet exception
   * @throws IOException      io exception
   */
  @Override
  protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    String city = request.getParameter("city");
    String locale = CommonUtils.getLocale(request);
    String memKey = MEM_CACHE_KEY_PREFIX + locale;
    String path = String.format(CITIES_PATH_FORMAT, locale);

    List<AppCity> list = (List<AppCity>) memCache.computeIfAbsent(memKey,
        k -> loadCities(request.getResourceResolver(), path),
        (long) NUMBER_OF_SECONDS * NUMBER_OF_MINUTES_IN_HOUR * NUMBER_OF_SECONDS_IN_MINUTE);
    fillCityPackages(locale, list, packageService);
    if (city != null) {
      int flag = 0;
      for (AppCity appCity : list) {
        if (appCity.getCity().equals(city)) {
          flag = 1;
          CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(appCity));
        }
      }
      if (flag == 0) {
        response.setStatus(StatusEnum.BAD_REQUEST.getValue());
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", STATUS_CODE);
        jsonObject.addProperty("message", "Not found");
        response.getWriter().write(gson.toJson(jsonObject));
      }
    } else {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(list));
    }
  }

  /**
   * Adds packages to city.
   * @param locale         locale
   * @param list           of city
   * @param packageService packageService
   */
  public static void fillCityPackages(String locale, List<AppCity> list,
      final PackageService packageService) {
    try {
      for (AppCity appCity : list) {
        PackagesRequestParams requestParams = new PackagesRequestParams();
        requestParams.setLocale(locale);
        requestParams.setCity(appCity.getCity());
        requestParams.setLimit(NumberConstants.CONST_200);
        PackagesListModel packagesListModel = packageService.getFilteredPackages(requestParams);
        List<PackageDetail> packages = packagesListModel.getData();
        if (packages != null) {
          appCity
              .setPackages(packages
                  .stream().map(
                      PackageDetail::getPath)
                  .map(
                      packagePath -> StringUtils
                          .substring(packagePath,
                              StringUtils.ordinalIndexOf(packagePath,
                                  Constants.FORWARD_SLASH_CHARACTER, 2) + 1))
                  .collect(Collectors.toList()));
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * Load regions.
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
    return list;
  }
}
