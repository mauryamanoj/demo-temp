package com.saudi.tourism.core.services.impl;

import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.SSIDLoginUserService;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.tripplan.CreateTripPlanFilter;
import com.saudi.tourism.core.models.components.tripplan.v1.CityItinerary;
import com.saudi.tourism.core.models.components.tripplan.v1.TripDay;
import com.saudi.tourism.core.models.components.tripplan.v1.TripDetail;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlan;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanFilter;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPoint;
import com.saudi.tourism.core.services.CalendarService;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.TestCacheImpl;
import com.saudi.tourism.core.utils.Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test for Trip Plan Service.
 *
 * @noinspection FieldCanBeLocal, ResultOfMethodCallIgnored
 */
class SSIDTripPlanServiceTest {

  public static final String ANY_LANGUAGE = "any-language";

  private static final String FULL_PATH_TO_TRIP_PLAN_1 = "/path/to/trip/plan/page";

  private static final String FULL_PATH_TO_TRIP_PLAN_2 = FULL_PATH_TO_TRIP_PLAN_1 + "2";
  private static CityItinerary city1Itinerary;
  private static CityItinerary city2Itinerary;
  private TripPlanServiceImpl.TripPlanHolder stubHolder;
  private SSIDTripPlanServiceImpl testService;
  private TripPlanFilter stubFilter;

  private TripPlan emptyTripPlan;
  private TripPlan stubTripPlan1;
  private TripPlan stubTripPlan2;

  private List<TripPlan> stubTripPlanList;

  @BeforeEach
  public void setUp() {
    final String city1Id = "city1";
    final String city2Id = "city2";

    city1Itinerary = new CityItinerary(city1Id);
    city2Itinerary = new CityItinerary(city2Id);

    // Some empty stub trip plan
    emptyTripPlan = new TripPlan();

    // Trip plan with city "city1"
    stubTripPlan1 = new TripPlan();
    stubTripPlan1.setPath(FULL_PATH_TO_TRIP_PLAN_1);
    stubTripPlan1.getData().getCities().add(city1Itinerary);

    // Trip plan with two cities: "city1", "city2"
    stubTripPlan2 = new TripPlan();
    stubTripPlan2.setPath(FULL_PATH_TO_TRIP_PLAN_2);
    final List<CityItinerary> stubTripPlan2Cities = stubTripPlan2.getData().getCities();
    stubTripPlan2Cities.add(city1Itinerary);
    stubTripPlan2Cities.add(city2Itinerary);

    stubTripPlanList = Arrays.asList(stubTripPlan1, stubTripPlan2);

    final Map<String, TripPlan> stubMap = new HashMap<>();
    stubMap.put(FULL_PATH_TO_TRIP_PLAN_1, stubTripPlan1);
    stubMap.put(FULL_PATH_TO_TRIP_PLAN_2, stubTripPlan2);

    stubFilter = new TripPlanFilter();
    stubFilter.getCityIds().addAll(Arrays.asList(city1Id, city2Id));

    stubHolder = new TripPlanServiceImpl.TripPlanHolder(stubTripPlanList, stubMap, stubFilter);

    testService = spy(new SSIDTripPlanServiceImpl());
  }

  // getTripPlanFilter - Marked as generated

  // getTripPlanList - Marked as generated

  @Test
  void getFromCacheOrQueryTripPlans_from_cache() throws RepositoryException {
    final String language = ANY_LANGUAGE;
    final String memCacheKey = Constants.KEY_PREFIX_TRIP_PLANS + language;

    // Make sure querying is not executed
    doReturn(null).when(testService).queryTripPlans(eq(language));

    final TestCacheImpl memCache = Utils.spyMemCache(null);
    // Put value into the cache
    memCache.add(memCacheKey, stubHolder);
    Utils.setInternalState(testService, "memCache", memCache);

    final TripPlanServiceImpl.TripPlanHolder resultHolder =
        testService.getFromCacheOrQueryTripPlans(language);
    assertSame(stubHolder, resultHolder);
    // For getTripPlanFilter and getTripPlanList - those ones shouldn't be empty after the execution
    assertNotNull(resultHolder.getFilter());
    assertFalse(resultHolder.getList().isEmpty());
  }

  @Test
  void getFromCacheOrQueryTripPlans_throwsRepositoryException() throws RepositoryException {
    final String language = ANY_LANGUAGE;

    final Map<String, String> stubQueryMap = Collections.emptyMap();
    doReturn(stubQueryMap).when(testService).getQueryMapToSearchTripPlanPages(anyString());

    Utils.setInternalState(testService, "memCache", Utils.mockMemCache(null));

    doThrow(RepositoryException.class).when(testService).queryTripPlans(eq(language));

    assertThrows(RepositoryException.class, () -> testService.getTripPlanList(language));
  }

  @Test
  void getFromCacheOrQueryTripPlans() throws RepositoryException {
    final String language = "lang";
    final String memCacheKey = Constants.KEY_PREFIX_TRIP_PLANS + language;

    doReturn(stubHolder).when(testService).queryTripPlans(eq(language));

    final TestCacheImpl memCache = Utils.spyMemCache(null);
    Utils.setInternalState(testService, "memCache", memCache);

    // Just make sure that cache is empty here before checking
    assertNull(memCache.get(memCacheKey));

    // cityIds list is not empty and holder list is not empty
    final TripPlanServiceImpl.TripPlanHolder resultHolder =
        testService.getFromCacheOrQueryTripPlans(language);
    assertSame(stubHolder, resultHolder);
    // For getTripPlanFilter and getTripPlanList - those ones shouldn't be empty after the execution
    assertNotNull(resultHolder.getFilter());
    assertFalse(resultHolder.getList().isEmpty());
    // Should be stored into the cache
    assertSame(stubHolder, memCache.get(memCacheKey));

    // Holder object is changed here, so needs to be re-created in setUp.

    // cityIds list is not empty, but holder list is empty - should be cached
    memCache.remove(memCacheKey);
    assertNull(memCache.get(memCacheKey));
    Utils.setInternalState(stubHolder, "list", Collections.emptyList());
    assertSame(stubHolder, testService.getFromCacheOrQueryTripPlans(language));
    assertSame(stubHolder, memCache.get(memCacheKey));

    // cityIds list is empty, but holder list is not - should be cached
    memCache.remove(memCacheKey);
    assertNull(memCache.get(memCacheKey));
    stubHolder.getFilter().getCityIds().clear();
    Utils.setInternalState(stubHolder, "list", stubTripPlanList);
    assertSame(stubHolder, testService.getFromCacheOrQueryTripPlans(language));
    assertSame(stubHolder, memCache.get(memCacheKey));

    // cityIds list is empty and the holder list is empty - should not be cached
    memCache.remove(memCacheKey);
    assertNull(memCache.get(memCacheKey));
    stubHolder.getFilter().getCityIds().clear();
    Utils.setInternalState(stubHolder, "list", stubTripPlanList);
    // Holder containing an empty list
    Utils.setInternalState(stubHolder, "list", Collections.emptyList());
    assertSame(stubHolder, testService.getFromCacheOrQueryTripPlans(language));
    assertNull(memCache.get(memCacheKey));
  }

