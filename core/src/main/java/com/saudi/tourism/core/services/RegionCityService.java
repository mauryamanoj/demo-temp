package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

/**
 * RegionCityService, is used to retrive cities and regions.
 */
public interface RegionCityService {

  /**
   * Get all cities and regions.
   * @param locale locale
   * @return All cities and regions
   */
  List<RegionCity> getAll(String locale);

  /**
   * Get all cities only.
   * @param locale locale
   * @return All cities
   */
  List<RegionCity> getCities(String locale);

  /**
   * Get all regions only.
   * @param locale locale
   * @return All regions
   */
  List<RegionCity> getRegions(String locale);

  /**
   * get by id.
   * @param id id
   * @param locale locale
   * @return city-region
   */
  RegionCity getRegionCityById(String id, String locale);

  /**
   * get by id.
   * @param id id
   * @param locale locale
   * @return city-region
   */
  RegionCityExtended getRegionCityExtById(String id, String locale);

  /**
   * Get All cities with extended props.
   *
   * @param locale locale
   * @return All cities with extended props
   */
  List<RegionCityExtended> getCitiesExt(String locale);

  /**
   * Searches the city / region with latitude / longitude coordinates provided, exact match.
   *
   * @param locale    language to get data
   * @param latitude  latitude coordinate
   * @param longitude longitude coordinate
   * @return found extended instance or {@code null} if couldn't find
   */
  RegionCityExtended searchRegionCityByCoords(String locale, String latitude, String longitude);

  /**
   * Searches all app region pages.
   *
   * @param request    request
   * @param path  path
   * @return all AppRegion under path
   */
  List<AppRegion> loadAllAppRegionsPages(SlingHttpServletRequest request, String path);
}
