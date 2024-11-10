package com.saudi.tourism.core.atfeed.servlets;

import static com.saudi.tourism.core.utils.AIRConstants.REGION_PATH_FORMAT;
import static com.saudi.tourism.core.utils.Constants.RT_APP_REGION_PAGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.atfeed.utils.AIRExploreRegionExporter;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * This is used to get all experiences from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/air/v1/exploreregion . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience Package Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/exploreregion" })
@Slf4j

public class AIRExploreRegionServlet extends SlingSafeMethodsServlet {
  private static final long serialVersionUID = 1L;
      /**
      * The RoadTrip Data service.
      */
  @Reference
  private transient RegionCityService service;

  /**
  * The Resource Resolver factory.
  */
  @Reference
  private transient ResourceResolverFactory resolverFactory;
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String locale = Constants.DEFAULT_LOCALE;
      if (null != request.getRequestParameter(Constants.LOCALE)
           && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
        locale = request.getRequestParameter(Constants.LOCALE).getString();
      }
      List<RegionCity> filteredCities = new ArrayList<RegionCity>();
      List<RegionCity> regionDetails = service.getRegions(locale);
      Resource resource = CommonUtils.resolveResource(resolverFactory.getThreadResourceResolver(),
                String.format(AIRConstants.SAUDITOURISM_REGION_PATH, locale));
      if (resource.hasChildren()) {
        RegionCity regionCity;
        for (Resource currentResource : resource.getChildren()) {
          ValueMap map = currentResource.getValueMap();
          if (map.containsKey(AIRConstants.REGION_ID)) {
            final String currentRegion = map.get(AIRConstants.REGION_ID).toString();
            Optional<RegionCity> region = regionDetails.stream().filter(provinces ->
                StringUtils.equalsIgnoreCase(provinces.getId(), currentRegion)).findFirst();
            regionCity = new RegionCityExtended(region.get());
            regionCity.setDescription(StringEscapeUtils.escapeCsv(map
                     .get(AIRConstants.DESCRIPTION).toString()));
            regionCity.setCtaType(map.get(AIRConstants.CTA_TYPE).toString());
            regionCity.setHideFav(map.get(AIRConstants.HIDE_FAV).toString());
            regionCity.setHighlight(map.get(AIRConstants.HIGH_LIGHT).toString());
            regionCity.setIcon(map.get(AIRConstants.ICON).toString());
            regionCity.setImagePosition(map.get(AIRConstants.IMAGE_POSITION).toString());
            regionCity.setLinkUrl(map.get(AIRConstants.LINK_URL).toString()
                 .replaceAll(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
            regionCity.setImageDesktopLink(map.get(AIRConstants.IMAGE_FILE_REFERENCE).toString());
            regionCity.setImageMobileLink(map.get(AIRConstants.IMAGE_MOBILE_REFERENCE).toString());
            regionCity.setLinkCopy(map.get(AIRConstants.COPY_LINK).toString());
            regionCity.setIsFeatured(map.get(AIRConstants.ISFEATURED).toString());
            regionCity.setType(map.get(AIRConstants.CITY_TYPE).toString());
            regionCity.setUseActivityLink(map.get(AIRConstants.USE_ACTIVITY_LINK).toString());
            filteredCities.add(regionCity);
            LOGGER.info("Currently iterating through region" + resource.getName());
          }
        }
      }
      String regionPath = String.format(REGION_PATH_FORMAT, locale);
      Map<String, AppRegion> regionInfo = getRegions(resolverFactory.getThreadResourceResolver(), regionPath);
      AIRExploreRegionExporter.writeCSV(response, StatusEnum.SUCCESS.getValue(), filteredCities,
            regionInfo, request);
      LOGGER.debug("Success :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET, response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET,
          e.getLocalizedMessage());
    }
  }

 /**
  * Load regions.
  *
  * @param resolver resolver
  * @param path     path
  * @return list of regions
  */
  private Map<String, AppRegion> getRegions(ResourceResolver resolver, String path) {
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page root = pageManager.getPage(path);
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    Map<String, AppRegion> regions = new HashMap<>();
    for (@NotNull Iterator<Page> it = root.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_REGION_PAGE)) {
        AppRegion appRegion = page.getContentResource().adaptTo(AppRegion.class);
        regions.put(appRegion.getId().split(AIRConstants.SLASH)[appRegion.getId()
            .split(AIRConstants.SLASH).length - 1].replace("province", AIRConstants.REGION), appRegion);
      }
    }
    return regions;
  }

}