  @Test
  void createTripPlan_empty() throws RepositoryException, SSIDFunctionnalException, MiddlewareException {
    final CreateTripPlanFilter filterForEmptyTripPlan = new CreateTripPlanFilter();

    doReturn(null).when(testService)
        .prepareTripPlanWithData(same(filterForEmptyTripPlan), nullable(UserIDToken.class));
    doReturn(emptyTripPlan).when(testService)
        .createEmptyTripPlan(same(filterForEmptyTripPlan), nullable(UserIDToken.class));

    assertSame(emptyTripPlan, testService.createTripPlan(filterForEmptyTripPlan, null));
  }

  @Test
  void createTripPlan_withData() throws RepositoryException, SSIDFunctionnalException, MiddlewareException {
    final CreateTripPlanFilter filter = CreateTripPlanFilter.builder().withData(true).build();

    // UserIdToken is null for these tests
    doReturn(null).when(testService).createEmptyTripPlan(same(filter), nullable(UserIDToken.class));
    doReturn(stubTripPlan1).when(testService)
        .prepareTripPlanWithData(same(filter), nullable(UserIDToken.class));

    assertSame(stubTripPlan1, testService.createTripPlan(filter, null));
  }

  /**
   * @noinspection ConstantConditions
   */
  @Test
  void prepareTripPlanWithData() throws RepositoryException, SSIDFunctionnalException, MiddlewareException {
    // UserIdToken is null in those tests
    final UserIDToken user = null;
    final String language = ANY_LANGUAGE;

    final CreateTripPlanFilter withDataFilter =
        CreateTripPlanFilter.builder().withData(true).locale(language).build();
    final List<TripPlan> list = Collections.emptyList();
    final Calendar someDate = new GregorianCalendar();

    //
    // 1. Check filtered by exact match
    //
    doReturn(list).when(testService).getTripPlanList(eq(language));
    doReturn(stubTripPlan1).when(testService).checkExactMatch(same(withDataFilter), anyList());
    doReturn(null).when(testService)
        .combineCityItineraries(any(CreateTripPlanFilter.class), anyList());
    doReturn(emptyTripPlan).when(testService)
        .populateTripPlan(any(TripPlan.class), any(CreateTripPlanFilter.class), anyBoolean());
    doNothing().when(testService)
        .savePreparedTripPlan(any(TripPlan.class), nullable(UserIDToken.class));

    final TripPlan exactMatchTripPlan = testService.prepareTripPlanWithData(withDataFilter, user);
    assertSame(emptyTripPlan, exactMatchTripPlan);
    verify(testService, times(1)).getTripPlanList(anyString());
    verify(testService).checkExactMatch(same(withDataFilter), same(list));
    verify(testService)
        .populateTripPlan(any(TripPlan.class), any(CreateTripPlanFilter.class), eq(false));

    //
    // 2. Check combined from several city itineraries
    //
    doReturn(null).when(testService).checkExactMatch(same(withDataFilter), anyList());
    doReturn(stubTripPlan2).when(testService)
        .combineCityItineraries(any(CreateTripPlanFilter.class), anyList());
    // + populate with true param
    withDataFilter.setStartDate(someDate);

    final TripPlan combinedTripPlan = testService.prepareTripPlanWithData(withDataFilter, user);
    assertSame(emptyTripPlan, combinedTripPlan);
    verify(testService, times(2)).getTripPlanList(anyString());
    verify(testService).combineCityItineraries(same(withDataFilter), same(list));
    verify(testService)
        .populateTripPlan(any(TripPlan.class), any(CreateTripPlanFilter.class), eq(true));
  }

  @Test
  void savePreparedTripPlan_no_user() throws SSIDFunctionnalException, MiddlewareException {
    // userIdToken is null
    testService.savePreparedTripPlan(stubTripPlan1, null);
    // user is null
    final UserIDToken noUser1 = new UserIDToken();
    noUser1.setToken("some-token");
    testService.savePreparedTripPlan(stubTripPlan1, noUser1);
    // user is blank
    final UserIDToken noUser2 = new UserIDToken();
    noUser2.setToken("some-token");
    testService.savePreparedTripPlan(stubTripPlan1, noUser2);
    // token is null
    final UserIDToken noToken1 = new UserIDToken();
    noToken1.setUser("some-user");
    testService.savePreparedTripPlan(stubTripPlan1, noToken1);
    // token is blank
    final UserIDToken noToken2 = new UserIDToken();
    noToken2.setUser("some-user");
    testService.savePreparedTripPlan(stubTripPlan1, noToken2);

    // Shouldn't be called
    verify(testService, times(0)).prepareSaveTripPlanJson(any(TripPlan.class));
  }

  @Test
  void savePreparedTripPlan() throws IOException, SSIDFunctionnalException, MiddlewareException {
    final TripPlan tripPlan = stubTripPlan1;
    final String someJson = "{\"some\": \"json\"}";
    final String someId = "some-trip-plan-id";
    final UserIDToken user = new UserIDToken();
    user.setUser("some-user");
    user.setToken("some-token");
    user.setLocale("some-locale");

    doReturn(someJson).when(testService).prepareSaveTripPlanJson(tripPlan);
    final SSIDLoginUserService loginUserService = mock(SSIDLoginUserService.class);
    doReturn(loginUserService).when(testService).getLoginUserService();
    doReturn(someId).when(loginUserService).saveTripPlan(same(someJson), same(user));

    // Just to make sure we don't have id in the trip plan to save
    assertTrue(
        tripPlan.getId() == null || tripPlan.getId().equals(TripPlanConstants.NEW_TRIP_PLAN_ID));
    testService.savePreparedTripPlan(tripPlan, user);
    // Id is changed
    assertEquals(someId, tripPlan.getId());

    // Test IO Exception
    doThrow(IOException.class).when(loginUserService).saveTripPlan(same(someJson), same(user));
    assertThrows(IllegalStateException.class,
        () -> testService.savePreparedTripPlan(tripPlan, user));
  }

  // createEmptyTripPlan - Marked as generated

