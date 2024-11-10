package com.saudi.tourism.core.services.impl;


import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.MemHolder;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.LoginUserService;
import com.saudi.tourism.core.models.common.Image;
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
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.services.CalendarService;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.TripPlanService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.JcrQueryUtils;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.services.impl.TripPlanServiceImpl.TRIP_PLAN_SERVICE_DESCRIPTION;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_FOUR;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_TWO;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_ZERO;
import static com.saudi.tourism.core.utils.NumberConstants.SEVEN;
import static com.saudi.tourism.core.utils.NumberConstants.SIX;
import static com.saudi.tourism.core.utils.NumberConstants.THREE;

/**
 * Service for requesting and working with Trip Plans.
 */
@Component(service = TripPlanService.class,
           immediate = true,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + TRIP_PLAN_SERVICE_DESCRIPTION})
@Slf4j
public class TripPlanServiceImpl implements TripPlanService {

  /**
   * This Service description for OSGi.
   */
  static final String TRIP_PLAN_SERVICE_DESCRIPTION = "Trip Plan Service";

  /**
   * Constant how many events can be added to each Trip Plan day.
   */
  private static final int MAX_EVENTS_TO_ADD_TO_TRIP_PLAN_DAY = 2;

  /**
   * The cache for Trip Plans requests.
   */
  @Reference
  private Cache memCache;

  /**
   * The service for obtaining resource resolver.
   */
  @Reference
  private UserService resolverService;

  /**
   * SlingSettings Service to obtain run modes.
   */
  @Reference
  private SlingSettingsService slingSettingsService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * The service for searching events.
   */
  @Reference
  private EventService eventService;

  /**
   * The service for checking holidays.
   */
  @Reference
  private CalendarService calendarService;

  /**
   * Service to get city by id.
   */
  @Reference
  private RegionCityService citiesService;

  /**
   * Service to get activities by path.
   */
  @Reference
  private ActivityService activityService;

  /**
   * The login user service for storing newly created trip plan into the user's storage.
   * It's not injected using reference to avoid dead lock as the trip plan service exists as a
   * reference in the LoginUserService.
   */
  private LoginUserService loginUserService;

  /**
   * Set of properties that needs to be saved into Login Storage. We need only id & path
   * properties to store data.
   */
  private static final Map<Integer, Set<String>> SAVE_PROPS =
      ImmutableMap.<Integer, Set<String>>builder()
          //@formatter:off
          // 0. Trip plan root
          .put(CONST_ZERO, ImmutableSet.of(Constants.PN_ID, Constants.PATH_PROPERTY,
              Constants.START_DATE, Constants.END_DATE, Constants.PN_TITLE, Constants.CITIES))
          // 1. Level one is "cities" array
          // 2. City itinerary
          .put(CONST_TWO, ImmutableSet.of(TripPlanConstants.CITY_ID, Constants.CITY, "days"))
          // 3. Level three is "days" array or "city" child of city itinerary
          .put(THREE, ImmutableSet.of(Constants.PN_ID))
          // 4. Trip day
          .put(CONST_FOUR, ImmutableSet.of(Constants.PN_ID, TripPlanConstants.SCHEDULE))
          // 5. Level five is "schedule" array
          // 6. Trip point
          .put(SIX, ImmutableSet.of(Constants.PN_ID, Constants.PATH_PROPERTY,
              TripPlanConstants.CITY_ID, Constants.CITY))
          // 7. City in trip point
          .put(SEVEN, ImmutableSet.of(Constants.PN_ID))
          //@formatter:on
          .build();

  /**
   * Trip planner serialization / deserialization gson builder.
   */
  private GsonBuilder gsonBuilder = null;

  /**
   * Returns all possible filters for trip plans. Uses memory cache.
   *
   * @param language the locale
   * @return trip plan filter object
   * @throws RepositoryException if can't get some JCR data from the query
   */
  @Generated
  @Override
  public TripPlanFilter getTripPlanFilter(final String language) throws RepositoryException {
    // Return cloned
    return new TripPlanFilter(getFromCacheOrQueryTripPlans(language).getFilter());
  }

  /**
   * Returns all existing trip plans for the specified locale. Uses memory cache.
   *
   * @param language the current language
   * @return list of all trip plan objects
   * @throws RepositoryException if can't get some JCR data from the query
   */
  @Generated
  @Override
  public List<TripPlan> getTripPlanList(final String language) throws RepositoryException {
    return getFromCacheOrQueryTripPlans(language).getList();
  }

  /**
   * Checks cache, and if the trip plans holder (a list, a map and a filter) is in cache, returns
   * it, otherwise, requests a new one using QueryBuilder ({@link #queryTripPlans(String)}), and
   * stores it into the mem cache.
   *
   * @param language the current locale
   * @return TripPlanHolder instance
   * @throws RepositoryException if couldn't read data from CRX
   */
  @NotNull TripPlanHolder getFromCacheOrQueryTripPlans(final String language)
      throws RepositoryException {
    final String memCacheKey = Constants.KEY_PREFIX_TRIP_PLANS + language;

    TripPlanHolder holder = (TripPlanHolder) memCache.get(memCacheKey);

    if (holder == null) {
      // Get trip plans list using query if couldn't find it in the cache
      holder = queryTripPlans(language);

      if (CollectionUtils.isNotEmpty(holder.getFilter().getCityIds()) || CollectionUtils
          .isNotEmpty(holder.getList())) {
        memCache.add(memCacheKey, holder);
      }
    }

    return holder;
  }

  @Override
  public TripPlan createTripPlan(final CreateTripPlanFilter filter, final UserIDToken user)
      throws RepositoryException {
    // Check if the trip plan should be empty or with data
    final Boolean withData = filter.getWithData();
    if (Boolean.TRUE.equals(withData)) {
      LOGGER.trace("Trip plan with data requested. Filter: {}", filter);
      return prepareTripPlanWithData(filter, user);
    }

    LOGGER.trace("Empty trip plan requested. Filter: {}", filter);
    return createEmptyTripPlan(filter, user);
  }

  /**
   * Prepares trip plan with data, from exact match trip plan page or with a combination of
   * city itineraries from several trip plan pages pages for multi-city request, and populates
   * trip plan with events.
   *
   * @param filter trip plan filter
   * @param user   user data to store trip plan into user storage
   * @return prepared trip plan
   * @throws RepositoryException if could not read data from CRX
   */
  TripPlan prepareTripPlanWithData(final CreateTripPlanFilter filter, final UserIDToken user)
      throws RepositoryException {
    final List<TripPlan> allTripPlans = getTripPlanList(filter.getLocale());

    // 1. Filter by exact match
    LOGGER.trace("Trying to look for existing trip plan for the requested cities list. Filter: {}",
        filter);
    TripPlan newTripPlan = checkExactMatch(filter, allTripPlans);

    // 2. If not found - combine from several city itineraries into one
    if (newTripPlan == null) {
      LOGGER.debug("No exact matched trip plans found, trying to combine city itineraries");
      newTripPlan = combineCityItineraries(filter, allTripPlans);
    }

    // Adjust start/end dates and populate the trip plan with events
    final TripPlan fullTripPlanWithEvents =
        this.populateTripPlan(newTripPlan, filter, Objects.nonNull(filter.getStartDate()));
    savePreparedTripPlan(fullTripPlanWithEvents, user);

    return fullTripPlanWithEvents;
  }

