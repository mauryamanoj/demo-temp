package com.saudi.tourism.core.models.components;

import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Attraction Map Filters Admin page.
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class AttractionMapFiltersSettings {

  /**
   * The whitelisted categories.
   */
  @ValueMapValue
  private List<String> whitelistedCategories;

}