  @Test
  void combineCityItineraries() {
    final String city1Id = "city1";
    final String city2Id = "city2";

    final List<String> someCityIds = Arrays.asList(city1Id, city2Id);
    final CreateTripPlanFilter someCreateFilter =
        CreateTripPlanFilter.builder().city(someCityIds).build();
    final CreateTripPlanFilter emptyCreateFilter = new CreateTripPlanFilter();
    final List<TripPlan> stubAllTripPlanList = stubTripPlanList;
    final List<CityItinerary> stubCityItinerariesList =
        Arrays.asList(city1Itinerary, city2Itinerary);

    // 1. There are no trip itineraries in AEM
    final List<TripPlan> emptyAllTripPlanList = new LinkedList<>();
    doReturn(Collections.emptyList()).when(testService)
        .extractCityItineraries(same(emptyAllTripPlanList), eq(stubFilter.getCityIds()));
    assertThrows(IllegalArgumentException.class,
        () -> testService.combineCityItineraries(someCreateFilter, emptyAllTripPlanList));

    // 2. Empty cityIds
    doReturn(stubCityItinerariesList).when(testService)
        .extractCityItineraries(same(stubAllTripPlanList), same(stubFilter.getCityIds()));
    assertThrows(IllegalArgumentException.class,
        () -> testService.combineCityItineraries(emptyCreateFilter, stubAllTripPlanList));

    // 3. No city itineraries found for city ids
    doReturn(Collections.emptyList()).when(testService)
        .extractCityItineraries(eq(stubAllTripPlanList), eq(someCityIds));
    doReturn(0).when(testService).calculateDaysDistance(city1Itinerary);
    assertThrows(IllegalArgumentException.class,
        () -> testService.combineCityItineraries(someCreateFilter, stubAllTripPlanList));

    // 4. Choosing min by distance out of two itineraries for city1
    final CityItinerary city1Itinerary2 = new CityItinerary(city1Id);
    doReturn(Arrays.asList(city1Itinerary, city1Itinerary2)).when(testService)
        .extractCityItineraries(eq(stubAllTripPlanList), eq(someCityIds));
    // city2Itinerary should be in the result (minimal days distance)
    doReturn(1).when(testService).calculateDaysDistance(city1Itinerary);
    doReturn(0).when(testService).calculateDaysDistance(city1Itinerary2);
    final TripPlan minDistanceResult =
        testService.combineCityItineraries(someCreateFilter, stubAllTripPlanList);
    assertNotNull(minDistanceResult);
    final List<CityItinerary> minDistanceResultCities = minDistanceResult.getData().getCities();
    assertEquals(1, minDistanceResultCities.size());
    assertSame(city1Itinerary2, minDistanceResultCities.get(0));

    // 5. Request for n cities should produce trip plan containing n cities
    final CityItinerary city3Itinerary = new CityItinerary("city3");
    doReturn(Arrays.asList(city1Itinerary, city2Itinerary, city1Itinerary2, city3Itinerary))
        .when(testService).extractCityItineraries(eq(stubAllTripPlanList), eq(someCityIds));
    doReturn(0).when(testService).calculateDaysDistance(any(CityItinerary.class));
    final TripPlan okResult =
        testService.combineCityItineraries(someCreateFilter, stubAllTripPlanList);
    final List<String> filterCities = someCreateFilter.getCity();
    final List<CityItinerary> okResultCities = okResult.getData().getCities();
    assertEquals(filterCities.size(), okResultCities.size());
    assertTrue(okResultCities.stream()
        .allMatch(cityItinerary -> filterCities.contains(cityItinerary.getCityId())));
  }

  @Test
  void checkExactMatch() {
    final String city1Id = "city1";
    final String city2Id = "city2";
    final String city3Id = "city3";

    doReturn(0).when(testService).calculateDaysDistance(any(CityItinerary.class));

    final CreateTripPlanFilter stubCreateFilter =
        CreateTripPlanFilter.builder().city(Arrays.asList(city1Id, city2Id, city3Id)).build();

    // 1. Couldn't find matching
    doReturn(false).when(testService).matchFilters(any(TripPlan.class), eq(stubCreateFilter));
    assertNull(testService.checkExactMatch(stubCreateFilter, stubTripPlanList));

    // 2. Empty list
    final List<TripPlan> emptyTripPlanList = Collections.emptyList();
    doReturn(true).when(testService).matchFilters(any(TripPlan.class), eq(stubCreateFilter));
    assertNull(testService.checkExactMatch(stubCreateFilter, emptyTripPlanList));

    // 3. Ok and answers min by distance (the second one)
    doReturn(1).when(testService).calculateDaysDistance(stubTripPlan1);
    assertSame(stubTripPlan2, testService.checkExactMatch(stubCreateFilter, stubTripPlanList));
  }

  @Test
  void getCityPreferredTripDaysCount() {
    final String cityId = "city1";

    final RegionCityService mockCityService = mock(RegionCityService.class);
    Utils.setInternalState(testService, "citiesService", mockCityService);

    // 1. City not found (city is null)
    assertEquals(TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT,
        testService.getCityPreferredTripDaysCount(cityId));

    // 2. Days count from city
    final int expectedPref = 123;
    final RegionCityExtended mockRegionCityExtended = mock(RegionCityExtended.class);
    //noinspection ResultOfMethodCallIgnored
    doReturn((long) expectedPref).when(mockRegionCityExtended).getPreferredTripDaysCount();
    doReturn(mockRegionCityExtended).when(mockCityService)
        .getRegionCityExtById(eq(cityId), anyString());
    assertEquals(expectedPref, testService.getCityPreferredTripDaysCount(cityId));
  }

  @Test
  void calculateDaysDistance_TripPlan() {
    // 1. No data
    assertEquals(TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT,
        testService.calculateDaysDistance(emptyTripPlan));

    // 2. Exact days distance - two cities trip plan
    final int daysCity1 = 2;
    final int daysCity2 = 3;
    doReturn(daysCity1).when(testService).calculateDaysDistance(city1Itinerary);
    doReturn(daysCity2).when(testService).calculateDaysDistance(city2Itinerary);
    assertEquals(daysCity1 + daysCity2, testService.calculateDaysDistance(stubTripPlan2));
  }

  @Test
  void calculateDaysDistance_TripDetail() {
    // 1. No cities in trip plan
    final TripDetail emptyTripDetail = new TripDetail();
    assertEquals(TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT,
        testService.calculateDaysDistance(emptyTripDetail));

    // 2. Sum all cities from trip detail
    final TripDetail mockTripDetail = mock(TripDetail.class);
    final List<CityItinerary> listOfItineraries = Arrays.asList(city1Itinerary, city2Itinerary);
    //noinspection ResultOfMethodCallIgnored
    doReturn(listOfItineraries).when(mockTripDetail).getCities();
    final int daysCity1 = 2;
    final int daysCity2 = 3;
    doReturn(daysCity1).when(testService).calculateDaysDistance(city1Itinerary);
    doReturn(daysCity2).when(testService).calculateDaysDistance(city2Itinerary);
    assertEquals(daysCity1 + daysCity2, testService.calculateDaysDistance(mockTripDetail));
  }

  @Test
  void calculateDaysDistance_CityItinerary() {
    final CityItinerary testItinerary = SSIDTripPlanServiceTest.city2Itinerary;
    final String cityId = testItinerary.getCityId();

    final int cityPrefCount = 4;
    final int expectedResult = cityPrefCount - testItinerary.getDays().size();

    doReturn((long) cityPrefCount).when(testService).getCityPreferredTripDaysCount(cityId);

    assertEquals(expectedResult, testService.calculateDaysDistance(testItinerary));
  }

