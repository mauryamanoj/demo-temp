package com.saudi.tourism.core.servlets.app;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_MINUTES_IN_HOUR;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS;
import static com.saudi.tourism.core.utils.Constants.NUMBER_OF_SECONDS_IN_MINUTE;
import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;
import static com.saudi.tourism.core.utils.Constants.RT_APP_REGION_PAGE;

/**
 * Servlet for get regions.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Regions Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/regions"})
@Slf4j
public class AppRegionServlet extends SlingSafeMethodsServlet {
  /**
   * The cache for Trip Plans requests.
   */
  @Reference
  private transient Cache memCache;
  /**
   * Memory cache key.
   */
  public static final String MEM_CACHE_KEY_PREFIX = "app-region-dic:";
  /**
   * Path to regions path.
   */
  private static final String REGION_PATH_FORMAT = ROOT_APP_CONTENT_PATH + "/%s/regions";

  /**
   * Get regions.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    String locale = CommonUtils.getLocale(request);
    String memKey = MEM_CACHE_KEY_PREFIX + locale;
    String path = String.format(REGION_PATH_FORMAT, locale);

    List<AppRegion> list = (List<AppRegion>) memCache.computeIfAbsent(memKey,
        k -> loadRegions(request.getResourceResolver(), path),
        (long) NUMBER_OF_SECONDS * NUMBER_OF_MINUTES_IN_HOUR * NUMBER_OF_SECONDS_IN_MINUTE);
    CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
        RestHelper.getObjectMapper().writeValueAsString(list));
  }

  /**
   * Load regions.
   * @param resolver resolver
   * @param path path
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
}
