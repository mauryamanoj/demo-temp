package com.saudi.tourism.core.services.impl;

import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackageFilterModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class PackageServiceImplGetFilteredPackageTest {

    private static final String PAGE_PATH = "/content/sauditourism/en/packages/full-day-in-jeddah";
    private static final String TAGS_PATH = "/content/cq:tags/sauditourism";
    private static final String CACHE_KEY = Constants.PACKAGES_CACHE_KEY + Constants.DEFAULT_LOCALE;
    private static final Integer LIMIT = 10;
    private Cache memCache;
    private PackageServiceImpl packageService;
    private List<AppFilterItem> areas;
    private List<AppFilterItem> categories;
    private List<AppFilterItem> target;
    private PackagesRequestParams params = new PackagesRequestParams();

    @BeforeEach
    public void setUp(AemContext context) {
        context.load().json("/pages/full-day-in-jeddah.json", PAGE_PATH);
        context.load().json("/utils/tags.json", TAGS_PATH);

        memCache = mock(Cache.class);
        packageService = new PackageServiceImpl();
        areas = mock(List.class);
        categories = mock(List.class);
        target = mock(List.class);

        ResourceBundleProvider bundleProvider = mock(ResourceBundleProvider.class);
        when(bundleProvider.getResourceBundle(new Locale(Constants.DEFAULT_LOCALE))).thenReturn(Utils.i18nBundle());
        context.registerService(ResourceBundleProvider.class, bundleProvider, ImmutableMap.of(
                "component.name", "org.apache.sling.i18n.impl.JcrResourceBundleProvider"));
        Utils.setInternalState(packageService, "memCache", memCache);

        PackageDetail detail = context.resourceResolver().getResource("/content/sauditourism/en" +
                "/packages/full-day-in-jeddah/jcr:content").adaptTo(PackageDetail.class);

        PackageFilterModel filterModel = new PackageFilterModel();
        filterModel.setArea(areas);
        filterModel.setCategory(categories);
        filterModel.setTarget(target);

        PackagesListModel model = new PackagesListModel();
        model.setData(Collections.singletonList(detail));
        model.setPagination(new Pagination());
        model.setFilters(filterModel);

        when(memCache.get(CACHE_KEY)).thenReturn(model);

        params.setAll(false);
        params.setLocale(Constants.DEFAULT_LOCALE);
        params.setLimit(LIMIT);
    }

    @Test
    @DisplayName("Test getFilteredPackages method when all filters are set and match")
    void getFilteredPackagesWithFilters(AemContext context) {
        params.setDuration(Arrays.asList("1-day"));
        params.setArea(Arrays.asList("jeddah"));
        params.setCategory(Arrays.asList("hike"));
        params.setTarget(Arrays.asList("couples"));
        params.setMinPrice("100");
        params.setMaxPrice("500");

        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when all filters are set and match")
    void getFilteredPackagesWithNoFilters(AemContext context) {
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when duration filter doesn't match")
    void getFilteredPackagesWhenDurationFilterNoMatch(AemContext context) {
        params.setDuration(Arrays.asList("2-day"));
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when area filter doesn't match")
    void getFilteredPackagesWhenAreaFilterNoMatch(AemContext context) {
        params.setArea(Arrays.asList("pavlodar"));
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when category filter doesn't match")
    void getFilteredPackagesWhenCategoryFilterNoMatch(AemContext context) {
        params.setCategory(Arrays.asList("hiking"));
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when target filter doesn't match")
    void getFilteredPackagesWhenTargetFilterNoMatch(AemContext context) {
        params.setTarget(Arrays.asList("Female Travelers"));
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when price filter doesn't match")
    void getFilteredPackagesWhenPriceFilterNoMatch(AemContext context) {
        params.setMinPrice("100");
        params.setMaxPrice("200");
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when all set to true and getRelatedPackagesPaths" +
                         "method is invoked")
    void getFilteredPackagesWhenAll(AemContext context) {
        params.setAll(true);
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getRelatedPackagesPaths().isEmpty());
        assertEquals(1, result.getPagination().getTotal());
    }

    @Test
    @DisplayName("Test getFilteredPackages method when city filter is set")
    void getFilteredPackagesWhenCityFilter(AemContext context) {
        params.setCity("jeddah");
        PackagesListModel result = packageService.getFilteredPackages(params);
        verify(areas, times(1)).sort(any());
        verify(categories, times(1)).sort(any());
        verify(target, times(1)).sort(any());
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getPagination().getTotal());
        assertTrue(params.getRegion().equals(params.getCity()));
    }
}