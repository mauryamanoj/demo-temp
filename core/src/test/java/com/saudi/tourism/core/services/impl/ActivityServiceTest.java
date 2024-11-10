package com.saudi.tourism.core.services.impl;

import com.day.cq.search.QueryBuilder;
import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.models.components.tripplan.v1.SearchActivityFilter;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.TestCacheImpl;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * ActivityService / ActivityServiceImpl.
 */
@ExtendWith(AemContextExtension.class)
class ActivityServiceTest {

  private static final String FULL_PATH_TO_ACTIVITY_PAGE1 = "/full/path/to/activity/page";

  private static final String FULL_PATH_TO_ACTIVITY_PAGE2 = FULL_PATH_TO_ACTIVITY_PAGE1 + "2";

  private AemContext context;

  /**
   * Service being tested.
   */
  private ActivityServiceImpl testService;

  private Activity stubActivity1;
  private Activity stubActivity2;
  private ActivityServiceImpl.ActivityHolder stubHolder;

  @BeforeEach
  void setUp(AemContext context) {
    this.context = context;

    stubActivity1 = new Activity();
    stubActivity1.setPath(FULL_PATH_TO_ACTIVITY_PAGE1);

    stubActivity2 = new Activity();
    stubActivity2.setPath(FULL_PATH_TO_ACTIVITY_PAGE2);

    final List<Activity> stubList = Arrays.asList(stubActivity1, stubActivity2);

    final Map<String, Activity> stubMap = new HashMap<>();
    stubMap.put(FULL_PATH_TO_ACTIVITY_PAGE1, stubActivity1);
    stubMap.put(FULL_PATH_TO_ACTIVITY_PAGE2, stubActivity2);

    stubHolder = new ActivityServiceImpl.ActivityHolder(stubList, stubMap);

    testService = spy(new ActivityServiceImpl());

    Utils.setInternalState(testService, "memCache", Utils.mockMemCache(context));
  }

  @Test
  void testGetActivityList_filter() {
    final String language = "lang";

    final Activity okActivity1 = stubActivity1;
    final Activity okActivity2 = stubActivity2;
    final Activity badActivity1 = mock(Activity.class);
    final Activity badActivity2 = mock(Activity.class);

    final List<Activity> stubActivitiesList =
        Arrays.asList(okActivity1, okActivity2, badActivity1, badActivity2);

    doReturn(stubActivitiesList).when(testService).getActivityList(eq(language));

    final SearchActivityFilter stubFilter = SearchActivityFilter.builder().locale(language).build();
    doReturn(true).when(testService).matchFilters(eq(okActivity1), eq(stubFilter));
    doReturn(true).when(testService).matchFilters(eq(okActivity2), eq(stubFilter));
    doReturn(false).when(testService).matchFilters(eq(badActivity1), eq(stubFilter));
    doReturn(false).when(testService).matchFilters(eq(badActivity2), eq(stubFilter));

    final List<Activity> resultList = testService.getActivityList(stubFilter);

    assertEquals(2, resultList.size());
    assertTrue(resultList.contains(okActivity1));
    assertTrue(resultList.contains(okActivity2));
  }

  @Test
  void getActivityByExactPath_empty_path() {
    doReturn(stubHolder).when(testService).getFromCacheOrQueryActivities(anyString());

    assertNull(testService.getActivityByExactPath(null));
    assertNull(testService.getActivityByExactPath(StringUtils.EMPTY));
    assertNull(testService.getActivityByExactPath(StringUtils.SPACE));
  }

  @Test
  void getActivityByExactPath() {
    doReturn(stubHolder).when(testService).getFromCacheOrQueryActivities(anyString());

    assertSame(stubActivity1, testService.getActivityByExactPath(FULL_PATH_TO_ACTIVITY_PAGE1));
    assertSame(stubActivity2, testService.getActivityByExactPath(FULL_PATH_TO_ACTIVITY_PAGE2));
    // Test path with jcr:content
    assertSame(stubActivity1, testService
        .getActivityByExactPath(FULL_PATH_TO_ACTIVITY_PAGE1 + "/" + JcrConstants.JCR_CONTENT));
    // Test unknown path
    assertNull(testService.getActivityByExactPath("/unknown/path"));
  }

