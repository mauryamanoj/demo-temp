package com.saudi.tourism.core.services;

import com.google.gson.JsonArray;
import com.saudi.tourism.core.beans.nativeapp.RegionCityEntertainer;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * The service EntertainerCitiesService.
 */
public interface EntertainerCitiesService {

  /**
   * Return Entertainer cities config.
   *
   * @param locale   locale
   * @param resolver   resolver
   * @param cityOfResidence   cityOfResidence
   * @param activeEntertainer   activeEntertainer
   * @return RegionCityEntertainer RegionCityEntertainer
   */
  List<RegionCityEntertainer> getCityConfig(String locale, ResourceResolver resolver, String cityOfResidence,
                                            boolean activeEntertainer);


  /**
   * Return Entertainer locations .
   *
   * @param locale   locale
   * @return JsonElement JsonElement
   */
  JsonArray getEntertainerLocations(String locale);

}