  /**
   * Method sends trip plan to login storage for saving, if the user token parameter is not null.
   * Method updates trip plan id with the saved one.
   *
   * @param tripPlanToSave trip plan to save
   * @param user           user data to save to login storage
   */
  void savePreparedTripPlan(final TripPlan tripPlanToSave, final UserIDToken user) {
    // Try to store new trip plan to login storage
    if (user != null && !StringUtils.isAnyBlank(user.getToken(), user.getUser())) {
      final String tripPlanJsonToSave = prepareSaveTripPlanJson(tripPlanToSave);

      try {
        final String tripPlanId = getLoginUserService().saveTripPlan(tripPlanJsonToSave, user);
        LOGGER.debug("Trip plan was saved into User Storage with id {}", tripPlanId);
        tripPlanToSave.setId(tripPlanId);

      } catch (IOException e) {
        LOGGER.error("I/O error while storing trip plan", e);
        throw new IllegalStateException("Trip plan can not be saved into User Storage", e);
      }
    }
  }

  /**
   * Create, save and return empty trip plan.
   *
   * @param filter trip plan filter
   * @param user   user data for saving into user login storage
   * @return prepared empty trip plan
   */
  @Generated
  @NotNull TripPlan createEmptyTripPlan(final CreateTripPlanFilter filter, final UserIDToken user) {
    final TripPlan emptyTripPlan = prepareEmptyTripPlan(filter);

    // Try to store new trip plan to login storage
    savePreparedTripPlan(emptyTripPlan, user);

    return emptyTripPlan;
  }

