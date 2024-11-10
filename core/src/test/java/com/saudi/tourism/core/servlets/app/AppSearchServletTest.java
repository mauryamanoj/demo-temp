package com.saudi.tourism.core.servlets.app;

import com.day.cq.wcm.api.NameConstants;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static com.saudi.tourism.core.servlets.app.AppSearchServlet.LOCALE_TEMPLATE;
import static com.saudi.tourism.core.utils.Constants.DEFAULT_LOCALE;
import static com.saudi.tourism.core.utils.Constants.LOCALE;
import static com.saudi.tourism.core.utils.Constants.QUERY_PARTIAL_TEXT_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) class AppSearchServletTest {

  public static final String PAGE_TITLE = "Page Title";
  private static final String SEARCH_PATH = "/content/sauditourism/" + LOCALE_TEMPLATE + "/app1";

  private static final String SEARCH_STRING = "string";
  public static final String ID = "/en/app1/node1/node2";
  public static final String PAGE_PATH = "/content/sauditourism/en/app1/node1/node2";
  public static final String PAGE_DESCRIPTION = "Page Description";
  public static final String PATH_TO_IMAGE = "/path/to/image";

  private SearchResultsService searchResultsService;

  private AppSearchServlet appSearchServlet;

  @BeforeEach
  public void setUp(AemContext context) {
    searchResultsService = mock(SearchResultsService.class);
    appSearchServlet = new AppSearchServlet();
    context.registerService(SearchResultsService.class, searchResultsService);
  }

  private void registerServlet(final AemContext context, Map<String, Object> parameters) {
    context.registerInjectActivateService(appSearchServlet, parameters);
  }

  @Test
  @DisplayName("Test servlet when no search paths are not defined")
  void testServletWhenPathsNotDefined(AemContext context)
      throws ServletException, IOException {
    registerServlet(context, Collections.emptyMap());
    Map<String, Object> map = ImmutableMap.of(
        LOCALE, DEFAULT_LOCALE,
        Constants.SEARCH_QUERY, SEARCH_STRING);
    context.request().setParameterMap(map);
    appSearchServlet.doGet(context.request(), context.response());
    assertEquals("{\"status\":\"error\",\"statusCode\":null,\"message\":\"Search paths not defined\",\"id\":null}",
        context.response().getOutputAsString());
    assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, context.response().getStatus());
  }

  @Test
  @DisplayName("Test servlet when locale is not set in request")
  void testServletWhenNoLocale(AemContext context) throws ServletException, IOException {
    registerServlet(context, Collections.emptyMap());
    appSearchServlet.doGet(context.request(), context.response());
    assertEquals("{\"status\":\"error\",\"statusCode\":null,\"message\":\"Mandatory locale "
        + "request parameter is missing.\",\"id\":null}", context.response().getOutputAsString());
    assertEquals(HttpStatus.SC_BAD_REQUEST, context.response().getStatus());
  }

  @Test
  @DisplayName("Test servlet when search string is not set in request")
  void testServletWhenNoSearchString(AemContext context) throws ServletException, IOException {
    registerServlet(context, Collections.emptyMap());
    Map<String, Object> map = ImmutableMap.of(LOCALE, DEFAULT_LOCALE);
    context.request().setParameterMap(map);
    appSearchServlet.doGet(context.request(), context.response());
    assertEquals("{\"status\":\"error\",\"statusCode\":null,\"message\":\"Mandatory q "
        + "request parameter is missing.\",\"id\":null}", context.response().getOutputAsString());
    assertEquals(HttpStatus.SC_BAD_REQUEST, context.response().getStatus());
  }

  @Test
  @DisplayName("Test servlet when all mandatory parameters are set")
  void testServletWithParameters(AemContext context) throws ServletException, IOException {
    final String exactPath = SEARCH_PATH.replace(LOCALE_TEMPLATE, DEFAULT_LOCALE);
    registerServlet(context, Collections.singletonMap("includeSearchPaths",
        new String[]{SEARCH_PATH}));
    Map<String, Object> map = ImmutableMap.of(
        LOCALE, DEFAULT_LOCALE,
        Constants.SEARCH_QUERY, SEARCH_STRING);
    context.request().setParameterMap(map);
    SearchParams searchParams = SearchParams.builder()
        .searchPaths(Arrays.asList(exactPath))
        .nodeTypes(Arrays.asList(NameConstants.NT_PAGE))
        .searchString(SEARCH_STRING + QUERY_PARTIAL_TEXT_CHARACTER)
        .offset(0L)
        .limit(NumberConstants.CONST_TEN)
        .fullTextSearch(true)
        .build();

    SearchResultModel searchNodes = SearchResultModel.builder()
        .id(ID)
        .pagePath(PAGE_PATH)
        .pageTitle(PAGE_TITLE)
        .pageDescription(PAGE_DESCRIPTION)
        .featureImage(PATH_TO_IMAGE)
        .totalResults(1L)
        .build();
    Pagination pagination = new Pagination();
    pagination.setTotal(1);
    pagination.setLimit(10);
    pagination.setOffset(0);

    SearchListResultModel searchListResultModel =  new SearchListResultModel();
    searchListResultModel.setData(Arrays.asList(searchNodes));
    searchListResultModel.setPagination(pagination);

    when(searchResultsService.appSearch(eq(context.resourceResolver()), eq(searchParams)))
        .thenReturn(searchListResultModel);
    appSearchServlet.doGet(context.request(), context.response());
    assertEquals(HttpStatus.SC_OK, context.response().getStatus());
    assertEquals(
        "{\"data\":[{\"pageTitle\":\"Page Title\",\"pageDescription\":\"Page Description\",\"pagePath\":\"/content/sauditourism/en/app1/node1/node2\",\"featureImage\":\"/path/to/image\",\"totalResults\":1,\"type\":null,\"id\":\"/en/app1/node1/node2\",\"categories\":null}],\"pagination\":{\"total\":1,\"offset\":0,\"limit\":10}}",
        context.response().getOutputAsString());
  }

  @Test
  @DisplayName("Test servlet when all mandatory parameters are set and no data was found")
  void testServletWithParametersAndNoData(AemContext context) throws ServletException, IOException {
    final String exactPath = SEARCH_PATH.replace(LOCALE_TEMPLATE, DEFAULT_LOCALE);
    registerServlet(context, Collections.singletonMap("includeSearchPaths",
        new String[]{SEARCH_PATH}));
    Map<String, Object> map = ImmutableMap.of(
        LOCALE, DEFAULT_LOCALE,
        Constants.SEARCH_QUERY, SEARCH_STRING);
    context.request().setParameterMap(map);

    Pagination pagination = new Pagination();
    pagination.setTotal(0);
    pagination.setLimit(10);
    pagination.setOffset(0);

    SearchListResultModel searchListResultModel =  new SearchListResultModel();
    searchListResultModel.setData(Collections.emptyList());
    searchListResultModel.setPagination(pagination);

    when(searchResultsService.appSearch(any(), any()))
        .thenReturn(searchListResultModel);

    appSearchServlet.doGet(context.request(), context.response());
    assertEquals(HttpStatus.SC_OK, context.response().getStatus());
    assertEquals("{\"data\":[],\"pagination\":{\"total\":0,\"offset\":0,\"limit\":10}}",
        context.response().getOutputAsString());
  }
}
