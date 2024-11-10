package com.saudi.tourism.core.servlets.nativeapp;

import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import com.saudi.tourism.core.models.app.location.NativeAppLocationPageModel;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.saudi.tourism.core.services.SaudiTourismConfigs;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet that retrieves the list of location pages.
 * http://localhost:4502/bin/api/v2/app/locations./en
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Locations Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
          + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
          + "/bin/api/v2/app/locations"})
@Slf4j
public class LocationsServlet extends SlingAllMethodsServlet {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * city Service.
   */
  @Reference
  private transient RegionCityService citiesService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    String resourcePath = CommonUtils.getGetAppPath(request.getRequestPathInfo().getSuffix());

    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
          .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);
        List<NativeAppLocationPageModel> listOfPages = searchRecursivelyPropPres(resourceResolver, node,
            null, saudiTourismConfig, RestHelper.getParameters(request));
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(listOfPages));
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            "No information for the location pages");
      }
    } catch (Exception e) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          "No information for the location pages");
    }

  }

  /**
   * Retrieves the list of location pages.
   *
   * @param resolver resolver
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @param saudiTourismConfigs saudiTourismConfigs
   * @param queryParams queryparams
   * @return list of pages
   */
  @Generated
  public List<NativeAppLocationPageModel> searchRecursivelyPropPres(ResourceResolver resolver, Node node,
                                                                 List<NativeAppLocationPageModel> searchResults,
                                                                 SaudiTourismConfigs saudiTourismConfigs,
                                                                 Map<String, Object> queryParams) {

    if (searchResults == null) {
      searchResults = new ArrayList<>();
    }

    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        String template = null;
        if (currentSubNode.hasProperty(NN_TEMPLATE)) {
          template = currentSubNode.getProperty(NN_TEMPLATE).getString();
        }
        if (Constants.APP_LOCATION_TEMPLATE.equals(template)) {
          final Resource subResource = resolver.getResource(currentSubNode.getPath());
          if (null != subResource) {
            final NativeAppLocationPageModel model = subResource.adaptTo(NativeAppLocationPageModel.class);
            final String lang = CommonUtils.getLanguageForPath(model.getPath());
            Resource enResource = resolver.getResource(model.getPath().replace(lang, "en")
                + "/" + "jcr:content");
            if (null != enResource) {
              model.setRegionId(AppUtils.stringToID(enResource.getValueMap()
                  .get("region", String.class)));
              if (null != model.getRegionId() && null == model.getRegion()) {
                RegionCity regObj = citiesService.getRegionCityById(model.getRegionId(), lang);
                if (regObj != null) {
                  model.setRegion(regObj.getName());
                }
              }
            }
            String[] excludedPaths = saudiTourismConfigs.getExcludingPaths();
            if (null != excludedPaths && excludedPaths.length >= 1) {
              boolean result = Arrays.stream(excludedPaths).anyMatch(model.getPath()::equals);
              if (!result && isFilterLocationPage(model, queryParams)) {
                searchResults.add(model);
              }
            } else {
              if (isFilterLocationPage(model, queryParams)) {
                searchResults.add(model);
              }
            }
          }
        }
        searchRecursivelyPropPres(resolver, currentSubNode, searchResults, saudiTourismConfigs,
            queryParams);

      }
      return searchResults;
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the locations for the app. " + rpe.getMessage());
    }
    return searchResults;
  }

  /**
   * returns if location page is to be filtered.
   *
   * @param model model.
   * @param queryMap queryMap.
   * @return is Filter location page.
   */
  private boolean isFilterLocationPage(NativeAppLocationPageModel model, Map<String, Object> queryMap) {
    boolean isCityFilter = true, isRegionFilter = true, isTypeFilter = true, isCategoryFilter = true, isFavId = true;
    if (queryMap.containsKey("city")) {
      isCityFilter = isFilter(queryMap.get("city"), isCityFilter, model.getCityId());
    }
    if (queryMap.containsKey("region")) {
      isRegionFilter = isFilter(queryMap.get("region"), isRegionFilter, model.getRegionId());
    }
    if (queryMap.containsKey("type")) {
      isTypeFilter = isFilter(queryMap.get("type"), isTypeFilter, model.getType());
    }
    if (queryMap.containsKey("category") && null != model.getTags()) {
      isCategoryFilter = isFilterFromList(queryMap.get("category"), Arrays.asList(model.getTags()));
    }
    if (queryMap.containsKey("favId")) {
      isFavId = isFilterFromList(queryMap.get("favId"), Arrays.asList(model.getWebMappingPath()));
    }
    return isCityFilter && isRegionFilter && isTypeFilter && isCategoryFilter && isFavId;
  }

  /**
   * Returns if filter.
   *
   * @param filterValues filter values.
   * @param filter filter.
   * @param matchValue match value.
   * @return is Filter.
   */
  private boolean isFilter(Object filterValues,
                           boolean filter, String matchValue) {
    if (null != filterValues) {
      List<String> cityList = Arrays.asList(filterValues.toString().split(","));
      if (!cityList.contains(matchValue)) {
        filter = false;
      }
    }
    return filter;
  }

  /**
   * Returns if filter.
   *
   * @param filterValues filter values.
   * @param matchValues match values.
   * @return is Filter List.
   */
  private boolean isFilterFromList(Object filterValues,
                            List<String> matchValues) {
    final boolean[] isFilter = new boolean[1];
    if (null != filterValues) {
      List<String> filterList = Arrays.asList(filterValues.toString().split(","));
      filterList.forEach(filterVal -> {
        if (matchValues.contains(filterVal)) {
          isFilter[0] = true;
        }
      });
    } else {
      isFilter[0] = true;
    }
    return isFilter[0];
  }

}