  /**
   * Combine city itineraries from multiple trip plans to match cities list from the user's request.
   *
   * @param filter       filter to check
   * @param allTripPlans list of all authored trip plans to search itineraries
   * @return combined trip plan for cities from the request
   */
  @NotNull TripPlan combineCityItineraries(final CreateTripPlanFilter filter,
      final List<TripPlan> allTripPlans) {
    TripPlan newTripPlan;

    final List<String> cityIds = filter.getCity();
    final List<CityItinerary> allMatchedItineraries = extractCityItineraries(allTripPlans, cityIds);

    // No matching multi-city trip plans were found, try to combine several itineraries
    newTripPlan = new TripPlan();
    final TripDetail tripDetail = newTripPlan.getData();

    if (CollectionUtils.isNotEmpty(cityIds)) {
      for (String cityId : cityIds) {
        final Stream<CityItinerary> itineraries =
            extractCityItineraries(allMatchedItineraries, cityId);
        // Search and get the correct element with minimal distance (without sorting to be faster)
        final Optional<CityItinerary> firstItinerary =
            itineraries.min(Comparator.comparingInt(this::calculateDaysDistance));
        if (!firstItinerary.isPresent()) {
          LOGGER.error("Not found itineraries for city id: {}, please check filter data", cityId);
          continue;
        }

        final CityItinerary chosen = firstItinerary.get();
        LOGGER.debug("Selected nearest itinerary for the city {}: {}", cityId, chosen);
        tripDetail.getCities().add(chosen);
      }
    }

    if (CollectionUtils.isEmpty(tripDetail.getCities())) {
      // Actually, the situation when we don't have single city trip plans for requested cities
      // can not happen so the error can be thrown here
      final String errorMessage =
          "Not found itineraries for filter " + filter.toString() + ". Please check the request.";
      LOGGER.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    return newTripPlan;
  }

  /**
   * Check existing trip plans for the provided list of city ids and return the best authored
   * trip plan for the same cities list, the nearest one by preferred trip days.
   *
   * @param filter       filter to check
   * @param allTripPlans list of all existing trip plans to search in
   * @return the best matching trip plan with the same cities or null
   */
  TripPlan checkExactMatch(@NotNull final CreateTripPlanFilter filter,
      @NotNull final List<TripPlan> allTripPlans) {
    TripPlan newTripPlan = null;

    final Optional<TripPlan> exactMatch =
        allTripPlans.stream().filter(tripPlan -> matchFilters(tripPlan, filter))
            .min(Comparator.comparingInt(this::calculateDaysDistance));
    if (exactMatch.isPresent()) {
      newTripPlan = exactMatch.get();
      LOGGER.debug("Found exact match trip plan (returning the best by preferred days) {}",
          newTripPlan);
    }
    return newTripPlan;
  }

  /**
   * Returns preferred trip days count for a city by id.
   *
   * @param cityId city id to get
   * @return preferred trip days count for the city or
   * {@link TripPlanConstants#UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT} if the city wasn't found
   */
  Long getCityPreferredTripDaysCount(final String cityId) {
    return Optional.ofNullable(citiesService.getRegionCityExtById(cityId, Constants.DEFAULT_LOCALE))
        .map(RegionCityExtended::getPreferredTripDaysCount)
        .orElse(TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT);
  }

  /**
   * Returns days distance for this trip plan (sums up days distances for city itineraries).
   *
   * @param tripPlan trip plan to get total days distance
   * @return sum of days distances for all included cities or
   * {@link TripPlanConstants#UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT} if can not be calculated
   */
  int calculateDaysDistance(@NotNull final TripPlan tripPlan) {
    return Optional.ofNullable(tripPlan.getData()).map(this::calculateDaysDistance)
        .orElseGet(TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT::intValue);
  }

  /**
   * Returns days distance for this trip plan (sums up days distances for city itineraries).
   *
   * @param tripDetail trip detail instance to get the list of city itineraries
   * @return sum of days distances for all included cities or
   * {@link TripPlanConstants#UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT} if can not be calculated
   */
  int calculateDaysDistance(final @NotNull TripDetail tripDetail) {
    final List<CityItinerary> itineraries = tripDetail.getCities();
    if (CollectionUtils.isEmpty(itineraries)) {
      return TripPlanConstants.UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT.intValue();
    }

    return itineraries.stream().map(this::calculateDaysDistance).reduce(0, Integer::sum);
  }

  /**
   * Distance in days between the preferred count of days for this city and pre-planned.
   * It's used for getting the correct itinerary for city when combining itineraries to one trip
   * plan according to city's preferred trip days count.
   *
   * @param itinerary city itinerary to calculate days distance
   * @return absolute value of the difference between dates
   */
  Integer calculateDaysDistance(@NotNull final CityItinerary itinerary) {
    return Math.toIntExact(
        Math.abs(itinerary.getDaysCount() - getCityPreferredTripDaysCount(itinerary.getCityId())));
  }

  /**
   * The function prepares the empty trip plan for user (with cities list if it was provided in
   * the filter).
   *
   * @param filter filter to process
   * @return new empty trip plan instance
   */
  @NotNull TripPlan prepareEmptyTripPlan(final CreateTripPlanFilter filter) {

    final TripPlan emptyTripPlan = new TripPlan();

    // Update cities if they are in the request
    final List<CityItinerary> cityItineraries = emptyTripPlan.getData().getCities();
    final List<String> filterCityIds = filter.getCity();
    if (CollectionUtils.isNotEmpty(filterCityIds)) {
      for (String cityId : filterCityIds) {
        cityItineraries
            .add(new CityItinerary(citiesService.getRegionCityById(cityId, filter.getLocale())));
      }
    }

    adjustDates(emptyTripPlan, filter);

    // Update title from filter
    final String filterTitle = filter.getTitle();
    if (StringUtils.isNotBlank(filterTitle)) {
      emptyTripPlan.setTitle(filterTitle);
    }

    return emptyTripPlan;
  }

  /**
   * Returns list of itineraries for a specific city from the all itineraries list, sorted by
   * days distance.
   *
   * @param cityItineraries all city itineraries
   * @param city            city id to filter
   * @return sorted list of itineraries for provided city param
   */
  Stream<CityItinerary> extractCityItineraries(final List<CityItinerary> cityItineraries,
      final String city) {
    // Just in case if was requested incorrectly
    final String cityId = AppUtils.stringToID(city);
    if (StringUtils.isBlank(cityId)) {
      return Stream.empty();
    }

    return cityItineraries.stream()
        .filter(cityItinerary -> cityItinerary.getCityId().equalsIgnoreCase(cityId));
  }

  /**
   * Returns all city itineraries for the provided.
   *
   * @param tripPlans list of all trip plans for language
   * @param cities    list of all city ids to extract itineraries
   * @return list of all matching city itineraries
   */
  List<CityItinerary> extractCityItineraries(final List<TripPlan> tripPlans, List<String> cities) {
    if (CollectionUtils.isEmpty(tripPlans) || CollectionUtils.isEmpty(cities)) {
      return Collections.emptyList();
    }

    return tripPlans.stream().flatMap(
        tripPlan -> Optional.ofNullable(tripPlan.getData()).map(TripDetail::getCities)
            .map(List::stream).orElse(null))
        // @formatter:off
        .filter(Objects::nonNull).filter(itinerary -> {
        // @formatter:on
          final String cityId = itinerary.getCityId();
          return StringUtils.isNotBlank(cityId) && cities.contains(cityId);
        }).collect(Collectors.toList());
  }

  /**
   * Getter for LoginUserService (initializes the field).
   *
   * @return login user service
   */
  @Generated
  LoginUserService getLoginUserService() {
    if (loginUserService == null) {
      loginUserService = CommonUtils.getService(LoginUserService.class);
    }

    return loginUserService;
  }

  /**
   * This method prepares json of the trip plan to be saved into user login storage.
   *
   * @param tripPlan trip plan to save
   * @return json with properties that are obligatory for saving
   */
  @Generated
  String prepareSaveTripPlanJson(final TripPlan tripPlan) {
    final ObjectMapper objectMapper = RestHelper.getObjectMapper();
    final JsonNode originalJsonNode = objectMapper.valueToTree(tripPlan);
    final JsonNode preparedJsonNode = traverseFilter(originalJsonNode, 0);
    if (preparedJsonNode == null) {
      throw new IllegalStateException("Error in preparing json for saving created trip plan");
    }
    return preparedJsonNode.toString();
  }

  /**
   * This method iterates json properties and returns only properties which name exists in the
   * provided set.
   *
   * @param source source json node to process
   * @param level  current processing level
   * @return new processed json node
   */
  static JsonNode traverseFilter(JsonNode source, final Integer level) {
    if (source.isObject()) {
      return processSourceAsObject(source, level);
    } else if (source.isArray()) {
      return processSourceAsArray(source, level);
    } else {
      // Source is simple value - just return it
      if (!source.isNull()) {
        return source;
      }
    }
    return null;
  }

  /**
   * Process source as object.
   * @param source source
   * @param level level
   * @return JsonNode
   */
  private static JsonNode processSourceAsObject(JsonNode source, final Integer level) {
    if (!SAVE_PROPS.containsKey(level)) {
      return null;
    }

    final Set<String> neededKeys = SAVE_PROPS.get(level);

    final ObjectNode result = RestHelper.getObjectMapper().createObjectNode();
    boolean hasChildren = false;

    // Iterate object fields and add to result
    final Iterator<String> fieldNames = source.fieldNames();
    while (fieldNames.hasNext()) {
      final String fieldName = fieldNames.next();
      final JsonNode fieldValue = source.get(fieldName);

      if (neededKeys.contains(fieldName) || fieldValue.isObject() || fieldValue.isArray()) {
        final JsonNode processedChildNode = traverseFilter(fieldValue, level + 1);
        if (processedChildNode != null) {
          result.set(fieldName, processedChildNode);
          hasChildren = true;
        }
      }
    }

    if (hasChildren) {
      return result;
    }
    return null;
  }

  /**
   * Process source as array.
   * @param source source
   * @param level level
   * @return JsonNode
   */
  private static JsonNode processSourceAsArray(JsonNode source, final Integer level) {
    final ArrayNode arrayNode = (ArrayNode) source;
    final int arraySize = arrayNode.size();
    if (arraySize > 0) {
      boolean hasElements = false;
      final ArrayNode result = RestHelper.getObjectMapper().createArrayNode();

      // Iterate array elements and add each to result
      for (int i = 0; i < arraySize; i++) {
        JsonNode arrayElement = arrayNode.get(i);

        if (arrayElement.isObject() || arrayElement.isArray()) {
          final JsonNode processedArrayElement = traverseFilter(arrayElement, level + 1);
          if (processedArrayElement != null) {
            result.add(processedArrayElement);
            hasElements = true;
          }
        }
      }

      if (hasElements) {
        return result;
      }
    }
    return null;
  }

  /**
   * Check if one TripPlan matches the desired filter.
   *
   * @param tripPlan trip plan instance
   * @param filter   selected trip plan filters
   * @return the boolean
   */
  boolean matchFilters(@NotNull final TripPlan tripPlan,
      @NotNull final CreateTripPlanFilter filter) {

    // Check the path param
    final String filterTripPlanPath = filter.getPath();
    if (StringUtils.isNotBlank(filterTripPlanPath)) {
      return filterTripPlanPath.equals(tripPlan.getPath());
      // Don't check other parameters if we're looking for trip plan by its path
    }

    // Check filter cities list
    final List<String> filterCities = filter.getCity();
    if (CollectionUtils.isNotEmpty(filterCities)) {
      final TripDetail tripDetail = tripPlan.getData();
      if (tripDetail == null) {
        return false;
      }
      final List<CityItinerary> tripPlanCities = tripDetail.getCities();
      return tripPlanCities.size() == filterCities.size() && tripPlanCities.stream()
          .map(CityItinerary::getCityId).filter(StringUtils::isNotBlank)
          .allMatch(filterCities::contains);
    }

    return false;
  }

  /**
   * Returns path to the root trip plan page from admin options.
   *
   * @param language language to get proper path
   * @return path to all trip plans root page
   */
  @Generated
  @NotNull
  public String getPathToTripPlans(final String language) {
    return AdminUtil.getAdminOptions(language, StringUtils.EMPTY).getTripPlansPath();
  }

  /**
   * Fills trip plan additional data, limits days according to the dates.
   *
   * @param foundTripPlan original trip plan
   * @param filter        filter instance for filling proper data
   * @param addEvents     {@code true} to add events from event service
   * @return Trip Plan object
   */
  TripPlan populateTripPlan(@NotNull final TripPlan foundTripPlan,
      @NotNull final CreateTripPlanFilter filter, boolean addEvents) {

    // Clone trip plan to not break existing cached object
    final TripPlan tripPlan = new TripPlan(foundTripPlan);

    adjustDates(tripPlan, filter);

    // Update title from filter
    final String filterTitle = filter.getTitle();
    if (StringUtils.isNotBlank(filterTitle)) {
      tripPlan.setTitle(filterTitle);
    }

    final TripDetail tripDetail = tripPlan.getData();
    if (tripDetail == null) {
      // No days found for this trip plan, return empty
      return tripPlan;
    }

    populateTripPlanCities(filter, tripDetail, addEvents);
    return tripPlan;
  }

  /**
   * Process cities.
   * @param filter      filter instance for filling proper data
   * @param tripDetail  tripDetail
   * @param addEvents   {@code true} to add events from event service
   */
  private void populateTripPlanCities(final CreateTripPlanFilter filter,
      final TripDetail tripDetail, final boolean addEvents) {
    final String language = filter.getLocale();
    for (CityItinerary cityItinerary : tripDetail.getCities()) {
      final String cityId = cityItinerary.getCityId();
      final List<TripDay> days = cityItinerary.getDays();

      // Request events from EventService to add to this trip plan
      EventListModel filteredEventsList = null;
      final Set<String> alreadyAddedEvents = new HashSet<>();
      if (addEvents) {
        filteredEventsList = getFilteredEvents(filter, cityId, days);
      }

      // The number of current day, iterates from 0 to `days - 1`.
      int dayNum = 0;
      final LocalDate startDate = CommonUtils.calendarToLocalDate(filter.getStartDate());
      if (Objects.isNull(startDate)) {
        return;
      }
      for (TripDay day : days) {
        final LocalDate thisDayLocalDate = startDate.plusDays(dayNum);
        final String thisDayDate = CommonUtils.dateToString(thisDayLocalDate);
        day.setDate(thisDayDate);

        String displayDate =
            CommonUtils.dateToString(thisDayLocalDate, Constants.FMT_WD_D_M, language);
        if (Constants.ARABIC_LOCALE.equals(language)) {
          displayDate = CommonUtils.getArabicNumeralChar(displayDate);
        }
        day.setDisplayedDate(displayDate);

        // Update holiday for this day
        day.setHoliday(
            calendarService.getHolidayNameByDate(thisDayLocalDate, filter.getLocale()));

        if (addEvents) {
          addEventsToDay(day, filteredEventsList, alreadyAddedEvents, day.getEventCategories(),
              thisDayDate);
        }

        // Go to next day
        dayNum++;
      }
    }
  }

  /**
   * Get FilteredEvents.
   * @param filter filter
   * @param cityId cityId
   * @param days days
   * @return EventListModel
   */
  private EventListModel getFilteredEvents(final CreateTripPlanFilter filter, final String cityId,
      final List<TripDay> days) {
    EventListModel filteredEventsList = null;
    final EventsRequestParams eventFilter = prepareEventFilter(filter, cityId, days.size());
    try {
      filteredEventsList = eventService.getFilteredEvents(eventFilter);
    } catch (RepositoryException e) {
      LOGGER.error("Couldn't read events data from JCR", e);
    }
    return filteredEventsList;
  }

  @Override
  public TripPlan getPopulatedUserTripPlan(@NotNull final TripPlan storedTripPlan,
      @Nullable final TripPlan jcrTripPlan, final String language) {
    final TripPlan resultTripPlan = createTripPlan(storedTripPlan, jcrTripPlan);

    final TripDetail resultTripDetail = resultTripPlan.getData();
    final List<CityItinerary> resultItineraries = resultTripDetail.getCities();
    resultItineraries.clear();

    final TripDetail storedTripDetail = storedTripPlan.getData();
    if (storedTripDetail == null || CollectionUtils.isEmpty(storedTripDetail.getCities())) {
      // No cities found for this trip plan, stop processing and return empty Trip Plan object
      return resultTripPlan;
    }

    try (ResourceResolver resolver = resolverService.getResourceResolver()) {
      LocalDate startDate;
      final String storedStartDate = storedTripPlan.getStartDate();
      if (StringUtils.isNotBlank(storedStartDate)) {
        try {
          startDate = CommonUtils.dateToLocalDate(storedStartDate);
        } catch (Exception e) {
          LOGGER.error("Could not parse startDate for saved Trip Plan. This trip plan will be "
              + "ignored and won't be displayed on trip overview page.", e);
          return null;
        }
      } else {
        LOGGER.error("Stored Trip Plan doesn't have required start date field. Skipping it.");
        return null;
      }

      int totalDaysCount = 0;
      for (CityItinerary itinerary : storedTripDetail.getCities()) {
        totalDaysCount = processCityItinerary(itinerary, language, resultTripPlan,
            resultItineraries, resolver, startDate, totalDaysCount);
      }

      // Total days count shouldn't be empty
      if (totalDaysCount == 0) {
        totalDaysCount = 1;
      }
      // check for empty trip
      if (resultTripPlan.getDaysCount() == 0) {
        resultTripPlan.setDaysCount(totalDaysCount);
      }

      final LocalDate endDate = startDate.plusDays(totalDaysCount);
      resultTripPlan.setEndDate(CommonUtils.dateToString(endDate));

      // Update display date format (to be "Tue 28, Aug")
      String displayedStartDate =
          CommonUtils.dateToString(startDate, Constants.FMT_WD_D_M, language);
      String displayedEndDate = CommonUtils.dateToString(endDate, Constants.FMT_WD_D_M, language);
      if (Constants.ARABIC_LOCALE.equals(language)) {
        displayedStartDate = CommonUtils.getArabicNumeralChar(displayedStartDate);
        displayedEndDate = CommonUtils.getArabicNumeralChar(displayedEndDate);
      }
      resultTripPlan.setDisplayedStartDate(displayedStartDate);
      resultTripPlan.setDisplayedEndDate(displayedEndDate);
    }

    // Images shouldn't be empty
    if (StringUtils.isBlank(resultTripPlan.getDesktopImage())) {
      resultTripPlan.setDesktopImage(Constants.DEFAULT_IMAGE_PLACEHOLDER);
    }
    if (StringUtils.isBlank(resultTripPlan.getMobileImage())) {
      resultTripPlan.setDesktopImage(Constants.DEFAULT_IMAGE_PLACEHOLDER);
    }

    return resultTripPlan;
  }

  /**
   * Process itinerary.
   * @param itinerary itinerary
   * @param language language
   * @param resultTripPlan resultTripPlan
   * @param resultItineraries resultItineraries
   * @param resolver resolver
   * @param startDate startDate
   * @param totalDaysCount totalDaysCount
   * @return Days Count
   */
  private int processCityItinerary(final CityItinerary itinerary, final String language,
      final TripPlan resultTripPlan, final List<CityItinerary> resultItineraries,
      ResourceResolver resolver, LocalDate startDate, int totalDaysCount) {
    final String storedCityId = itinerary.getCityId();
    final CityItinerary resultCityItinerary;

    RegionCityExtended extendedCityInfo = null;
    if (StringUtils.isNotBlank(storedCityId)) {
      extendedCityInfo = citiesService.getRegionCityExtById(storedCityId, language);

      if (extendedCityInfo != null) {
        // Update trip plan images from the city if they haven't been updated previously
        initTripPlanImageByCity(resultTripPlan, extendedCityInfo);
        resultCityItinerary = new CityItinerary(extendedCityInfo);
      } else {
        // Workaround if data was stored incorrectly or city id was changed
        resultCityItinerary = new CityItinerary(storedCityId);
      }
    } else {
      resultCityItinerary = new CityItinerary("unknown");
    }

    resultItineraries.add(resultCityItinerary);
    final List<TripDay> resultDays = resultCityItinerary.getDays();

    final List<TripDay> storedDays = itinerary.getDays();

    if (CollectionUtils.isEmpty(storedDays)) {
      totalDaysCount++;

      // New day
      final TripDay newTripDay =
          prepareAndAddNewTripDay(resultDays, language, resolver, extendedCityInfo, null);
      updateNewDayDates(newTripDay, language, startDate, totalDaysCount);

    } else {
      // Iterate and process all trip points for each day
      for (TripDay storedDay : storedDays) {
        totalDaysCount++;

        final TripDay newTripDay =
            prepareAndAddNewTripDay(resultDays, language, resolver, extendedCityInfo,
                storedDay);
        updateNewDayDates(newTripDay, language, startDate, totalDaysCount);
      }
    }
    return totalDaysCount;
  }

  /**
   * init trip plan image by city.
   * @param tripPlan tripPlan
   * @param extendedCityInfo extendedCityInfo
   */
  private void initTripPlanImageByCity(final TripPlan tripPlan,
      final RegionCityExtended extendedCityInfo) {
    final Image cityImage = extendedCityInfo.getImage();

    if (StringUtils
        .isAnyBlank(tripPlan.getDesktopImage(), tripPlan.getMobileImage())
        && cityImage != null) {

      // Store the hero sized image into trip plan
      DynamicMediaUtils.prepareDMImages(cityImage, DynamicMediaUtils.DM_CROP_1920_1080,
          DynamicMediaUtils.DM_CROP_375_667, DynamicMediaUtils.isCnServer(slingSettingsService));

      if (StringUtils.isBlank(tripPlan.getDesktopImage())) {
        tripPlan.setDesktopImage(cityImage.getDesktopImage());
      }
      if (StringUtils.isBlank(tripPlan.getMobileImage())) {
        tripPlan.setMobileImage(cityImage.getMobileImage());
      }
    }
  }

  /**
   * Create trip plan object.
   * @param storedTripPlan storedTripPlan
   * @param jcrTripPlan jcrTripPlan
   * @return new trip plan
   */
  private TripPlan createTripPlan(@NotNull final TripPlan storedTripPlan,
      @Nullable final TripPlan jcrTripPlan) {
    final TripPlan resultTripPlan;
    if (jcrTripPlan == null) {
      // Reconstruct trip plan as the trip plan page wasn't found
      resultTripPlan = new TripPlan(storedTripPlan);

    } else {
      // Get data from JCR
      resultTripPlan = new TripPlan(jcrTripPlan);

      // Update fields from saved trip plan data to the populated one
      resultTripPlan.setId(storedTripPlan.getId());
      final String title = storedTripPlan.getTitle();
      if (StringUtils.isNotBlank(title)) {
        resultTripPlan.setTitle(title);
      }
      resultTripPlan.setStartDate(storedTripPlan.getStartDate());
      resultTripPlan.setEndDate(storedTripPlan.getEndDate());
    }
    return resultTripPlan;
  }

  /**
   * Creates and adds a new trip day into the provided list of days (city itinerary).
   *
   * @param resultDaysList    days list to update
   * @param language          the current locale
   * @param resolver          current resource resolver to request trip point pages for this day
   * @param cityExtended      city info to update point image (if we couldn't find point image)
   * @param storedOriginalDay original day from user storage to get data, or null when empty day
   *                          is created
   * @return new added day
   */
  protected TripDay prepareAndAddNewTripDay(final List<TripDay> resultDaysList,
      final String language, final ResourceResolver resolver, final RegionCityExtended cityExtended,
      final TripDay storedOriginalDay) {

    // New day
    final TripDay resultDay = new TripDay();
    resultDaysList.add(resultDay);

    // If id is empty, re-create it. For the trip-detail component.
    // TODO Check this with FE guys
    if (StringUtils.isBlank(resultDay.getId())) {
      resultDay.setId("day" + resultDaysList.size());
    }

    // Iterate stored schedule, stored items contain only paths, so re-create them from crx,
    // then add to the result schedule
    final LinkedList<TripPoint> resultDayPoints = resultDay.getTripPoints();
    if (storedOriginalDay != null) {
      final Set<String> alreadyAddedPointIds = new HashSet<>();
      //@formatter:off
      storedOriginalDay.getTripPoints().stream().map(
          // If the path wasn't stored, try to get that from activity id
          storedPoint -> StringUtils.defaultIfBlank(storedPoint.getPath(), storedPoint.getId()))
          .filter(StringUtils::isNotBlank).map(pointIdOrPath -> {
            try {
              return TripPoint.createForPath(pointIdOrPath, language, resolver, activityService,
                  slingSettingsService);
            } catch (Exception e) {
              LOGGER.error("Error while adding Trip Plan day point {}. The point will be ignored.",
                  pointIdOrPath, e);
              return null;
            }
          }).filter(Objects::nonNull).forEach(tripPoint -> {
            final String tripPointId = tripPoint.getId();
            // Do not add same trip points into the same day
            if (!alreadyAddedPointIds.contains(tripPointId)) {
              // Trip point image shouldn't be empty - update it from city, if we got city
              updateTripPointImageFromCity(tripPoint, cityExtended);

              resultDayPoints.add(tripPoint);
              alreadyAddedPointIds.add(tripPointId);
            }
          });
      //@formatter:on
    }

    return resultDay;
  }

  /**
   * Updates date / displayDate for a new trip day.
   *
   * @param newTripDay        trip day to update
   * @param language          language for getting display date format
   * @param tripPlanStartDate the start date for the current trip plan
   * @param totalDaysCount    total amount of days to calculate current days's date
   */
  void updateNewDayDates(final TripDay newTripDay, final String language,
      final LocalDate tripPlanStartDate, final int totalDaysCount) {
    final LocalDate thisDayLocalDate = tripPlanStartDate.plusDays(totalDaysCount - 1L);
    newTripDay.setDate(CommonUtils.dateToString(thisDayLocalDate));

    String displayDate = CommonUtils.dateToString(thisDayLocalDate, Constants.FMT_WD_D_M, language);
    if (Constants.ARABIC_LOCALE.equals(language)) {
      displayDate = CommonUtils.getArabicNumeralChar(displayDate);
    }
    newTripDay.setDisplayedDate(displayDate);

    newTripDay.setHoliday(calendarService.getHolidayNameByDate(thisDayLocalDate, language));
  }

  /**
   * Update trip point image from the city provided or use default image.
   *
   * @param tripPoint        trip point instance to update
   * @param extendedCityInfo city instance to get image
   */
  void updateTripPointImageFromCity(final TripPoint tripPoint,
      final RegionCityExtended extendedCityInfo) {
    if (!StringUtils.isBlank(tripPoint.getImageSource())) {
      return;
    }

    if (extendedCityInfo != null) {
      final Image cityImage = extendedCityInfo.getImage();
      if (cityImage != null) {
        tripPoint.setImageSource(DynamicMediaUtils
            .getScene7ImageWithDefaultImage(cityImage.getS7fileReference(),
                DynamicMediaUtils.DM_CROP_667_375,
                DynamicMediaUtils.isCnServer(slingSettingsService)));
      }
    }

    // Image shouldn't be empty
    if (StringUtils.isBlank(tripPoint.getImageSource())) {
      tripPoint.setImageSource(Constants.DEFAULT_IMAGE_PLACEHOLDER);
    }
  }

  @Override
  public TripPlan getTripPlanByPath(final String path) throws RepositoryException {
    final String language = CommonUtils.getLanguageForPath(path);
    final TripPlanHolder holder = getFromCacheOrQueryTripPlans(language);

    final TripPlan result = holder.getMap().get(path);
    if (result == null) {
      LOGGER
          .warn("Couldn't find trip plan by path {}. Check if the page was removed / unpublished.",
              path);
    } else {
      LOGGER.debug("Found trip plan by path {}", result);
    }

    return result;
  }

  @Override
  public List<TripPlan> fromJson(@NotNull final JSONArray metadataTripsArray,
      final String language) {
    final String tripsArrayJson = metadataTripsArray.toString();
    final Gson deserializer = getDeserializerGson();
    final TripPlan[] parsedArray = deserializer.fromJson(tripsArrayJson, TripPlan[].class);

    return Stream.of(parsedArray).map(storedTripPlan -> {
      TripPlan originalTripPlan = null;

      final String tripPlanPagePath =
          CommonUtils.toAbsolutePath(storedTripPlan.getPath(), language);

      if (StringUtils.isBlank(tripPlanPagePath)) {
        LOGGER.debug("Path property is not found in the stored trip plan {}, trying to "
            + "re-construct data", storedTripPlan);

      } else {
        try {
          LOGGER.debug("Trying to get Trip Plan by path {}", tripPlanPagePath);
          originalTripPlan = getTripPlanByPath(tripPlanPagePath);
        } catch (RepositoryException e) {
          LOGGER.error("Couldn't query JCR data", e);
        }

        if (originalTripPlan == null) {
          LOGGER.error("Couldn't find Trip Itinerary page by path {}, the trip plan data will"
              + " be re-constructed from stored trip plan only", tripPlanPagePath);
        }
      }

      return getPopulatedUserTripPlan(storedTripPlan, originalTripPlan, language);
    }).filter(Objects::nonNull).collect(Collectors.toList());

  }

  /**
   * Prepares and returns Gson object for trip plans deserialization.
   *
   * @return configured
   */
  @Generated
  private Gson getDeserializerGson() {
    if (gsonBuilder == null) {
      gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(TripPlan.class, new TripPlanDeserializer());
    }

    return this.gsonBuilder.create();
  }

  /**
   * Updates start/end dates of the result trip plan and filter for the proper output.
   *
   * @param tripPlan trip plan instance to update
   * @param filter   the current search filter object
   */
  @Override
  public void adjustDates(final TripPlan tripPlan, final CreateTripPlanFilter filter) {
    final LocalDate startDate = CommonUtils.calendarToLocalDate(filter.getStartDate());
    if (startDate == null) {
      // No start date in filter - can't adjust dates
      return;
    }

    LocalDate endDate = CommonUtils.calendarToLocalDate(filter.getEndDate());

    Integer daysCount = tripPlan.getDaysCount();
    // Just in case if there is no data in JCR
    if (daysCount == 0) {
      if (endDate != null) {
        // Calculate by end date
        daysCount = CommonUtils.getDaysCount(startDate, endDate);
      } else if (Objects.nonNull(filter) && Objects.nonNull(filter.getCity()) && !filter.getCity()
          .isEmpty()) {
        // during creation of new trip end date is not present; so daysCount == No. of cities
        daysCount = filter.getCity().size();
      } else {
        // Workaround - return one day trip plan
        daysCount = 1;
      }
    }

    endDate = startDate.plusDays(daysCount);
    tripPlan.setStartDate(CommonUtils.dateToString(startDate));
    tripPlan.setEndDate(CommonUtils.dateToString(endDate));
    tripPlan.setDaysCount(daysCount);

    // Update display date format (to be "Tue 28, Aug")
    final String language = filter.getLocale();
    String displayedStartDate = CommonUtils.dateToString(startDate, Constants.FMT_WD_D_M, language);
    String displayedEndDate = CommonUtils.dateToString(endDate, Constants.FMT_WD_D_M, language);
    if (Constants.ARABIC_LOCALE.equals(language)) {
      displayedStartDate = CommonUtils.getArabicNumeralChar(displayedStartDate);
      displayedEndDate = CommonUtils.getArabicNumeralChar(displayedEndDate);
    }
    tripPlan.setDisplayedStartDate(displayedStartDate);
    tripPlan.setDisplayedEndDate(displayedEndDate);

    // Update filter with a new date (for events searching).
    filter.setEndDate(CommonUtils.localDateToCalendar(endDate));
  }

  /**
   * Method adds events to the specified day.
   *
   * @param day                trip day instance to add events
   * @param filteredEventsList filtered events list from event service
   * @param alreadyAddedEvents set to check if event was already added to this trip plan
   * @param allowedCategories  array of allowed categories to add to this day
   * @param thisDayDate        the current day date to filter events
   */
  void addEventsToDay(final TripDay day, final EventListModel filteredEventsList,
      final Set<String> alreadyAddedEvents, final String[] allowedCategories,
      final String thisDayDate) {

    if (filteredEventsList == null || CollectionUtils.isEmpty(filteredEventsList.getData())) {
      return;
    }

    // Filter events to match this day
    final Stream<EventDetail> eventsToAddToThisDay =
        filteredEventsList.getData().stream().filter(event -> {
          final String eventId = event.getId();

          // Filter already added events
          if (alreadyAddedEvents.contains(eventId)) {
            return false;
          }

          // Filter events by date
          if (!CommonUtils
              .isDateBetweenStartEnd(thisDayDate, thisDayDate, event.getCalendarStartDate(),
                  event.getCalendarEndDate())) {
            return false;
          }

          // Filter events by allowed categories for this day
          final List<String> eventCategories = event.getCategory();
          return ArrayUtils.isEmpty(allowedCategories)
              || CollectionUtils.isNotEmpty(eventCategories) && Arrays.stream(allowedCategories)
              .anyMatch(eventCategories::contains);

        }).limit(getMaxEventsToAddToDay()); // Add only necessary amount of events

    // Add events to this day from filtered events
    eventsToAddToThisDay.forEach(event -> {
      try {
        final TripPoint eventPoint = new TripPoint(event, slingSettingsService);

        // Add to the end of the day - after activities
        day.getTripPoints().add(eventPoint);

        alreadyAddedEvents.add(event.getId());
      } catch (Exception e) {
        LOGGER.error("Error when adding event {} to trip plan", event, e);
      }
    });
  }

  /**
   * Creates filter for EventService according to this Trip Plan and provided filter.
   *
   * @param filter    the current filter for trip plans searching
   * @param cityId    id of the city to look for events
   * @param daysCount count of days for this city
   * @return EventsRequestParams object
   */
  @NotNull
  private EventsRequestParams prepareEventFilter(final @NotNull CreateTripPlanFilter filter,
      final String cityId, final int daysCount) {
    final EventsRequestParams eventFilter = new EventsRequestParams();
    eventFilter.setLocale(filter.getLocale());
    eventFilter.setLimit(getMaxEventsToAddToDay() * daysCount);
    eventFilter.setCity(Collections.singletonList(cityId));

    final Calendar startDate = filter.getStartDate();
    if (null != startDate) {
      eventFilter.setStartDate(CommonUtils.dateToString(startDate));
    }

    final Calendar endDate = filter.getEndDate();
    if (null != endDate) {
      eventFilter.setEndDate(CommonUtils.dateToString(endDate));
    }

    return eventFilter;
  }

  /**
   * Queries all existing trip plan components from JCR and produces a list of TripPlan objects
   * (TripPlanHolder instance).
   *
   * @param language language for querying
   * @return a list of trip plans in a MemHolder instance
   * @throws RepositoryException if can't get some JCR data from the query
   */
  @NotNull TripPlanHolder queryTripPlans(final String language) throws RepositoryException {
    LOGGER.debug("Querying trip plans for language {}", language);

    final String path = getPathToTripPlans(language);

    final Map<String, String> queryMap = getQueryMapToSearchTripPlanPages(path);

    // City must be specified in properties (also this field is used in facets)
    // property name: 2_property
    final String citiesQueryPropName =
        "2" + Constants.UNDERSCORE + JcrPropertyPredicateEvaluator.PROPERTY;
    queryMap.put(citiesQueryPropName, TripPlanConstants.CITY_PROP_PATH_ON_PAGE);
    queryMap.put(citiesQueryPropName + Constants.DOT + JcrPropertyPredicateEvaluator.OPERATION,
        JcrPropertyPredicateEvaluator.OP_EXISTS);

    // Adds facets properties to the query starting from 3_property
    final Map<String, String> facetPropertiesMap = new HashMap<>();
    // Store 2_property (city) to facets map to be able to extract data
    facetPropertiesMap.put(TripPlanConstants.CITY_PROP_PATH_ON_PAGE, citiesQueryPropName);

    final TripPlanFilter filter;
    final List<TripPlan> tripPlanList = new LinkedList<>();
    final Map<String, TripPlan> tripPlanMap = new HashMap<>();

    final SearchResult searchResult;
    try (ResourceResolver resolver = resolverService.getResourceResolver()) {
      final Query query = queryBuilder
          .createQuery(PredicateGroup.create(queryMap), resolver.adaptTo(Session.class));

      searchResult = query.getResult();

      LOGGER.debug("Found {} trip plans for the path {}. Building holder.",
          searchResult.getHits().size(), path);

      // Iterate all trip plan pages, adapt them into TripPlan model and add to list/map
      CommonUtils.iteratorToStream(searchResult.getResources()).forEach(pageContentResource -> {
        final TripPlan tripPlan = pageContentResource.adaptTo(TripPlan.class);
        if (tripPlan == null) {
          LOGGER.error("Couldn't adapt trip plan page content resource {}. The trip plan page "
              + "will be ignored.", pageContentResource);
        } else {
          tripPlanList.add(tripPlan);
          tripPlanMap.put(tripPlan.getPath(), tripPlan);
        }
      });

      // Add possible filters (from facets)
      filter = getFilterFromResult(searchResult, facetPropertiesMap);
      LOGGER.trace("Extracted trip plan filter: {} for language {}", filter, language);

      return new TripPlanHolder(ImmutableList.copyOf(tripPlanList),
          ImmutableMap.copyOf(tripPlanMap), filter);
    }
  }

  /**
   * Returns query map for searching trip itinerary pages. Wrapping of utils method to be able to
   * mock it during unit testing.
   *
   * @param path path to all trip plans parent page
   * @return prepared map for query builder
   */
  @Generated
  @NotNull Map<String, String> getQueryMapToSearchTripPlanPages(final String path) {
    return JcrQueryUtils.getQueryMapToSearchPages(path, Constants.RT_TRIP_PLAN_PAGE);
  }

  /**
   * Processes facets from the query result and creates TripPlanFilter instance.
   *
   * @param searchResult  query search result to process
   * @param facetPropsMap map property to query property key
   * @return filter instance
   * @throws RepositoryException if can't get some JCR data from the query
   */
  TripPlanFilter getFilterFromResult(final SearchResult searchResult,
      final Map<String, String> facetPropsMap) throws RepositoryException {
    final TripPlanFilter filter = new TripPlanFilter();
    final Map<String, Facet> facets = searchResult.getFacets();

    Facet currentFacet;

    // Cities
    currentFacet = facets.get(facetPropsMap.get(TripPlanConstants.CITY_PROP_PATH_ON_PAGE));
    if (currentFacet != null) {
      // In filter we store city ids, not city objects (populated in the API request)
      filter.setCityIds(
          currentFacet.getBuckets().stream().map(Bucket::getValue).filter(StringUtils::isNotBlank)
              .distinct().collect(Collectors.toCollection(LinkedList::new)));
    }

    return filter;
  }

  /**
   * Safe deserializer for trip plans (for deserialization of the data stored in the user login
   * storage).
   */
  @SuppressWarnings({"java:S1141", "java:S3776"})
  private static class TripPlanDeserializer implements JsonDeserializer<TripPlan> {
    @Override
    public TripPlan deserialize(final JsonElement json, final Type type,
        final JsonDeserializationContext context) {
      final TripPlan result = new TripPlan();
      final JsonObject jsonObject = json.getAsJsonObject();

      result.setId(getStrProp(jsonObject, Constants.PN_ID));
      result.setPath(getStrProp(jsonObject, Constants.PATH_PROPERTY));
      result.setTitle(getStrProp(jsonObject, Constants.PN_TITLE));
      result.setStartDate(getStrProp(jsonObject, Constants.START_DATE));
      result.setEndDate(getStrProp(jsonObject, Constants.END_DATE));

      final TripDetail data = result.getData();

      final String errorMessage = "Exception while processing json trip plan data";

      try {
        final JsonArray cities = jsonObject.getAsJsonArray(Constants.CITIES);
        if (cities != null) {
          for (JsonElement cityItineraryJsonElement : cities) {
            JsonObject cityItineraryJsonObject = cityItineraryJsonElement.getAsJsonObject();

            String cityId = getCityId(cityItineraryJsonObject);

            final List<TripDay> cityItineraryDays = new LinkedList<>();
            try {
              final JsonArray daysJsonArray = cityItineraryJsonObject.getAsJsonArray("days");
              if (daysJsonArray != null) {
                int daysCount = daysJsonArray.size();
                for (int i = 0; i < daysCount; i++) {
                  final JsonObject dayJsonObject = daysJsonArray.get(i).getAsJsonObject();
                  final TripDay tripDay = new TripDay();
                  cityItineraryDays.add(tripDay);
                  tripDay.setId(getStrProp(dayJsonObject, Constants.PN_ID));

                  final JsonArray tripPointsJsonArray =
                      dayJsonObject.getAsJsonArray(TripPlanConstants.SCHEDULE);
                  if (tripPointsJsonArray != null) {
                    for (int j = 0; j < tripPointsJsonArray.size(); j++) {
                      try {
                        final TripPoint tripPoint = new TripPoint();
                        final JsonObject tripPointJsonObject =
                            tripPointsJsonArray.get(j).getAsJsonObject();
                        tripPoint.setPath(getStrProp(tripPointJsonObject, Constants.PATH_PROPERTY));
                        tripDay.getTripPoints().add(tripPoint);

                        // Get city from point if it wasn't stored for the itinerary
                        if (StringUtils.isBlank(cityId)) {
                          cityId = getCityId(tripPointJsonObject);
                        }
                      } catch (Exception e) {
                        LOGGER.error(errorMessage, e);
                      }
                    }
                  }
                }
              }
            } catch (Exception e) {
              LOGGER.error(errorMessage, e);
            }

            final CityItinerary cityItinerary = new CityItinerary(cityId);
            cityItinerary.getDays().addAll(cityItineraryDays);
            data.getCities().add(cityItinerary);
          }
        }
      } catch (Exception e) {
        LOGGER.error(errorMessage, e);
      }

      return result;
    }

    /**
     * Get string property from JsonObject.
     *
     * @param jsonObject jsonObject to get property from
     * @param propName   name of the property
     * @return string value of the property
     */
    @Generated
    private static String getStrProp(final JsonObject jsonObject, final String propName) {
      final JsonElement jsonElement = jsonObject.get(propName);
      if (jsonElement != null) {
        try {
          return jsonElement.getAsString();
        } catch (Exception e) {
          LOGGER.error("Couldn't extract string property {} from {}", propName, jsonObject, e);
        }
      }

      return null;
    }

    /**
     * Extract city id using cityId property or id property of the child object "city".
     *
     * @param jsonObject json object to check
     * @return city id stored in this object
     */
    @Generated
    @Nullable
    private static String getCityId(final JsonObject jsonObject) {
      String cityId = null;

      final JsonElement cityIdJsonObj = jsonObject.get("cityId");
      if (cityIdJsonObj != null) {
        cityId = cityIdJsonObj.getAsString();
      }
      if (StringUtils.isBlank(cityId)) {
        JsonObject cityObj = jsonObject.getAsJsonObject(Constants.CITY);
        if (cityObj == null || cityObj.isJsonNull()) {
          cityObj = jsonObject.getAsJsonObject("cityObj");
        }
        if (cityObj != null) {
          if (cityObj.isJsonObject()) {
            cityId = getStrProp(cityObj, Constants.PN_ID);
          } else if (cityObj.isJsonPrimitive()) {
            // Workaround for english city name
            cityId = AppUtils.stringToID(cityObj.getAsString());
          }
        }
      }

      return cityId;
    }
  }

  /**
   * Just a getter for static field to be able to change the returning value in unit testing.
   *
   * @return the maximum number of events that is allowed to be added into one trip day
   * automatically {@link TripPlanServiceImpl#MAX_EVENTS_TO_ADD_TO_TRIP_PLAN_DAY}
   */
  @Generated
  int getMaxEventsToAddToDay() {
    return TripPlanServiceImpl.MAX_EVENTS_TO_ADD_TO_TRIP_PLAN_DAY;
  }

  /**
   * Trip plan list memory holder to be stored into the cache.
   */
  public static final class TripPlanHolder extends MemHolder<TripPlan> {

    /**
     * Filters (cities) for the current trip plan list.
     */
    @Getter
    private final TripPlanFilter filter;

    /**
     * Constructor with a list of trip plans, a map and a filter for one language.
     *
     * @param list   list
     * @param map    map
     * @param filter all possible filters for creating trip plan for this language
     */
    public TripPlanHolder(final List<TripPlan> list, final Map<String, TripPlan> map,
        @NotNull final TripPlanFilter filter) {
      super(list, map);
      this.filter = filter;
    }
  }
}
