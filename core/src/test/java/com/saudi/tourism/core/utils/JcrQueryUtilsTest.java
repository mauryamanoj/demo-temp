package com.saudi.tourism.core.utils;

import com.adobe.cq.social.group.api.GroupConstants;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.services.impl.SearchResultsServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.BOOL_PROPERTY;
import static org.eclipse.jetty.util.URIUtil.SLASH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class JcrQueryUtilsTest {


  private static final String CONTENT_PATH =
      "/content/sauditourism/en/understand/language-and-culture";
  private QueryBuilder queryBuilder;
  private ResourceResolver resourceResolver;
  private Query mockQuery;
  private Node mockNode;
  private SearchResult mockSearchResult;
  private SlingSettingsService settingsService;
  private SearchResult searchResult;
  private Hit mockHit;
  private Query query;
  private SearchResultsService searchResultsService;
  private Session session;

  @BeforeEach public void setUp(AemContext context) throws RepositoryException {
    context.load().json("/components/search-results/results.json", CONTENT_PATH);
    resourceResolver = Mockito.mock(ResourceResolver.class);
    session = Mockito.mock(Session.class);
    queryBuilder = Mockito.mock(QueryBuilder.class);
    query = Mockito.mock(Query.class);
    settingsService = Mockito.mock(SlingSettingsService.class);
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

  @Test public void addExcludeHideInNavProperty() {
    Map<String, String> queryParams = new HashMap<>();
    JcrQueryUtils.addExcludeHideInNavProperty(queryParams);
    Assertions.assertNotNull(queryParams.get(BOOL_PROPERTY));
  }

  @Test public void addSearchFullTextParam() {
    Map<String, String> queryParams = new HashMap<>();
    String[] fulltextRelPaths = new String[] {"jcr:content/data"};
    JcrQueryUtils.addSearchFullTextParam(1, queryParams, "test");
    Assertions.assertEquals(2, queryParams.size());
  }

  @Test public void addSearchFullTextParamNullQueryParam() {
    Map<String, String> queryParams = null;
    String[] fulltextRelPaths = new String[] {"jcr:content/data"};
    JcrQueryUtils.addSearchFullTextParam(1, queryParams, "test");
    Assertions.assertNull(queryParams);
  }

  @Test public void addSearchNodeTypes() {
    Map<String, String> queryParams = new HashMap<>();
    String[] types = new String[] {NameConstants.NT_PAGE};
    JcrQueryUtils.addSearchNodeTypes(1, queryParams, types, true);
    Assertions.assertEquals(2, queryParams.size());
  }

  @Test public void addSearchNodeTypesEmpty() {
    Map<String, String> queryParams = new HashMap<>();
    String[] types = new String[] {};
    JcrQueryUtils.addSearchNodeTypes(1, queryParams, types, true);
    Assertions.assertEquals(0, queryParams.size());
  }

  @Test public void addExcludePath() {
    Map<String, String> queryParams = new HashMap<>();
    String contentRoot = StringUtils.join(Constants.ROOT_CONTENT_PATH, SLASH, "en");
    String configRoot = StringUtils.join(contentRoot, SLASH, Constants.CONFIGS);

    JcrQueryUtils.addExcludePath(1, queryParams, configRoot);
    Assertions.assertEquals(3, queryParams.size());
  }

  @Test public void getSearchResult() {
    Map<String, String> queryParams = new HashMap<>();
    PredicateGroup predicates = PredicateConverter.createPredicates(queryParams);
    Mockito.when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
    Mockito.when(queryBuilder.createQuery(predicates, session)).thenReturn(query);
    Mockito.when(query.getResult()).thenReturn(searchResult);

    SearchResult result =
        JcrQueryUtils.getSearchResult(resourceResolver, queryParams, 0, 5, queryBuilder);
    Assertions.assertNotNull(result);
  }

  @Test public void getSearchResultLimitTest() {
    Map<String, String> queryParams = new HashMap<>();
    PredicateGroup predicates = PredicateConverter.createPredicates(queryParams);
    Mockito.when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
    Mockito.when(queryBuilder.createQuery(predicates, session)).thenReturn(query);
    Mockito.when(query.getResult()).thenReturn(searchResult);

    SearchResult result =
        JcrQueryUtils.getSearchResult(resourceResolver, queryParams, 10, 100, queryBuilder);
    Assertions.assertNotNull(result);
  }

  @Test public void getSearchResultNullQueryBuilder() {
    Map<String, String> queryParams = new HashMap<>();
    SearchResult result =
        JcrQueryUtils.getSearchResult(resourceResolver, queryParams, 10, 100, null);
    Assertions.assertNull(result);
  }

  @Test public void addSearchMultipleFullTextParam() {
    Map<String, String> queryParams = new HashMap<>();
    JcrQueryUtils.addSearchMultipleFullTextParam(1, queryParams, "search query");
    Assertions.assertEquals(3, queryParams.size());
  }

  @Test
  void getQueryMapToSearchPages() {
    final String path = "any-path";
    final String resourceType = "any-resource-type";

    final Map<String, String> map = JcrQueryUtils.getQueryMapToSearchPages(path, resourceType);
    assertEquals(path, map.get(Constants.PATH_PROPERTY));
    assertEquals(Constants.TYPE_PAGE_CONTENT, map.get(GroupConstants.PROPERTY_TYPE));
    assertEquals(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, map.get("1_property"));
    assertEquals(resourceType, map.get("1_property.value"));
  }
}