  @Test
  void prepareEmptyTripPlan() {
    doNothing().when(testService).adjustDates(any(TripPlan.class), any(CreateTripPlanFilter.class));

    // 1. Blank title, no cities
    final CreateTripPlanFilter emptyCreateFilter = new CreateTripPlanFilter();
    final TripPlan resultEmptyTripPlan1 = testService.prepareEmptyTripPlan(emptyCreateFilter);
    assertNull(resultEmptyTripPlan1.getTitle());
    assertTrue(isEmpty(resultEmptyTripPlan1.getData().getCities()));

    //
    // 2. Not blank title, has cities in filter
    //
    final String locale = ANY_LANGUAGE;
    final String city1Id = "city1";
    final String city2Id = "city2";
    final CreateTripPlanFilter stubCreateFilter =
        CreateTripPlanFilter.builder().city(Arrays.asList(city1Id, city2Id)).locale(locale).build();
    final String testTitle = "test trip plan title";
    stubCreateFilter.setTitle(testTitle);

    final RegionCity stubCity1 = new RegionCity(city1Id, city1Id);
    final RegionCity stubCity2 = new RegionCity(city2Id, city2Id);
    final RegionCityService mockCitiesService = mock(RegionCityService.class);
    Utils.setInternalState(testService, "citiesService", mockCitiesService);
    doReturn(stubCity1).when(mockCitiesService).getRegionCityById(eq(city1Id), eq(locale));
    doReturn(stubCity2).when(mockCitiesService).getRegionCityById(eq(city2Id), eq(locale));

    final TripPlan resultEmptyTripPlan2 = testService.prepareEmptyTripPlan(stubCreateFilter);
    assertEquals(testTitle, resultEmptyTripPlan2.getTitle());
    final List<CityItinerary> cityItineraries = resultEmptyTripPlan2.getData().getCities();
    final List<String> filterCities = stubCreateFilter.getCity();
    assertEquals(filterCities.size(), cityItineraries.size());
    assertTrue(cityItineraries.stream()
        .allMatch(itinerary -> filterCities.contains(itinerary.getCityId())));
  }

  @Test
  void extractCityItineraries_city_itineraries() {
    final List<CityItinerary> cityItineraries = Arrays.asList(city1Itinerary, city2Itinerary);

    // blank city id returns empty stream
    assertFalse(testService.extractCityItineraries(cityItineraries, StringUtils.EMPTY).findFirst()
        .isPresent());

    // get only matching itineraries
    final String cityId = "city1";
    final Stream<CityItinerary> resultStream =
        testService.extractCityItineraries(cityItineraries, cityId);
    assertTrue(resultStream.allMatch(cityItinerary -> cityItinerary.getCityId().equals(cityId)));
  }

  @Test
  void extractCityItineraries_trip_plans() {
    final TripPlan stubTripPlan3 = new TripPlan();
    stubTripPlan3.setPath(FULL_PATH_TO_TRIP_PLAN_1 + "3");
    final CityItinerary city3Itinerary = new CityItinerary("city3");
    stubTripPlan3.getData().getCities().add(city3Itinerary);
    final List<TripPlan> allTripPlansList =
        Arrays.asList(stubTripPlan1, stubTripPlan2, stubTripPlan3);

    final List<String> citiesRequested = Arrays.asList("city1", "city2");

    // Empty trip plans list
    assertTrue(
        testService.extractCityItineraries(Collections.emptyList(), citiesRequested).isEmpty());
    // Empty list of cities
    assertTrue(
        testService.extractCityItineraries(allTripPlansList, Collections.emptyList()).isEmpty());

    // Exact cities
    final List<CityItinerary> resultItinerariesList =
        testService.extractCityItineraries(allTripPlansList, citiesRequested);
    assertTrue(resultItinerariesList.stream()
        .allMatch(cityItinerary -> citiesRequested.contains(cityItinerary.getCityId())));
  }

  // getLoginUserService - Marked as generated

  // prepareSaveTripPlanJson - Marked as generated