  @Test
  void getFromCacheOrQueryActivities_from_cache() {
    final String language = "lang";
    final String memCacheKey = Constants.KEY_PREFIX_ACTIVITIES + language;

    // Make sure querying is not exectured
    doReturn(null).when(testService).queryActivities(eq(language));

    final TestCacheImpl memCache = Utils.spyMemCache(null);
    memCache.add(memCacheKey, stubHolder);
    Utils.setInternalState(testService, "memCache", memCache);

    assertSame(stubHolder, testService.getFromCacheOrQueryActivities(language));
  }

  @Test
  void getFromCacheOrQueryActivities_query() {
    final String language = "lang";
    final String memCacheKey = Constants.KEY_PREFIX_ACTIVITIES + language;

    doReturn(stubHolder).when(testService).queryActivities(eq(language));

    final TestCacheImpl memCache = Utils.spyMemCache(null);
    Utils.setInternalState(testService, "memCache", memCache);

    // Just make sure that cache is empty
    memCache.remove(memCacheKey);
    assertNull(memCache.get(memCacheKey));

    assertSame(stubHolder, testService.getFromCacheOrQueryActivities(language));
    // Should be stored into the cache
    assertSame(stubHolder, memCache.get(memCacheKey));

    // Empty list shouldn't be cached.
    memCache.remove(memCacheKey);
    assertNull(memCache.get(memCacheKey));
    // Holder containing empty list
    Utils.setInternalState(stubHolder, "list", Collections.emptyList());
    assertSame(stubHolder, testService.getFromCacheOrQueryActivities(language));
    assertNull(memCache.get(memCacheKey));
  }

  /** @noinspection ConstantConditions*/
  @Test
  void matchFilters() {
    // Path filter
    final Activity pathActivity = stubActivity2;
    final SearchActivityFilter pathFilter =
        SearchActivityFilter.builder().path(FULL_PATH_TO_ACTIVITY_PAGE2).build();

    // City filter
    final String cityId1 = "some-city1";
    final String cityId2 = "some-city2";
    final String cityId3 = "some-city3";
    final String cityId4 = null;
    final String cityId5 = StringUtils.SPACE;
    final SearchActivityFilter citiesFilter =
        SearchActivityFilter.builder().city(Arrays.asList(cityId1, cityId2)).build();
    final RegionCity someCity = new RegionCity(cityId1, "some city name");
    final Activity cityActivity1 = new Activity();
    cityActivity1.setCity(someCity);
    final Activity cityActivity2 = new Activity();
    Utils.setInternalState(cityActivity2, "cityId", cityId2);
    final Activity cityActivity3 = new Activity();
    Utils.setInternalState(cityActivity3, "cityId", cityId3);
    final Activity cityActivity4 = new Activity();
    Utils.setInternalState(cityActivity4, "cityId", cityId4);
    final Activity cityActivity5 = new Activity();
    Utils.setInternalState(cityActivity5, "cityId", cityId5);

    // Interest filter
    final String interest1 = "some-interest1";
    final String interest2 = "some-interest2";
    final String interest3 = "some-interest3";
    final SearchActivityFilter interestFilter =
        SearchActivityFilter.builder().interest(Arrays.asList(interest1, interest2)).build();
    final Activity interestActivity1 = new Activity();
    Utils.setInternalState(interestActivity1, "interests", new String[] {interest1});
    final Activity interestActivity2 = new Activity();
    Utils.setInternalState(interestActivity2, "interests", new String[] {interest1, interest2});
    final Activity interestActivity3 = new Activity();
    Utils.setInternalState(interestActivity3, "interests", new String[] {interest3});

    // Partner filter
    final String partner1 = "some-partner1";
    final String partner2 = "some-partner2";
    final String partner3 = "some-partner3";
    final SearchActivityFilter partnerFilter =
        SearchActivityFilter.builder().partner(Arrays.asList(partner1, partner2)).build();
    final Activity partnerActivity1 = new Activity();
    Utils.setInternalState(partnerActivity1, "travelPartner", new String[] {partner1});
    final Activity partnerActivity2 = new Activity();
    Utils.setInternalState(partnerActivity2, "travelPartner", new String[] {partner1, partner2});
    final Activity partnerActivity3 = new Activity();
    Utils.setInternalState(partnerActivity3, "travelPartner", new String[] {partner3});

    // Path
    assertTrue(testService.matchFilters(pathActivity, pathFilter));
    assertFalse(testService.matchFilters(stubActivity1, pathFilter));
    assertFalse(testService.matchFilters(cityActivity1, pathFilter));
    assertFalse(testService.matchFilters(partnerActivity1, pathFilter));
    assertFalse(testService.matchFilters(interestActivity1, pathFilter));

    // City
    assertTrue(testService.matchFilters(cityActivity1, citiesFilter));
    assertTrue(testService.matchFilters(cityActivity2, citiesFilter));
    assertFalse(testService.matchFilters(cityActivity3, citiesFilter));
    assertFalse(testService.matchFilters(cityActivity4, citiesFilter));
    assertFalse(testService.matchFilters(cityActivity5, citiesFilter));
    assertFalse(testService.matchFilters(pathActivity, citiesFilter));
    assertFalse(testService.matchFilters(partnerActivity1, citiesFilter));
    assertFalse(testService.matchFilters(interestActivity2, citiesFilter));

    // Interest
    assertTrue(testService.matchFilters(interestActivity1, interestFilter));
    assertTrue(testService.matchFilters(interestActivity2, interestFilter));
    assertFalse(testService.matchFilters(interestActivity3, interestFilter));
    assertFalse(testService.matchFilters(partnerActivity1, interestFilter));
    assertFalse(testService.matchFilters(cityActivity1, interestFilter));
    assertFalse(testService.matchFilters(pathActivity, interestFilter));

    // Partner
    assertTrue(testService.matchFilters(partnerActivity1, partnerFilter));
    assertTrue(testService.matchFilters(partnerActivity2, partnerFilter));
    assertFalse(testService.matchFilters(partnerActivity3, partnerFilter));
    assertFalse(testService.matchFilters(pathActivity, partnerFilter));
    assertFalse(testService.matchFilters(cityActivity1, partnerFilter));
    assertFalse(testService.matchFilters(interestActivity1, partnerFilter));
  }

