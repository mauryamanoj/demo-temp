package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.JcrQueryUtils;
import com.saudi.tourism.core.utils.NavItemUtils;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.TWO;
import static com.saudi.tourism.core.utils.NumberConstants.THREE;
import static org.eclipse.jetty.util.URIUtil.SLASH;

/**
 * Search Results Service Implementation.
 */
@Slf4j
@Component(immediate = true,
           service = SearchResultsService.class)
public class SearchResultsServiceImpl implements SearchResultsService {

  /**
   * Simple types map.
   */
  private static final Map<String, String> SIMPLE_TYPES =
      ImmutableMap.of("events", "event", "packages", "package");

  /**
   * Inject QueryBuilder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * Find content.
   *
   * @param resourceResolver ResourceResolver
   * @param currentPage      Page
   * @param fullTextToSearch String
   * @param resultsOffSet    Long
   * @param isFullTextSearch Boolean
   * @return List<SearchResultModel>.
   */
  @Override public List<SearchResultModel> webSearch(ResourceResolver resourceResolver,
      Page currentPage, String fullTextToSearch, Long resultsOffSet, Boolean isFullTextSearch) {
    String contentRoot = StringUtils
        .join(Constants.ROOT_CONTENT_PATH, SLASH, NavItemUtils.getSiteName(currentPage.getPath()));
    String configRoot = StringUtils.join(contentRoot, SLASH, Constants.CONFIGS);

    SearchParams searchParams =
        SearchParams.builder().searchPaths(Collections.singletonList(contentRoot))
            .excludeSearchPath(configRoot)
            .nodeTypes(Collections.singletonList(NameConstants.NT_PAGE))
            .searchString(fullTextToSearch).offset(resultsOffSet)
            .limit(NumberConstants.CONST_TWENTY).fullTextSearch(isFullTextSearch).build();

    return parseSearchResult(search(resourceResolver, searchParams));
  }

  /**
   * Find content.
   *
   * @param resourceResolver resource resolver
   * @param searchParams     search parameters
   * @return search results
   */
  @Override public SearchListResultModel appSearch(final ResourceResolver resourceResolver,
      final SearchParams searchParams) {
    SearchListResultModel searchListResultModel = new SearchListResultModel();
    SearchResult searchResult = search(resourceResolver, searchParams);
    List<SearchResultModel> data = parseSearchResult(searchResult);
    searchListResultModel.setData(data);
    Pagination pagination = new Pagination();
    pagination.setTotal((int) searchResult.getTotalMatches());
    pagination.setLimit(searchParams.getLimit());
    pagination.setOffset(searchParams.getOffset().intValue());
    searchListResultModel.setPagination(pagination);
    return searchListResultModel;
  }

