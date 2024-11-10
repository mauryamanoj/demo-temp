package com.saudi.tourism.core.services.attractions.v1;

import lombok.NonNull;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * Attractions CF Service.
 */
public interface AttractionsCFService {

  List<Attraction> fetchAllAttractions(
      @NonNull FetchAttractionsRequest request,
      @NonNull ResourceResolver resourceResolver);

  /**
   * Get list of filtered attractions..
   *
   * @param request the query parameter
   * @return a list of attractions
   */
  FetchAttractionsResponse getFilteredAttractions(FetchAttractionsRequest request);
}
