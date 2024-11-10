package com.saudi.tourism.core.services;

import java.io.IOException;
/**
 * This defines all methods to execute business of roadtrip filters.
 */
public interface RoadTripFilterService {

  /**
   * Get all roadtrip filters from API service.
   * @param locale locale.
   * @return Object roadtrip filter data from api
   * @throws IOException IOException .
   */
  Object getRoadTripFilters(String locale) throws IOException;
}
