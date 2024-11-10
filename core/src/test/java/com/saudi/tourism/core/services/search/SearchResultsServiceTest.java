package com.saudi.tourism.core.services.search;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.services.impl.SearchResultsServiceImpl;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class SearchResultsServiceTest {
  private static final String CONTENT_PATH =
      "/content/sauditourism/en/understand/language-and-culture";
  private QueryBuilder queryBuilder;
  private ResourceResolver resourceResolver;
  private Query mockQuery;
  private Node mockNode;
  private SearchResult mockSearchResult;
  private Long resultsOffSet = 5L;
  private SearchResult searchResult;
  private Hit mockHit;
  private Query query;
  private SearchResultsService searchResultsService;

  @BeforeEach public void setUp(AemContext context) throws RepositoryException {
    context.load().json("/components/search-results/results.json", CONTENT_PATH);
    searchResult = Mockito.mock(SearchResult.class);
    query = Mockito.mock(Query.class);
    queryBuilder = Mockito.mock(QueryBuilder.class);
    mockQuery = Mockito.mock(Query.class);
    mockHit = Mockito.mock(Hit.class);
    resourceResolver = Mockito.mock(ResourceResolver.class);
    mockSearchResult = Mockito.mock(SearchResult.class);
    mockNode = Mockito.mock(Node.class);
    when(queryBuilder.createQuery(any(), any())).thenReturn(mockQuery);
    when(mockQuery.getResult()).thenReturn(mockSearchResult);
    when(mockSearchResult.getNodes()).thenReturn(Arrays.asList(new Node[] {mockNode}).iterator());
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en");
    searchResultsService = new SearchResultsServiceImpl();
    Utils.setInternalState(searchResultsService, "queryBuilder", queryBuilder);
    when(queryBuilder.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getHits()).thenReturn(Collections.singletonList(mockHit));
    when(searchResult.getTotalMatches()).thenReturn(1L);
    when(mockHit.getResource()).thenReturn(context.currentResource(CONTENT_PATH));
  }

  @Test public void testWebSearch(AemContext context)
      throws IllegalArgumentException, RepositoryException {
    String RESOURCE_PATH = CONTENT_PATH + "/jcr:content";
    context.request().setQueryString("search=language&state=next&results=1");
    Resource res = context.currentResource(CONTENT_PATH);
    when(searchResult.getHits()).thenReturn(Collections.singletonList(mockHit));
    when(mockHit.getResource()).thenReturn(res);

    Resource pageResource = context.currentResource(RESOURCE_PATH);
    ValueMap valueMap = pageResource.adaptTo(ValueMap.class);
    assertNotNull(valueMap.get(JcrConstants.JCR_DESCRIPTION));
    assertNotNull(valueMap.get(Constants.PAGE_TITLE));
    assertNotNull(valueMap.get(JcrConstants.JCR_TITLE));
    assertNotNull(valueMap.get(Constants.FEATURE_IMAGE));

    List<SearchResultModel> searchResults = searchResultsService
        .webSearch(resourceResolver, context.currentPage(), "language", resultsOffSet,
            Boolean.TRUE);
    ;
    assertEquals(1, searchResults.size());

  }

  @Test void testAppSearch(AemContext context) throws RepositoryException {
    SearchParams searchParams = SearchParams.builder()
        .searchPaths(Arrays.asList(CONTENT_PATH))
        .nodeTypes(Arrays.asList(NameConstants.NT_PAGE))
        .searchString("language*")
        .offset(0L)
        .limit(NumberConstants.CONST_TEN)
        .fullTextSearch(true)
        .build();
    SearchListResultModel result = searchResultsService.appSearch(context.resourceResolver(),
        searchParams);
    assertEquals(1, result.getData().size());
    SearchResultModel item = result.getData().get(0);
    assertEquals("Meet Saudi", item.getPageTitle());
    assertEquals("From how to get here, to navigating the currency, climate and culture "
        + "once you’ve arrived, here’s everything you need to know about visiting Saudi Arabia.",
        item.getPageDescription());
    assertEquals("/content/sauditourism/en/understand/language-and-culture",
        item.getPagePath());
    assertEquals("/content/dam/saudi-tourism/media/understand/brand-page-hero.jpg",
        item.getFeatureImage());
    assertEquals(1, item.getTotalResults());
    assertEquals("/en/understand/language-and-culture", item.getId());
    Pagination pagination = result.getPagination();
    assertEquals(1, pagination.getTotal());
    assertEquals(0, pagination.getOffset());
    assertEquals(10, pagination.getLimit());
  }

}