  @Test
  void traverseFilter() throws IOException {
    //@formatter:off
    final String tripPlanJson = "{\n"
        + "  \"id\": \"4824773c-3430-4356-a962-182d7468ded7\",\n"
        + "  \"title\": \"My trip to Riyadh\",\n"
        + "  \"desktopImage\": \"/content/dam/Screen_Shot_2017-09-18_at_16.22.53.png\",\n"
        + "  \"mobileImage\": \"/content/dam/Screen_Shot_2017-09-18_at_16.22.53.png\",\n"
        + "  \"startDate\": \"2020-05-14T00:00:00+03:00\",\n"
        + "  \"displayedStartDate\": \"Thu 14, May\",\n"
        + "  \"endDate\": \"2020-05-16T00:00:00+03:00\",\n"
        + "  \"displayedEndDate\": \"Sat 16, May\",\n" + "  \"cities\": [\n"
        + "    {\n"
        + "      \"cityId\": \"riyadh\",\n"
        + "      \"city\": {\n"
        + "        \"id\": \"riyadh\",\n"
        + "        \"name\": \"Riyadh\",\n"
        + "        \"preferredTripDaysCount\": 3,\n"
        + "        \"image\": {\n"
        + "          \"fileReference\": \"/content/dam/Screen_Shot_2017-09-18_at_16"
        + ".22.53.png\",\n"
        + "          \"mobileImageReference\": "
        + "\"/content/dam/Screen_Shot_2017-09-18_at_16.22.53.png\",\n"
        + "          \"s7fileReference\": "
        + "\"/content/dam/Screen_Shot_2017-09-18_at_16.22.53.png\",\n"
        + "          \"desktopImage\": \"/content/dam/Screen_Shot_2017-09-18_at_16"
        + ".22.53.png\",\n"
        + "          \"mobileImage\": \"/content/dam/Screen_Shot_2017-09-18_at_16"
        + ".22.53.png\"\n"
        + "        }\n" + "      },\n" + "      \"days\": [\n" + "        {\n"
        + "          \"id\": \"day1\",\n"
        + "          \"date\": \"2020-05-14T00:00:00+03:00\",\n"
        + "          \"displayedDate\": \"Thu 14, May\",\n"
        + "          \"schedulePossiblyAffected\": false,\n"
        + "          \"schedule\": [\n" + "            {\n"
        + "              \"type\": \"activity\",\n"
        + "              \"id\": \"/Configs/activities/riyadh/1-number-nine\",\n"
        + "              \"path\": "
        + "\"/content/sauditourism/en/Configs/activities/riyadh/1-number-nine\",\n"
        + "              \"favId\": " + "\"/do/lifestyle/4-Must-See-Concept-Stores-in-Riyadh\",\n"
        + "              \"link\": "
        + "\"/content/sauditourism/en/do/lifestyle/4-Must-See-Concept-Stores-in-Riyadh.html\",\n"
        + "              \"imageSource\": "
        + "\"/content/dam/app/new-app/Saudi_APP_-_Resize_images_II/Riyadh’s_must"
        + "-see_concept_stores/Thumbs_300_px/img2-300px.jpg\",\n"
        + "              \"cityId\": \"riyadh\",\n"
        + "              \"city\": {\n"
        + "                \"id\": \"riyadh\",\n"
        + "                \"name\": \"Riyadh\"\n" + "              },\n"
        + "              \"title\": \"1. Number Nine\",\n"
        + "              \"shortDescription\": \"Some short description.\",\n"
        + "              \"description\": \"Some description description description.\",\n"
        + "              \"roundTheClock\": false,\n"
        + "              \"averageTime\": 0,\n"
        + "              \"latitude\": \"24.731850\",\n"
        + "              \"longitude\": \"46.649800\",\n"
        + "              \"image\": {\n" + "                " + "\"fileReference\": "
        + "\"/content/dam/app/new-app/Saudi_APP_-_Resize_images_II/Riyadh’s_must"
        + "-see_concept_stores/Thumbs_300_px/img2-300px.jpg\",\n"
        + "                \"alt\": \"1. Number Nine\"\n"
        + "              },\n" + "              \"tagIds\": [\n"
        + "                \"sauditourism:events/shopping-retail\"\n"
        + "              ],\n" + "              \"tags\": [\n"
        + "                {\n" + "                  \"id\": "
        + "\"sauditourism:events/shopping-retail\",\n"
        + "                  \"copy\": \"Shopping & Retail\"\n"
        + "                }\n"
        + "              ]\n"
        + "            }\n"
        + "          ]\n"
        + "        },\n"
        + "        {\n"
        + "          \"id\": \"day2\",\n"
        + "          \"date\": \"2020-05-15T00:00:00+03:00\",\n"
        + "          \"displayedDate\": \"Fri 15, May\",\n"
        + "          \"schedulePossiblyAffected\": false,\n"
        + "          \"schedule\": [\n" + "            {\n"
        + "              \"type\": \"activity\",\n"
        + "              \"id\": \"/Configs/activities/riyadh/2-pattern\",\n"
        + "              \"path\": "
        + "\"/content/sauditourism/en/Configs/activities/riyadh/2-pattern\",\n"
        + "              \"favId\": " + "\"/do/lifestyle/4-Must-See-Concept-Stores-in-Riyadh\",\n"
        + "              \"link\": "
        + "\"/content/sauditourism/en/do/lifestyle/4-Must-See-Concept-Stores-in-Riyadh.html\",\n"
        + "              \"imageSource\": "
        + "\"/content/dam/app/new-app/Saudi_APP_-_Resize_images_II/Riyadh’s_must"
        + "-see_concept_stores/Thumbs_300_px/img3-300px.jpg\",\n"
        + "              \"cityId\": \"riyadh\",\n"
        + "              \"city\": {\n"
        + "                \"id\": \"riyadh\",\n"
        + "                \"name\": \"Riyadh\"\n" + "              },\n"
        + "              \"title\": \"2. Pattern\",\n"
        + "              \"shortDescription\": \"Some short description\",\n"
        + "              \"description\": \"Some description\",\n"
        + "              \"roundTheClock\": false,\n"
        + "              \"averageTime\": 0,\n"
        + "              \"latitude\": \"24.565510\",\n"
        + "              \"longitude\": \"46.772290\",\n"
        + "              \"image\": {\n"
        + "                \"fileReference\": "
        + "\"/content/dam/app/new-app/Saudi_APP_-_Resize_images_II/Riyadh’s_must"
        + "-see_concept_stores/Thumbs_300_px/img3-300px.jpg\",\n"
        + "                \"alt\": \"2. Pattern\"\n"
        + "              },\n"
        + "              \"tagIds\": [\n"
        + "                \"sauditourism:events/shopping-retail\"\n"
        + "              ],\n" + "              \"tags\": [\n"
        + "                {\n"
        + "                  \"id\": " + "\"sauditourism:events/shopping-retail\",\n"
        + "                  \"copy\": \"Shopping & Retail\"\n"
        + "                }\n" + "              ]\n" + "            }\n"
        + "          ]\n" + "        }\n" + "      ],\n" + "      \"daysCount\": 2\n"
        + "    }\n" + "  ],\n" + "  \"daysCount\": 2\n" + "}";
    //@formatter:on

    // 0. Root: id, path, startDate, endDate, title, cities
    // 1. Level one is "cities" array
    // 2. City itinerary - cityId, city, days
    // 3. Level three is "days" array or "city" child of city itinerary - id
    // 4. Trip day - id, schedule
    // 5. Level five is "schedule" array
    // 6. Trip point - id, path, cityId, city
    // 7. City in trip point - id

    //@formatter:off
    final String expectedJsonText = "{\n"
        + "  \"id\": \"4824773c-3430-4356-a962-182d7468ded7\",\n"
        + "  \"title\": \"My trip to Riyadh\",\n"
        + "  \"startDate\": \"2020-05-14T00:00:00+03:00\",\n"
        + "  \"endDate\": \"2020-05-16T00:00:00+03:00\",\n"
        + "  \"cities\": [\n"
        + "    {\n"
        + "      \"cityId\": \"riyadh\",\n"
        + "      \"city\": {\n"
        + "        \"id\": \"riyadh\"\n"
        + "      },\n"
        + "      \"days\": [\n"
        + "        {\n"
        + "          \"id\": \"day1\",\n"
        + "          \"schedule\": [\n"
        + "            {\n"
        + "              \"id\": \"/Configs/activities/riyadh/1-number-nine\",\n"
        + "              \"path\": "
        + "\"/content/sauditourism/en/Configs/activities/riyadh/1-number-nine\",\n"
        + "              \"cityId\": \"riyadh\",\n"
        + "              \"city\": {\n"
        + "                \"id\": \"riyadh\"\n"
        + "              }\n"
        + "            }\n"
        + "          ]\n"
        + "        },\n"
        + "        {\n"
        + "          \"id\": \"day2\",\n"
        + "          \"schedule\": [\n"
        + "            {\n"
        + "              \"id\": \"/Configs/activities/riyadh/2-pattern\",\n"
        + "              \"path\": "
        + "\"/content/sauditourism/en/Configs/activities/riyadh/2-pattern\",\n"
        + "              \"cityId\": \"riyadh\",\n"
        + "              \"city\": {\n"
        + "                \"id\": \"riyadh\"\n"
        + "              }\n"
        + "            }\n"
        + "          ]\n"
        + "        }\n"
        + "      ]\n"
        + "    }\n"
        + "  ]\n"
        + "}\n";
    //@formatter:on


    final JsonNode tripPlanJsonNode = RestHelper.getObjectMapper().readTree(tripPlanJson);
    assertEquals(RestHelper.getObjectMapper().readTree(expectedJsonText),
        SSIDTripPlanServiceImpl.traverseFilter(tripPlanJsonNode, 0));
  }