  @Test
  void queryActivities() {
    final String language = "lang";

    final ResourceResolver resolver = spy(context.resourceResolver());
    final UserService resolverService = Utils.spyUserService(resolver);
    Utils.setInternalState(testService, "resolverService", resolverService);

    final String path = "/some/path/to/activities/parent/page";
    doReturn(path).when(testService).getPathToActivities(anyString());
    final HashMap<String, String> stubQueryMap = new HashMap<>();
    doReturn(stubQueryMap).when(testService).getQueryMapToSearchActivityPages(eq(path));

    final Resource mockResource1 = Utils.mockAdaptableResource(stubActivity1);
    final Resource mockResource2 = Utils.mockAdaptableResource(stubActivity2);
    // One not adaptable resource
    final Resource nullResource = Utils.mockAdaptableResource(Activity.class, null);
    final QueryBuilder queryBuilder =
        Utils.mockQueryBuilder(mockResource1, mockResource2, nullResource);
    Utils.setInternalState(testService, "queryBuilder", queryBuilder);

    final ActivityServiceImpl.ActivityHolder activityHolder = testService.queryActivities(language);

    verify(testService).getQueryMapToSearchActivityPages(eq(path));
    // Resolver should be gotten from UserService
    verify(resolverService).getResourceResolver();
    // Resolver must be closed after we finish execution
    verify(resolver).close();

    final List<Activity> resultList = activityHolder.getList();
    final Map<String, Activity> resultMap = activityHolder.getMap();
    final int expectedCount = 2;
    assertEquals(expectedCount, resultList.size());
    assertEquals(expectedCount, resultMap.size());

    assertTrue(resultList.containsAll(Arrays.asList(stubActivity1, stubActivity2)));
    assertSame(stubActivity1, resultMap.get(FULL_PATH_TO_ACTIVITY_PAGE1));
    assertSame(stubActivity2, resultMap.get(FULL_PATH_TO_ACTIVITY_PAGE2));
  }
}