  /**
   * This method iterate through the search Result and return the list of SearchResultModel model.
   *
   * @param searchResult SearchResultModel
   * @return List<SearchResultModel>
   */
  private List<SearchResultModel> parseSearchResult(final SearchResult searchResult) {
    return Optional.ofNullable(searchResult.getHits()).orElse(Collections.emptyList()).stream()
        .map(hit -> this.adaptToSearchResultModel(hit, searchResult.getTotalMatches()))
        .filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * This method adapts the Hit item and return the SearchResultModel.
   *
   * @param hit          search hit
   * @param totalResults Long
   * @return SearchResultModel
   */
  private SearchResultModel adaptToSearchResultModel(final Hit hit, final Long totalResults) {
    try {
      Resource resource = hit.getResource();
      if (resource.isResourceType(NameConstants.NT_PAGE)) {
        SearchResultModel.SearchResultModelBuilder builder = SearchResultModel.builder();
        Page page = resource.adaptTo(Page.class);
        Resource contentResource = page.getContentResource();
        if (contentResource == null) {
          return null;
        }
        ValueMap valueMap = contentResource.getValueMap();
        builder.pagePath(resource.getPath());
        builder.id(resource.getPath().replaceFirst(Constants.ROOT_CONTENT_PATH, StringUtils.EMPTY));
        builder.pageDescription(valueMap.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY));
        String pageTitle = valueMap.get(Constants.PAGE_TITLE, StringUtils.EMPTY);
        builder.pageTitle(valueMap.get(JcrConstants.JCR_TITLE, pageTitle));
        builder.totalResults(totalResults);
        builder.featureImage(valueMap.get(Constants.FEATURE_IMAGE, String.class));
        String pathType = Iterables.get(PathUtils.elements(resource.getPath()), THREE);
        final String finalPathType;
        if (LocaleUtils.isAvailableLocale(new Locale(pathType))) {
          finalPathType = Iterables.get(PathUtils.elements(resource.getPath()), TWO);
        } else {
          finalPathType = pathType;
        }
        String type = Optional.ofNullable(SIMPLE_TYPES.get(pathType))
            .orElseGet(() -> getPageType(finalPathType, valueMap));
        builder.type(type);
        return builder.build();
      }
    } catch (Exception e) {
      LOGGER.error("Unable to retrieve search results for query.", e);
    }
    return null;
  }

  /**
   * Execute search.
   *
   * @param resourceResolver resource resolver
   * @param params           search parameters
   * @return search result
   */
  private SearchResult search(final ResourceResolver resourceResolver, final SearchParams params) {
    Map<String, String> queryParams = new HashMap<>();
    int groupCount = NumberConstants.CONST_ZERO;
    String[] paths = params.getSearchPaths().toArray(new String[params.getSearchPaths().size()]);
    String[] types = params.getNodeTypes().toArray(new String[params.getNodeTypes().size()]);

    JcrQueryUtils.addSearchPaths(groupCount++, queryParams, paths, true);
    JcrQueryUtils.addSearchNodeTypes(groupCount++, queryParams, types, true);
    if (params.isFullTextSearch()) {
      JcrQueryUtils.addSearchFullTextParam(groupCount++, queryParams, params.getSearchString());
    } else {
      JcrQueryUtils
          .addSearchMultipleFullTextParam(groupCount++, queryParams, params.getSearchString());
    }
    if (StringUtils.isNotEmpty(params.getExcludeSearchPath())) {
      JcrQueryUtils.addExcludePath(groupCount, queryParams, params.getExcludeSearchPath());
    }

    JcrQueryUtils.addExcludeHideInNavProperty(queryParams);
    JcrQueryUtils.addEventsDataFilter(queryParams);

    JcrQueryUtils.addResourceTypeExistsProperty(queryParams);

    return JcrQueryUtils
        .getSearchResult(resourceResolver, queryParams, params.getOffset(), params.getLimit(),
            queryBuilder);
  }

  /**
   * Detect page type.
   *
   * @param pathType path type
   * @param valueMap value map
   * @return page type
   */
  private static String getPageType(final String pathType, ValueMap valueMap) {
    String lowerPathType = pathType.toLowerCase();
    String resourceType =
        valueMap.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);
    if ("app1".equals(lowerPathType)) {
      switch (resourceType) {
        case Constants.APP_LOCATION_RESOURCE_TYPE:
          return valueMap.get(Constants.APP_LOCATION_TYPE, String.class);
        case Constants.APP_CONTENT_RESOURCE_TYPE:
          return Constants.CONTENT;
        case Constants.APP_LEGAL_PAGE_RESOURCE_TYPE:
          return "legal";
        case Constants.APP_PACKAGE_RESOURCE_TYPE:
          return "tourPackage";
        case Constants.APP_CONTACTS_PAGE_RESOURCE_TYPE:
          return "contact";
        case Constants.RT_APP_CRUISE_PAGE:
          return Constants.PV_CRUISE;
        default:
          return StringUtils.EMPTY;
      }
    }
    return lowerPathType;
  }


}
