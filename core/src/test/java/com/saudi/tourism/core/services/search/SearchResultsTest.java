package com.saudi.tourism.core.services.search;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.models.common.TextModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.models.components.search.SearchResults;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.services.impl.SearchResultsServiceImpl;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class SearchResultsTest {
  private static final String CONTENT_PATH_SEARCH =
      "/content/sauditourism/en/understand/language-and-culture";
  private static final String CONTENT_PATH = "/content/sauditourism/en/test-search";

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
    context.load().json("/components/search-results/content.json", CONTENT_PATH);
    context.load().json("/components/search-results/results.json", CONTENT_PATH_SEARCH);
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
    Mockito.when(queryBuilder.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);
    Mockito.when(query.getResult()).thenReturn(searchResult);
  }

  @Test
  public void testSearchCompParamConfigs(AemContext context) throws RepositoryException {
    String RESOURCE_PATH = "/jcr:content/root/responsivegrid/search_results";
    context.currentResource(CONTENT_PATH + RESOURCE_PATH);

    List<SearchResultModel> listOfSearchResults = new ArrayList<>();
    listOfSearchResults.add(new SearchResultModel(PN_TITLE, "descr", "/content", "", 1L, "", "", new ArrayList<>()));
    SearchResultsService searchService = mock(SearchResultsService.class);

    when(searchService.webSearch(any(), any(),
        any(), any(), eq(true))).thenReturn(new ArrayList<>());

    when(searchService.webSearch(any(), any(),
        any(), any(), eq(false))).thenReturn(listOfSearchResults);

    context.registerService(SearchResultsService.class, searchService);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("search", "london");
    parameters.put("state", "next");
    parameters.put("results", 0);
    context.request().setParameterMap(parameters);

    SearchResults searchResults = context.request().adaptTo(SearchResults.class);
    Assertions.assertNotNull(searchResults.getNoResults());
    Assertions.assertNotNull(searchResults.getLearnMore());
    Assertions.assertNotNull(searchResults.getLinkPrevious());
    Assertions.assertNotNull(searchResults.getLinkNext());

    TextModel txtModelNoResults = searchResults.getNoResults();
    Assertions.assertNotNull(txtModelNoResults.getText());
    Assertions.assertNotNull(txtModelNoResults.getDescription());

    TextModel txtLearnMore = searchResults.getLearnMore();
    Assertions.assertNotNull(txtLearnMore.getText());

    TextModel txtLinkPrevious = searchResults.getLinkPrevious();
    Assertions.assertNotNull(txtLinkPrevious.getText());

    TextModel txtLinkNext = searchResults.getLinkNext();
    Assertions.assertNotNull(txtLinkNext.getText());
  }

  @Test
  public void testConstructSearchRequest(AemContext context) {
    String[] arrSearchText = {"understand"};
    String[] arrState = {"next"};
    String[] arrResults = {"4"};
    Map<String, String[]> requestParams = new HashMap<>();
    requestParams.put("search", arrSearchText);
    requestParams.put("state", arrState);
    requestParams.put("results", arrResults);
    SearchResults searchResults = new SearchResults();
    String fullSearchedText = StringUtils.EMPTY;
    fullSearchedText = searchResults.constructSearchRequest(requestParams, fullSearchedText);
    Assertions.assertNotNull(fullSearchedText);
    Assertions.assertNotNull(arrState);
    Assertions.assertNotNull(arrResults);
    // Scenario 1
    arrState[0] = "previous";
    arrResults[0] = "-1";
    requestParams.put("state", arrState);
    requestParams.put("results", arrResults);
    fullSearchedText = searchResults.constructSearchRequest(requestParams, fullSearchedText);
    Assertions.assertEquals(String.valueOf(5L), String.valueOf(searchResults.getCurResultsPage()));
    Assertions.assertEquals(String.valueOf(40L), String.valueOf(searchResults.getResultsOffSet()));

    // Scenario 2
    arrState[0] = "next";
    arrResults[0] = "1";
    requestParams.put("state", arrState);
    requestParams.put("results", arrResults);
    fullSearchedText = searchResults.constructSearchRequest(requestParams, fullSearchedText);
    Assertions.assertEquals(String.valueOf(2L), String.valueOf(searchResults.getCurResultsPage()));
    Assertions.assertEquals(String.valueOf(10L), String.valueOf(searchResults.getResultsOffSet()));

    // Scenario 3
    arrState[0] = "previous";
    arrResults[0] = "3";
    requestParams.put("state", arrState);
    requestParams.put("results", arrResults);
    fullSearchedText = searchResults.constructSearchRequest(requestParams, fullSearchedText);
    Assertions.assertEquals(String.valueOf(2L), String.valueOf(searchResults.getCurResultsPage()));
    Assertions.assertEquals(String.valueOf(-30L), String.valueOf(searchResults.getResultsOffSet()));
  }


  @Test
  public void testFindSearchResults(AemContext context)
      throws IllegalArgumentException, RepositoryException {
    String RESOURCE_PATH = CONTENT_PATH_SEARCH + "/jcr:content";
    SearchResultModel searchResultModel =
        context.currentResource(RESOURCE_PATH).adaptTo(SearchResultModel.class);
    SearchResults searchResults = new SearchResults();

    context.request().setQueryString("search=language&state=next&results=1");
    Resource res = context.currentResource(CONTENT_PATH_SEARCH);
    when(searchResult.getHits()).thenReturn(Collections.singletonList(mockHit));
    when(mockHit.getResource()).thenReturn(res);
    List<SearchResultModel> listOfSearchResults = searchResultsService
        .webSearch(resourceResolver, context.currentPage(), "language", resultsOffSet,
            Boolean.TRUE);
    Assertions.assertEquals(1, listOfSearchResults.size());
    if (listOfSearchResults.size() != 0) {
      searchResults.populateSearchSections(listOfSearchResults);
      Assertions
          .assertEquals(String.valueOf(1L), String.valueOf(searchResults.getMainResults().size()));
      Assertions.assertEquals(String.valueOf(0L),
          String.valueOf(searchResults.getSecondaryResults().size()));
    }

    Long totalSearchResults = 154L;
    double noOfSearchPage;
    Long roundValueNoOfPages = NumberConstants.CONST_ZERO_L;

    Double valueBeforeRoundUp = totalSearchResults / NumberConstants.CONST_TEN_D;
    noOfSearchPage = Math.round(totalSearchResults / NumberConstants.CONST_TEN_D);
    roundValueNoOfPages = Math.round(noOfSearchPage);
    if (valueBeforeRoundUp > roundValueNoOfPages) {
      roundValueNoOfPages = roundValueNoOfPages + NumberConstants.CONST_ONE;
    }

    Assertions.assertEquals(String.valueOf(roundValueNoOfPages), String.valueOf(16L));
  }

}