  @Test
  void matchFilters() {
    // Empty filter
    assertFalse(testService.matchFilters(stubTripPlan1, new CreateTripPlanFilter()));
    // Filter for trip plan 2 path
    final CreateTripPlanFilter tripPlan2PathFilter =
        CreateTripPlanFilter.builder().path(FULL_PATH_TO_TRIP_PLAN_2).build();
    assertFalse(testService.matchFilters(stubTripPlan1, tripPlan2PathFilter));
    assertTrue(testService.matchFilters(stubTripPlan2, tripPlan2PathFilter));
    // Filter for city "city1" - exact match for stubTripPlan1
    final String city1Id = "city1";
    final CreateTripPlanFilter city1Filter =
        CreateTripPlanFilter.builder().city(Collections.singletonList(city1Id)).build();
    assertTrue(testService.matchFilters(stubTripPlan1, city1Filter));
    assertFalse(testService.matchFilters(stubTripPlan2, city1Filter));
    // Filter for city "city2" (no exact match)
    final String city2Id = "city2";
    final CreateTripPlanFilter city2Filter =
        CreateTripPlanFilter.builder().city(Collections.singletonList(city2Id)).build();
    assertFalse(testService.matchFilters(stubTripPlan1, city2Filter));
    assertFalse(testService.matchFilters(stubTripPlan2, city2Filter));
    // Filter for cities "city1" and "city2"
    final CreateTripPlanFilter city1and2filter =
        CreateTripPlanFilter.builder().city(Arrays.asList(city1Id, city2Id)).build();
    assertFalse(testService.matchFilters(stubTripPlan1, city1and2filter));
    assertTrue(testService.matchFilters(stubTripPlan2, city1and2filter));
    // Empty trip plan
    assertFalse(testService.matchFilters(new TripPlan(), city1Filter));
  }

  // getPathToTripPlans - Marked as Generated

  /**
   * @noinspection ResultOfMethodCallIgnored
   */
  @Test
  void populateTripPlan() throws RepositoryException {
    final String cityId = "any-city";

    final TripPlan stubTripPlan = new TripPlan();
    final TripDetail expectedData = mock(TripDetail.class);
    Utils.setInternalState(stubTripPlan, "data", expectedData);

    final CityItinerary stubCityItinerary = new CityItinerary();
    final List<CityItinerary> expectedCities = Collections.singletonList(stubCityItinerary);
    doReturn(expectedCities).when(expectedData).getCities();

    final TripDay mockDay1 = mock(TripDay.class);
    final TripDay mockDay2 = mock(TripDay.class);
    final List<TripDay> expectedDays = stubCityItinerary.getDays();
    expectedDays.add(mockDay1);
    expectedDays.add(mockDay2);

    final LinkedList<TripPoint> tripPoints = new LinkedList<>();
    tripPoints.add(mock(TripPoint.class));
    doReturn(tripPoints).when(mockDay1).getTripPoints();

    final CreateTripPlanFilter filter =
        CreateTripPlanFilter.builder().city(Collections.singletonList(cityId))
            .locale(Constants.DEFAULT_LOCALE).build();
    filter.setCity(Collections.singletonList(cityId));

    doNothing().when(testService).addEventsToDay(any(), any(), any(), any(), any());
    doNothing().when(testService).adjustDates(any(TripPlan.class), any(CreateTripPlanFilter.class));

    //
    // Check no start date, no events, no title
    //
    final TripPlan resultPopulatedTripPlan =
        testService.populateTripPlan(stubTripPlan, filter, false);

    final List<TripPoint> expectedPoints =
        expectedDays.stream().map(TripDay::getTripPoints).flatMap(Collection::stream)
            .collect(Collectors.toList());

    final TripDetail foundData = resultPopulatedTripPlan.getData();
    final List<CityItinerary> foundCities = foundData.getCities();
    final List<TripDay> foundDays =
        foundCities.stream().map(CityItinerary::getDays).flatMap(Collection::stream)
            .collect(Collectors.toList());
    final List<TripPoint> foundPoints =
        foundDays.stream().map(TripDay::getTripPoints).flatMap(Collection::stream)
            .collect(Collectors.toList());

    assertNull(resultPopulatedTripPlan.getTitle());
    assertNull(resultPopulatedTripPlan.getStartDate());
    assertNotSame(expectedData, foundData);
    assertNotSame(expectedCities, foundCities);
    assertNotSame(expectedDays, foundDays);
    assertEquals(expectedDays.size(), foundDays.size());

    // Check no events were added
    assertEquals(expectedPoints.size(), foundPoints.size());

    //
    // Check if data null (no such node on page)
    //
    final TripPlan nullDataTripPlan = new TripPlan();
    Utils.setInternalState(nullDataTripPlan, "data", null);
    final TripPlan resultEmptyTripPlan =
        testService.populateTripPlan(nullDataTripPlan, filter, false);
    assertNull(resultEmptyTripPlan.getData());

    //
    // With start date and events
    //
    final Calendar calendarDate = Calendar.getInstance(Constants.DEFAULT_TIME_ZONE);
    calendarDate.setTimeInMillis(0L);
    calendarDate.set(Calendar.YEAR, 2021);
    calendarDate.set(Calendar.MONTH, Calendar.JANUARY);
    calendarDate.set(Calendar.DAY_OF_MONTH, 1);
    calendarDate.set(Calendar.HOUR, 0);

    final String expectedTitle = "some title";
    filter.setTitle(expectedTitle);
    filter.setStartDate(calendarDate);

    final EventService eventService = mock(EventService.class);
    Utils.setInternalState(testService, "eventService", eventService);
    final EventListModel stubFilteredEventsList = new EventListModel();
    doReturn(stubFilteredEventsList).when(eventService)
        .getFilteredEvents(any(EventsRequestParams.class));

    final CalendarService calendarService = mock(CalendarService.class);
    Utils.setInternalState(testService, "calendarService", calendarService);

    final TripPlan populatedWithEvents = testService.populateTripPlan(stubTripPlan, filter, true);
    assertEquals(expectedTitle, populatedWithEvents.getTitle());
    verify(testService, times(expectedDays.size()))
        .addEventsToDay(any(TripDay.class), same(stubFilteredEventsList), any(), any(), any());
    final List<CityItinerary> cityItineraries = populatedWithEvents.getData().getCities();
    final CityItinerary itinerary = cityItineraries.get(0);
    final List<TripDay> days = itinerary.getDays();
    assertEquals("2021-01-01T00:00:00+03:00", days.get(0).getDate());
    assertEquals("2021-01-02T00:00:00+03:00", days.get(1).getDate());
  }

  @Test
  void getPopulatedUserTripPlan() {
    // TODO Write this
    assertTrue(true);
  }

