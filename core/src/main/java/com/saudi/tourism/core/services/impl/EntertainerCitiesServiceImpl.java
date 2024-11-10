package com.saudi.tourism.core.services.impl;

import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.beans.nativeapp.RegionCityEntertainer;
import com.saudi.tourism.core.login.services.SSIDFavouritesTripsConfig;
import com.saudi.tourism.core.models.app.entertainer.EntertainerCitiesPageModel;
import com.saudi.tourism.core.models.app.entertainer.EntertainerCityModel;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.EntertainerCitiesService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_OK;


/**
 * Service for Entertainer cities config.
 */
@Slf4j
@Component(service = EntertainerCitiesService.class, immediate = true)
public class EntertainerCitiesServiceImpl implements EntertainerCitiesService {

  /**
   * RT_ENTERTAINER_CITIES_PAGE.
   */
  static final String RT_ENTERTAINER_CITIES_PAGE = "sauditourism/components/structure/entertainer-cities-page";

  /**
   * Reference to RegionCityService.
   */
  @Reference
  private RegionCityService regionCityService;

  /**
   * SSIDFavouritesTripsConfigImpl config.
   */
  @Reference
  private SSIDFavouritesTripsConfig config;


  @Override
  public List<RegionCityEntertainer> getCityConfig(@NonNull final String locale,
                                                   @NonNull final ResourceResolver resolver,
                                                   final String cityOfResidence,
                                                   @NonNull final boolean activeEntertainer) {

    List<RegionCity> localizedCityList = regionCityService.getCities(locale);




    Map<String, EntertainerCityModel> childMap = Collections.emptyMap();
    List<EntertainerCityModel> entertainerCityModelList;

    EntertainerCitiesPageModel entertainerCitiesPageModel = findEntertainerCityConfigPage(resolver);
    if (Objects.isNull((entertainerCitiesPageModel))) {
      return null;
    } else {
      entertainerCityModelList = entertainerCitiesPageModel.getEntertainerCityModelList();
      if (Objects.nonNull(entertainerCityModelList)) {
        childMap = CommonUtils.iteratorToStream(entertainerCityModelList.listIterator())
            .collect(Collectors.toMap(EntertainerCityModel::getCityId, Function.identity()));
      } else {
        childMap = Collections.emptyMap();
      }
    }
    Map<String, EntertainerCityModel> finalChildMap = childMap;
    List<RegionCityEntertainer> listRegionCityEntertainer = localizedCityList.stream().map(localizedCity -> {
      final String cityId = localizedCity.getId();
      final String cityName = localizedCity.getName();
      final EntertainerCityModel child = finalChildMap.get(cityId);
      if (!Objects.isNull(child)) {
        return RegionCityEntertainer.builder().locationId(child.getLocationId()).id(cityId).name(cityName)
            .coordinates(child.getCoordinates()).disableEntertainer(child.isDisableEntertainer())
            .destinationFeatureTags(child.getLocalizedDestinationFeatureTags(locale)).image(child.getImage())
            .latitude(child.getLatitude()).longitude(child.getLongitude()).radius(child.getRadius()).build();
      } else {
        return RegionCityEntertainer.builder().id(cityId).name(cityName).build();
      }
    }).sorted(Comparator.comparing(RegionCityEntertainer::getName)).collect(Collectors.toList());


    return getFilteredCitiesList(listRegionCityEntertainer, cityOfResidence, activeEntertainer);
  }

  /**
   * Return Entertainer cities config Model.
   *
   * @param resolver resolver
   * @return EntertainerCitiesPageModel EntertainerCitiesPageModel
   */
  private EntertainerCitiesPageModel findEntertainerCityConfigPage(final ResourceResolver resolver) {

    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Resource root =
        resolver.resolve(Constants.GLOBAL_CONFIG_PATH + Constants.FORWARD_SLASH_CHARACTER + Constants.CONFIGS);

    Node configNode = root.adaptTo(Node.class);
    if (Objects.isNull(configNode)) {
      return null;
    }
    String entertainerCitiesConfigPath = searchRecursivelyPropPres(configNode);
    if (StringUtils.isBlank(entertainerCitiesConfigPath)) {
      return null;
    }

    Resource configPageContentResource = resolver.getResource(entertainerCitiesConfigPath);

    EntertainerCitiesPageModel entertainerCitiesPageModel =
        configPageContentResource.adaptTo(EntertainerCitiesPageModel.class);
    return entertainerCitiesPageModel;
  }

  /**
   * Retrieves the path of first Entertainer cities config page.
   *
   * @param node root node for the locale
   * @return path
   */
  private String searchRecursivelyPropPres(Node node) {
    String path = null;
    try {
      NodeIterator rootNode = node.getNodes();
      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty("sling:resourceType")) {
          String resourceType = currentSubNode.getProperty("sling:resourceType").getString();
          if (RT_ENTERTAINER_CITIES_PAGE.equals(resourceType)) {
            return currentSubNode.getPath();
          }
        }
        path = searchRecursivelyPropPres(currentSubNode);
        if (path != null) {
          return path;
        }
      }
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading nodes. " + rpe.getMessage());
    }
    return path;
  }
  @Override
  public JsonArray getEntertainerLocations(String locale) {

    String locationsEndpoint = config.getEntertainerLocationsEndpoint();
    Map<String, Object> queryStrings = new HashMap<>();
    queryStrings.put("language", Constants.DEFAULT_LOCALE);
    ResponseMessage responseMessage = null;
    try {
      responseMessage = RestHelper.executeMethodGetWithQueryParameters(locationsEndpoint, null, queryStrings);
    } catch (IOException e) {
      LOGGER.error("Middleware Location API error", e);
      return null;
    }


    if ((responseMessage.getStatusCode() == SC_OK || responseMessage.getStatusCode() == SC_ACCEPTED)) {
      JsonParser parser = new JsonParser();
      JsonElement json = parser.parse(responseMessage.getMessage());
      JsonObject object = json.getAsJsonObject();
      return object.get("response").getAsJsonObject().get("locations").getAsJsonArray();
    }
    return null;

  }

  /**
   * Retrieves  filtered list of cities.
   *
   * @param cityEntertainerList cityEntertainerList
   * @param cityOfResidence     cityOfResidence
   * @param activeEntertainer   activeEntertainer
   * @return filteredCityEntertainerList
   */
  private List<RegionCityEntertainer> getFilteredCitiesList(final List<RegionCityEntertainer> cityEntertainerList,
                                                            final String cityOfResidence,
                                                            final boolean activeEntertainer) {

    return cityEntertainerList.stream().filter(city -> !city.isDisableEntertainer())
        .filter(city -> StringUtils.isBlank(cityOfResidence) || (StringUtils.isNotBlank(cityOfResidence)
            && !cityOfResidence.equalsIgnoreCase(city.getId())))
        .filter(city -> (activeEntertainer && StringUtils.isNotBlank(city.getLocationId())) || !activeEntertainer)
        .collect(
            Collectors.toList());
  }
}





