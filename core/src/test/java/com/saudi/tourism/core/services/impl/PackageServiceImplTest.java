package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.buckets.SimpleBucket;
import com.day.cq.search.facets.extractors.FacetImpl;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackageFilterModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.jackrabbit.vault.util.PathUtil;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.ROOT_CONTENT_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class PackageServiceImplTest {

  private static final Integer LIMIT = 10;
  private static final PackagesRequestParams PARAMS = new PackagesRequestParams();
  private static final String CACHE_KEY = Constants.PACKAGES_CACHE_KEY + Constants.DEFAULT_LOCALE;
  private static final String PAGE_1_PATH = "/content/sauditourism/en/packages/full-day-in-jeddah";
  private static final String PAGE_2_PATH = "/content/sauditourism/en/packages/two-days-in-jeddah-";
  private static final String PAGE_3_PATH = "/content/sauditourism/en/packages";
  private QueryBuilder queryBuilder;
  private Cache memCache;
  private PackageServiceImpl packageService;
  private ResourceBundleProvider i18nProvider;
  private ResourceBundle i18nBundle;

  @BeforeAll
  public static void initParams() {
    PARAMS.setAll(false);
    PARAMS.setLocale(Constants.DEFAULT_LOCALE);
    PARAMS.setLimit(LIMIT);
  }

  @BeforeEach
  public void setUp(AemContext context) {
    // TODO This needs to be mocked instead of using real methods
    final AdminSettingsService adminSettingsService = new AdminSettingsServiceImpl();
    AdminUtil.setAdminSettingsService(adminSettingsService);

    memCache = mock(Cache.class);
    Utils.setInternalState(adminSettingsService, "memCache", memCache);

    final UserService userService = mock(UserService.class);
    when(userService.getResourceResolver()).thenReturn(context.resourceResolver());
    Utils.setInternalState(adminSettingsService, "resolverProvider", userService);

    queryBuilder = mock(QueryBuilder.class);
    packageService = new PackageServiceImpl();
    Utils.setInternalState(packageService, "queryBuilder", queryBuilder);
    Utils.setInternalState(packageService, "memCache", memCache);
    Utils.setInternalState(packageService, "userService", userService);

    i18nProvider = mock(ResourceBundleProvider.class);
    i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };
    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any());
    Utils.setInternalState(packageService, "i18nProvider", i18nProvider);
  }

  @Test @DisplayName("Test private filterPackagesOnParams method when model has null packages")
  void testFilterPackagesOnParams_WhenDataIsNull(AemContext context) {
    when(memCache.get(CACHE_KEY)).thenReturn(new PackagesListModel());

    PackagesListModel result = packageService.getFilteredPackages(PARAMS);
    assertNull(result.getData());
    assertNull(result.getFilters());
    assertNull(result.getPagination());
  }

  @Test @DisplayName("Test private filterPackagesOnParams method when model has no packages")
  void testFilterPackagesOnParams_WhenDataIsEmpty(AemContext context) {
    PackagesListModel model = new PackagesListModel();
    model.setData(Collections.emptyList());
    model.setPagination(new Pagination());
    model.setFilters(initFilterModel());
    when(memCache.get(CACHE_KEY)).thenReturn(model);

    PackagesListModel result = packageService.getFilteredPackages(PARAMS);
    assertEquals(Collections.emptyList(), result.getData());
    assertNotNull(result.getFilters());
    assertEquals(Collections.emptyList(), result.getFilters().getArea());
    assertEquals(Collections.emptyList(), result.getFilters().getBudget());
    assertEquals(Collections.emptyList(), result.getFilters().getCategory());
    assertEquals(Collections.emptyList(), result.getFilters().getDuration());
    assertEquals(Collections.emptyList(), result.getFilters().getTarget());
    assertEquals(LIMIT, result.getPagination().getLimit());
  }

  @Test void testGetAllPackages_WhenNotInCache(AemContext context) throws RepositoryException {
    context.load().json("/pages/full-day-in-jeddah.json", PAGE_1_PATH);
    context.load().json("/pages/two-days-in-jeddah-.json", PAGE_2_PATH);
    context.load().json("/pages/admin-config.json",
        "/content/sauditourism/en/Configs/admin");
    context.load().json("/utils/tags.json", PathUtil.append(Constants.TAGS_URL, "sauditourism"));

    PackagesRequestParams params = new PackagesRequestParams();
    params.setAll(false);
    params.setLocale(Constants.DEFAULT_LOCALE);
    params.setLimit(LIMIT);

    ResourceBundleProvider bundleProvider = mock(ResourceBundleProvider.class);
    when(bundleProvider.getResourceBundle(null, new Locale(Constants.DEFAULT_LOCALE)))
            .thenReturn(Utils.i18nBundle());
    context.registerService(ResourceBundleProvider.class, bundleProvider);

    Query query = mock(Query.class);
    SearchResult searchResult = mock(SearchResult.class);
    Iterator<Node> iteratorNode = mock(Iterator.class);
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(iteratorNode.hasNext()).thenReturn(true, true, true, false);
    when(searchResult.getFacets()).thenReturn(ImmutableMap.of("3_property",
        new FacetImpl(Arrays.asList(new SimpleBucket(null, "sauditourism:city/fayfa"),
            new SimpleBucket(null, "sauditourism:city/Sharma")))));

    Node node1 = mock(Node.class);
    when(node1.getPath()).thenReturn(PathUtils.concat(PAGE_1_PATH, JcrConstants.JCR_CONTENT));

    Node node2 = mock(Node.class);
    when(node2.getPath()).thenReturn(PathUtils.concat(PAGE_2_PATH, JcrConstants.JCR_CONTENT));

    //This is to highlight the scenario when resource is not actually adaptable to PackageDetail,
    //but it is adapted with almost all fields set to null.
    Node node3 = mock(Node.class);
    when(node3.getPath()).thenReturn(PAGE_3_PATH);

    when(iteratorNode.next()).thenReturn(node1, node2, node3);
    when(searchResult.getNodes()).thenReturn(iteratorNode);

    PackagesListModel result = packageService.getFilteredPackages(params);
    assertEquals(2, result.getData().size());
    assertEquals(2, result.getFilters().getArea().size());
    assertEquals(new AppFilterItem("fayfa", "Fayfa"), result.getFilters().getArea().get(0));
    assertEquals(new AppFilterItem("sharma", "Sharma"), result.getFilters().getArea().get(1));
    assertEquals(2, result.getPagination().getTotal());
    assertEquals(10, result.getPagination().getLimit());
    assertEquals(0, result.getPagination().getOffset());
    verify(memCache, times(1)).add(eq(CACHE_KEY), any());
  }

  @Test
  void testFilterPackagesWithOffset0AndLimit1(AemContext context) {
    context.load().json("/pages/full-day-in-jeddah.json", PAGE_1_PATH);
    context.load().json("/pages/two-days-in-jeddah-.json", PAGE_2_PATH);

    PackageDetail detail1 =
        context.resourceResolver().getResource(PathUtils.concat(PAGE_1_PATH,
            JcrConstants.JCR_CONTENT)).adaptTo(PackageDetail.class);

    PackageDetail detail2 =
        context.resourceResolver().getResource(PathUtils.concat(PAGE_2_PATH,
            JcrConstants.JCR_CONTENT)).adaptTo(PackageDetail.class);

    PackagesListModel model = new PackagesListModel();
    model.setData(Arrays.asList(detail1, detail2));
    model.setPagination(new Pagination());
    model.setFilters(initFilterModel());
    when(memCache.get(CACHE_KEY)).thenReturn(model);
    PackagesRequestParams params = new PackagesRequestParams();
    params.setLimit(1);
    params.setOffset(0);
    params.setLocale(Constants.DEFAULT_LOCALE);
    params.setAll(false);
    PackagesListModel result = packageService.getFilteredPackages(params);
    assertEquals(1, result.getData().size());
    assertEquals(PAGE_1_PATH, ROOT_CONTENT_PATH + result.getData().get(0).getPath());
    assertEquals(2, result.getPagination().getTotal());
  }

  @Test
  void testFilterPackagesWithOffset1AndLimit1(AemContext context) {
    context.load().json("/pages/full-day-in-jeddah.json", PAGE_1_PATH);
    context.load().json("/pages/two-days-in-jeddah-.json", PAGE_2_PATH);

    PackageDetail detail1 =
        context.resourceResolver().getResource(PathUtils.concat(PAGE_1_PATH,
            JcrConstants.JCR_CONTENT)).adaptTo(PackageDetail.class);

    PackageDetail detail2 =
        context.resourceResolver().getResource(PathUtils.concat(PAGE_2_PATH,
            JcrConstants.JCR_CONTENT)).adaptTo(PackageDetail.class);

    PackagesListModel model = new PackagesListModel();
    model.setData(Arrays.asList(detail1, detail2));
    model.setPagination(new Pagination());
    model.setFilters(initFilterModel());
    when(memCache.get(CACHE_KEY)).thenReturn(model);
    PackagesRequestParams params = new PackagesRequestParams();
    params.setLimit(1);
    params.setOffset(1);
    params.setLocale(Constants.DEFAULT_LOCALE);
    params.setAll(false);
    PackagesListModel result = packageService.getFilteredPackages(params);
    assertEquals(1, result.getData().size());
    assertEquals(PAGE_2_PATH, ROOT_CONTENT_PATH + result.getData().get(0).getPath());
    assertEquals(2, result.getPagination().getTotal());
  }

  private PackageFilterModel initFilterModel() {
    PackageFilterModel filterModel = new PackageFilterModel();
    filterModel.setArea(Collections.emptyList());
    filterModel.setBudget(Collections.emptyList());
    filterModel.setCategory(Collections.emptyList());
    filterModel.setDuration(Collections.emptyList());
    filterModel.setTarget(Collections.emptyList());
    return filterModel;
  }

}
