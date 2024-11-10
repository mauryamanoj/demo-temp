package com.saudi.tourism.core.services;

import java.io.IOException;
/**
 * This defines all methods to execute business of experience filters.
 */
public interface ExperienceFilterService {

  /**
   * Get all experiences from API service.
   * @param locale locale.
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getPackageFilters(String locale) throws IOException;
}
