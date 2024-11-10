package com.saudi.tourism.core.services.filters.v1;

import java.util.List;

import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.models.components.FiltersSettings;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Filters Service.
 */
public interface FiltersService {

  /**
   * Get filters from result and thingsToDoFiltersSettings.
   *
   * @param resourceResolver
   * @param result
   * @param filtersSettings
   * @param locale
   * @param types
   * @return ThingsToDoFilterModel
   */
  FilterModel getFiltersFromResult(ResourceResolver resourceResolver,
                                   SearchResult result,
                                   List<FiltersSettings> filtersSettings,
                                   String locale,
                                   List<String> types);
}
