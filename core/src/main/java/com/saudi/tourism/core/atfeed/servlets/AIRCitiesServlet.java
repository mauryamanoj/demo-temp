package com.saudi.tourism.core.atfeed.servlets;

import static com.saudi.tourism.core.utils.Constants.CITIES_PATH_FORMAT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.atfeed.utils.AIRCitiesExporter;
import com.saudi.tourism.core.models.app.location.AppCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.components.bigslider.v1.BigSlide;
import com.saudi.tourism.core.models.components.bigslider.v1.BigSliderModel;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;

/**
 * This is used to get all experiences from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/air/v1/cities . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience Package Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/cities" })
@Slf4j

public class AIRCitiesServlet extends AbstractATFeedServlet {
  private static final long serialVersionUID = 1L;
  /** Nested folder path. */
  private static final String NESTED_FOLDER_PATH = "see-do/destinations" + Constants.FORWARD_SLASH_CHARACTER
        + JcrConstants.JCR_CONTENT;
  /**Discover regions category. */
  private static final String REGIONS_CATEGORY = "carousel";
  /**DESTINATIONS. */
  private static final String DESTINATIONS = "destinations-";

  /**
  * The RoadTrip Data service.
  */
  @Reference
  private transient RegionCityService service;

  /**
   * The Activities Service.
   */
  @Reference
  private GeneralQueryService generalQueryService;

  /**
   * The Query builder.
   */
  @Reference
  private GeneralQueryService queryBuilder;
  /**
  * The Resource Resolver factory.
  */
  @Reference
  private transient ResourceResolverFactory resolverFactory;
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String locale = generalQueryService.getLocale(request);
      String searchLocation = getSearchLocation(locale, NESTED_FOLDER_PATH);
      SearchResult searchResult = generalQueryService.findDiscover(request, searchLocation);
      Iterator<Resource> regionResources = searchResult.getResources();
      Map<String, Entity> discoverRegionEntities = new HashMap<String, Entity>();
      while (regionResources.hasNext()) {
        BigSliderModel regionModel = regionResources.next().adaptTo(BigSliderModel.class);
        Iterator<BigSlide> slideIterator = regionModel.getSlides().iterator();
        while (slideIterator.hasNext()) {
          BigSlide slideModel = slideIterator.next();
          Entity entity = new Entity();
          if (null != slideModel.getLink().getUrl()) {
            String cityName = REGIONS_CATEGORY
                    + slideModel.getLink().getUrl().replaceAll(Constants.FORWARD_SLASH_CHARACTER, AIRConstants.DASH);
            entity.setId(cityName.substring(cityName.indexOf(DESTINATIONS) + DESTINATIONS.length(), cityName.length()));
          } else {
            entity.setId(ComponentUtils.generateId(REGIONS_CATEGORY, slideModel.getTitle()));
          }
          entity.setName(slideModel.getTitle());
          entity.setMessage(slideModel.getDescription().toString());
          entity.setThumbnailUrl(setHost(slideModel.getImage().getFileReference(), getHost(request)));
          entity.setThumbnailS7Url(setHost(slideModel.getImage().getS7fileReference(), getHost(request)));
          entity.setCtaURL(slideModel.getLink().getUrl());
          discoverRegionEntities.put(entity.getId(), entity);
        }
      }

      List<RegionCityExtended> filteredCities = new ArrayList<RegionCityExtended>();
      List<RegionCityExtended> citiesExt = service.getCitiesExt(locale);
      Resource resource = CommonUtils.resolveResource(resolverFactory.getThreadResourceResolver(),
           String.format(AIRConstants.SAUDITOURISM_CITIES_PATH, locale));
      if (resource.hasChildren()) {
        RegionCityExtended regionCity;
        for (Resource currentResource : resource.getChildren()) {
          ValueMap map = currentResource.getValueMap();
          final String currentCity = currentResource.getValueMap().get(AIRConstants.CITY_ID).toString();
          Optional<RegionCityExtended> region = citiesExt.stream().filter(city ->
              StringUtils.equalsIgnoreCase(city.getId(), currentCity)).findFirst();
          regionCity = region.get();
          regionCity.setLinkCityUrl(map.get(AIRConstants.LINK_URL).toString()
               .replaceAll(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
          filteredCities.add(regionCity);
          LOGGER.info("Currently iterating through cities" + resource.getName());
        }
      }
      String cityPath = String.format(CITIES_PATH_FORMAT, locale);
      Map<String, AppCity> citiesInfo = generalQueryService.getCities(resolverFactory.getThreadResourceResolver(),
              cityPath);
      AIRCitiesExporter.writeCSV(response, StatusEnum.SUCCESS.getValue(), filteredCities, citiesInfo,
          request, discoverRegionEntities);
      LOGGER.debug("Success :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET, response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", AIRConstants.EXPERIENCES_PACKAGE_SERVLET,
          e.getLocalizedMessage());
    }
  }

}