  @Test
  void prepareAndAddNewTripDay() {
    final String language = ANY_LANGUAGE;
    final ResourceResolver mockResolver = mock(ResourceResolver.class);
    final RegionCityExtended mockCity = mock(RegionCityExtended.class);

    final List<TripDay> days = new LinkedList<>();

    // Original day is null
    testService.prepareAndAddNewTripDay(days, language, mockResolver, mockCity, null);
    assertTrue(CollectionUtils.isEmpty(days.get(0).getTripPoints()));

    // Day is with data
    days.clear();
    final TripDay sourceDay = new TripDay();
    final TripPoint mockPoint = new TripPoint();
    sourceDay.getTripPoints().add(mockPoint);

    // This is producing handled exception, so points anyway will be empty
    testService.prepareAndAddNewTripDay(days, language, mockResolver, mockCity, sourceDay);
    assertTrue(CollectionUtils.isEmpty(days.get(0).getTripPoints()));

    // Looks like this test or the tested method, or both need to be corrected

  }

  @Test
  void updateNewDayDates() {
    final TripDay tripDay = new TripDay();
    final String locale = ANY_LANGUAGE;
    final LocalDate startDate = LocalDate.parse("2021-01-01");

    final int totalDays = 4;
    final String expectedDayDate = "2021-01-04T00:00:00+03:00";

    final CalendarService calendarService = mock(CalendarService.class);
    Utils.setInternalState(testService, "calendarService", calendarService);
    final String someHolidayName = "holiday";
    doReturn(someHolidayName).when(calendarService)
        .getHolidayNameByDate(eq(startDate.plusDays(totalDays - 1)), eq(locale));

    testService.updateNewDayDates(tripDay, locale, startDate, totalDays);

    assertEquals(expectedDayDate, tripDay.getDate());
    assertNotNull(tripDay.getDisplayedDate());
    assertEquals(someHolidayName, tripDay.getAffectedScheduleReason());
    assertTrue(tripDay.isSchedulePossiblyAffected());
  }

  @Test
  void updateTripPointImageFromCity() {
    final RegionCityExtended extendedCity = mock(RegionCityExtended.class);
    final TripPoint tripPoint = new TripPoint();

    // Default if null
    testService.updateTripPointImageFromCity(tripPoint, null);
    assertEquals(Constants.DEFAULT_IMAGE_PLACEHOLDER, tripPoint.getImageSource());
    // Reset
    tripPoint.setImageSource(null);

    // Default if blank
    testService.updateTripPointImageFromCity(tripPoint, extendedCity);
    assertEquals(Constants.DEFAULT_IMAGE_PLACEHOLDER, tripPoint.getImageSource());
    tripPoint.setImageSource(null);

    // Updated from city
    final Image mockImage = mock(Image.class);
    doReturn(mockImage).when(extendedCity).getImage();
    doReturn("some-image-path").when(mockImage).getS7mobileImageReference();
    testService.updateTripPointImageFromCity(tripPoint, extendedCity);
    // The correct value must be tested in DynamicMediaUtils test class
    assertNotNull(tripPoint.getImageSource());

    // Not changed if not blank
    final String someImage = "some-image";
    tripPoint.setImageSource(someImage);
    testService.updateTripPointImageFromCity(tripPoint, extendedCity);
    assertSame(someImage, tripPoint.getImageSource());
  }

  @Test
  void getTripPlanByPath_empty_path() throws RepositoryException {
    doReturn(stubHolder).when(testService).getFromCacheOrQueryTripPlans(anyString());

    assertNull(testService.getTripPlanByPath(null));
    assertNull(testService.getTripPlanByPath(StringUtils.EMPTY));
    assertNull(testService.getTripPlanByPath(StringUtils.SPACE));
  }

  @Test
  void getTripPlanByPath() throws RepositoryException {
    doReturn(stubHolder).when(testService).getFromCacheOrQueryTripPlans(anyString());

    assertSame(stubTripPlan1, testService.getTripPlanByPath(FULL_PATH_TO_TRIP_PLAN_1));
    assertSame(stubTripPlan2, testService.getTripPlanByPath(FULL_PATH_TO_TRIP_PLAN_2));

    // Test unknown path
    assertNull(testService.getTripPlanByPath("/unknown/path"));
  }

  @Test
  void fromJson() throws JSONException, RepositoryException {
    final String locale = ANY_LANGUAGE;
    final TripPlan expected = mock(TripPlan.class);
    doReturn(expected).when(testService)
        .getPopulatedUserTripPlan(nullable(TripPlan.class), nullable(TripPlan.class), eq(locale));
    final String pathPrefix = Constants.ROOT_CONTENT_PATH + "/" + locale;
    doReturn(stubTripPlan1).when(testService)
        .getTripPlanByPath(eq(pathPrefix + FULL_PATH_TO_TRIP_PLAN_1));
    doReturn(stubTripPlan2).when(testService)
        .getTripPlanByPath(eq(pathPrefix + FULL_PATH_TO_TRIP_PLAN_2));

    // Empty
    assertTrue(testService.fromJson(new JSONArray(), locale).isEmpty());

    // Not empty, but path is blank
    final List<TripPlan> list1 = testService.fromJson(new JSONArray("[{}]"), locale);
    assertEquals(1, list1.size());
    assertSame(expected, list1.get(0));
    // Not empty and path is not blank
    final List<TripPlan> list2 = testService.fromJson(new JSONArray(stubTripPlanList), locale);
    assertEquals(stubTripPlanList.size(), list2.size());
    list2.forEach(tripPlan -> assertSame(expected, tripPlan));
  }


  // getDeserializerGson - Marked as Generated.
  @Test
  void adjustDates_empty_date() {
    final TripPlan tripPlan = new TripPlan();
    final CreateTripPlanFilter filter = new CreateTripPlanFilter();
    testService.adjustDates(tripPlan, filter);

    assertNull(tripPlan.getStartDate());
    assertNull(tripPlan.getEndDate());
    assertNull(filter.getEndDate());
  }

  @Test
  void adjustDates() {
    final LocalDate localDateStartDate = LocalDate.parse("2020-11-20");
    final Calendar startDateCal = CommonUtils.localDateToCalendar(localDateStartDate);

    //
    // English
    //
    final TripPlan tripPlanEn = stubTripPlan1;
    final String expectedStartDisplayDateEn = "Fri 20, Nov";
    final String expectedEndDisplayDateEn = "Sat 21, Nov";
    final CreateTripPlanFilter enFilter =
        CreateTripPlanFilter.builder().startDate(startDateCal).locale(Constants.DEFAULT_LOCALE)
            .build();

    testService.adjustDates(tripPlanEn, enFilter);

    assertEquals(CommonUtils.dateToString(startDateCal), tripPlanEn.getStartDate());
    final LocalDate endDate = CommonUtils.calendarToLocalDate(enFilter.getEndDate());
    assertEquals(CommonUtils.dateToString(endDate), tripPlanEn.getEndDate());
    assertEquals(expectedStartDisplayDateEn, tripPlanEn.getDisplayedStartDate());
    assertEquals(expectedEndDisplayDateEn, tripPlanEn.getDisplayedEndDate());

    //
    // Arabic
    //
    final TripPlan tripPlanAr = stubTripPlan2;
    final String expectedStartDisplayDateAr = "الجمعة ٢٠, نوفمبر";
    final String expectedEndDisplayDateAr = "السبت ٢١, نوفمبر";
    final CreateTripPlanFilter arFilter =
        CreateTripPlanFilter.builder().startDate(startDateCal).locale(Constants.ARABIC_LOCALE)
            .build();

    testService.adjustDates(tripPlanAr, arFilter);

    assertEquals(CommonUtils.dateToString(startDateCal), tripPlanAr.getStartDate());
    assertEquals(CommonUtils.dateToString(CommonUtils.calendarToLocalDate(arFilter.getEndDate())),
        tripPlanAr.getEndDate());
    assertEquals(expectedStartDisplayDateAr, tripPlanAr.getDisplayedStartDate());
    assertEquals(expectedEndDisplayDateAr, tripPlanAr.getDisplayedEndDate());
  }

