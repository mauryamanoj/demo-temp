package com.saudi.tourism.core.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.TestCacheImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RegionCityServiceTest {

  @Mock(answer= Answers.CALLS_REAL_METHODS)
  RegionCityServiceImpl mockService;

  @Mock(answer= Answers.CALLS_REAL_METHODS)
  TestCacheImpl memCache;

  @Mock
  UserService userService;

  @Mock
  TagManager tagManager;

  @Mock
  Tag tag;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private RegionCityService regionCityService;

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/utils/city.json",
        "/apps/sauditourism/components/content/utils/city-fixed");

    context.load().json("/utils/city-extended.json",
        Constants.GLOBAL_CITIES_EXT_DATA_CONTENT);

    ResourceResolver resolver = spy(context.resourceResolver());

    lenient().doReturn("category").when(tag).getTitle(any());
    lenient().doReturn(tag).when(tagManager).resolve(anyString());
    lenient().doReturn(tagManager).when(resolver).adaptTo(TagManager.class);

    memCache.init();
    context.registerService(Cache.class, memCache);

    doReturn(resolver).when(userService).getResourceResolver();
    context.registerService(UserService.class, userService);

    ResourceBundle i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };
    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any());
    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");

    context.registerInjectActivateService(mockService);
    regionCityService = context.getService(RegionCityService.class);
  }

  @Test void getAll() {
    List<RegionCity> all = regionCityService.getAll("en");
    Assertions.assertEquals(2, all.size());
  }

  @Test void getCities() {
    List<RegionCity> cities = regionCityService.getCities("en");
    Assertions.assertEquals(2, cities.size());

    List<RegionCity> regions = regionCityService.getRegions("en");
    Assertions.assertEquals(0, regions.size());
  }

  @Test void getCitiesExt() {
    List<RegionCityExtended> extendeds = regionCityService.getCitiesExt("en");
    for (RegionCityExtended extended : extendeds) {
      for (CategoryTag cat : extended.getDestinationFeatureTags()) {
        Assertions.assertEquals("category", cat.getCopy());
      }
    }
  }

  @Test void getRegionCityById() {
    RegionCity city = regionCityService.getRegionCityById("Riyadh", "en");
    Assertions.assertEquals("Riyadh", city.getId());
  }

  @Test void getRegionCityExtById() {
    RegionCityExtended city = regionCityService.getRegionCityExtById("Riyadh", "en");
    Assertions.assertEquals("Riyadh", city.getId());
  }

  @Test void searchRegionCityByCoords() {
    RegionCityExtended city = regionCityService.searchRegionCityByCoords("en", "12345","54321");
    Assertions.assertEquals("54321", city.getLongitude());
  }
}
