package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.beans.SearchConfigResponse;
import com.saudi.tourism.core.services.SearchConfigService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.SearchUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Contact Service.
 */
@Component(service = SearchConfigService.class)
public class SearchConfigServiceImpl implements SearchConfigService {

  /**
   * The service to provide read-only resource resolver.
   */
  @Reference
  private UserService resolverProvider;

  @Override
  public SearchConfigResponse getConfig(final String locale, String source) {

    try (ResourceResolver resourceResolver = resolverProvider.getResourceResolver()) {
      return SearchConfigResponse.builder()
        .trendingBlock(SearchUtils.getSearchTrendingsWithSource(null, resourceResolver,
          locale, source))
        .pillBlock(SearchUtils.getSearchPills(null,
          resourceResolver, locale))
        .searchCategories(SearchUtils.getSearchCategoriesFromTags(resourceResolver, locale))
        .build();
    }
  }

}