  @Test
  void addEventsToDay() {
    final String okDate = "2021-01-04T00:00:00.000";
    final String notOkDate = "2021-01-02T00:00:00.000";

    final String[] okCategoriesArray = {"ok-category"};
    final List<String> okCategories = Arrays.asList(okCategoriesArray);
    final List<String> notOkCategories = Collections.singletonList("bad-category");

    final String eventId1 = "some/event/id1";
    final EventDetail mockEvent1 = mock(EventDetail.class);
    when(mockEvent1.getId()).thenReturn(eventId1);
    when(mockEvent1.getCalendarStartDate()).thenReturn(okDate);
    when(mockEvent1.getCalendarEndDate()).thenReturn(okDate);

    final String eventId2 = "some/event/id2";
    final EventDetail mockEvent2 = mock(EventDetail.class);
    when(mockEvent2.getId()).thenReturn(eventId2);
    when(mockEvent2.getCalendarStartDate()).thenReturn(notOkDate);
    when(mockEvent2.getCalendarEndDate()).thenReturn(notOkDate);
    when(mockEvent2.getCategory()).thenReturn(okCategories);

    final String eventId3 = "some/event/id3";
    final EventDetail mockEvent3 = mock(EventDetail.class);
    doReturn(eventId3).when(mockEvent3).getId();
    doReturn(okDate).when(mockEvent3).getCalendarStartDate();
    doReturn(notOkCategories).when(mockEvent3).getCategory();

    final String eventId4 = "some/event/id4";
    final EventDetail mockEvent4 = mock(EventDetail.class);
    doReturn(eventId4).when(mockEvent4).getId();
    doReturn(notOkDate).when(mockEvent4).getCalendarStartDate();

    final String eventId5 = "some/event/id5";
    final EventDetail mockEvent5 = mock(EventDetail.class);
    doReturn(eventId5).when(mockEvent5).getId();
    doReturn(okDate).when(mockEvent5).getCalendarStartDate();
    doReturn(okCategories).when(mockEvent5).getCategory();

    final EventListModel all = new EventListModel();
    all.setData(Arrays.asList(mockEvent1, mockEvent2, mockEvent3, mockEvent4, mockEvent5));
    final EventListModel onlyEvent1 = new EventListModel();
    onlyEvent1.setData(Collections.singletonList(mockEvent1));

    final Set<String> emptySet = Collections.emptySet();
    final Set<String> setWithId1 = new HashSet<>();
    setWithId1.add(eventId1);

    final String[] emptyArray = {};
    final TripDay tripDay = new TripDay();
    final LinkedList<TripPoint> tripDayPoints = tripDay.getTripPoints();

    //
    // Trip day is not changed
    //
    testService.addEventsToDay(tripDay, null, emptySet, emptyArray, okDate);
    assertTrue(CollectionUtils.isEmpty(tripDayPoints));
    testService.addEventsToDay(tripDay, new EventListModel(), setWithId1, emptyArray, okDate);
    assertTrue(CollectionUtils.isEmpty(tripDayPoints));
    // Do not add if already added
    testService.addEventsToDay(tripDay, onlyEvent1, setWithId1, emptyArray, okDate);
    assertTrue(CollectionUtils.isEmpty(tripDayPoints));

    //
    // Adding events as points
    //

    // Checking by date
    doReturn(1).when(testService).getMaxEventsToAddToDay();
    testService.addEventsToDay(tripDay, all, emptySet, emptyArray, okDate);
    // One event with good date are added, the second is skipped
    // due to MAX_EVENTS_TO_ADD_TO_TRIP_PLAN_DAY
    assertEquals(1, tripDayPoints.size());
    final TripPoint firstPointDate = tripDayPoints.getFirst();
    assertEquals(eventId1, firstPointDate.getId());

    // Checking by category
    tripDayPoints.clear();
    testService.addEventsToDay(tripDay, all, emptySet, okCategoriesArray, okDate);
    assertEquals(1, tripDayPoints.size());
    // ok date and ok category
    assertEquals(eventId5, tripDayPoints.getFirst().getId());
  }

  @Test
  void queryTripPlans() throws RepositoryException {
    final Resource mockResource1 = Utils.mockAdaptableResource(TripPlan.class, stubTripPlan1);
    final Resource mockResource2 = Utils.mockAdaptableResource(TripPlan.class, stubTripPlan2);
    Utils.setInternalState(testService, "queryBuilder",
        Utils.mockQueryBuilder(mockResource1, mockResource2));
    doReturn("/some/path/to/trip-plans").when(testService).getPathToTripPlans(anyString());

    // two trip plans are found by method
    final int expectedSize = 2;
    // city1 and city2
    final int expectedCitiesCount = 2;

    final Map<String, String> queryMap = new HashMap<>();
    doReturn(queryMap).when(testService).getQueryMapToSearchTripPlanPages(anyString());

    final ResourceResolver mockResolver = mock(ResourceResolver.class);
    Utils.setInternalState(testService, "resolverService", Utils.spyUserService(mockResolver));

    doReturn(stubFilter).when(testService).getFilterFromResult(any(SearchResult.class), anyMap());

    final TripPlanServiceImpl.TripPlanHolder holder = testService.queryTripPlans(ANY_LANGUAGE);

    final TripPlanFilter filter = holder.getFilter();
    final List<TripPlan> list = holder.getList();
    final Map<String, TripPlan> map = holder.getMap();

    assertEquals(expectedSize, list.size());
    assertEquals(expectedSize, map.size());
    // How many different cities are in trip plans
    assertEquals(expectedCitiesCount, filter.getCityIds().size());

    // List is filled with correct trip plans
    assertEquals(stubTripPlan1, list.get(0));
    assertEquals(stubTripPlan2, list.get(1));

    for (TripPlan resultTripPlan : list) {
      // Map is ok
      assertEquals(map.get(resultTripPlan.getPath()), resultTripPlan);
      // All cities of this trip plan are in the filter
      assertTrue(filter.getCityIds().containsAll(
          resultTripPlan.getData().getCities().stream().map(CityItinerary::getCityId)
              .collect(Collectors.toList())));
    }
  }

  //getQueryMapToSearchTripPlanPages - Marked as generated

}
