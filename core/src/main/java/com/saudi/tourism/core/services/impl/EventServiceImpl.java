package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.EventsFiltersSettings;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.models.components.events.EventBase;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventFilterModel;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SaudiConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * The type Event service.
 */
@Component(service = EventService.class,
           immediate = true)
@Slf4j
public class EventServiceImpl implements EventService {
  /**
   * The constant VENUE.
   */
  public static final String VENUE = "venue";
  /**
   * The constant VENUE_TAGS.
   */
  public static final String VENUE_TAGS = "venueTags";
  /**
   * The constant CATEGORY_TAGS.
   */
  private static final String CATEGORY_TAGS = "categoryTags";
  /**
   * The constant FREE_PAID.
   */
  private static final String FREE_PAID = "freePaid";
  /**
   * The constant SEASON.
   */
  private static final String SEASON = "season";
  /**
   * The constant TARGET_GROUP_TAGS.
   */
  private static final String TARGET_GROUP_TAGS = "targetGroupTags";
  /**
   * used for extracting filters for events from search result,
   * should match query builder property number.
   */
  private static final String[] FILTER_GRPS =
      {"city:3", "region:4", "categoryTags:5", "freePaid:6", "targetGroupTags:7", "season:8",
          "calendarStartDate:9"};
  /**
   * The constant SLIDER_COUNT.
   */
  private static final int SLIDER_COUNT = 3;
  /**
   * The constant SEASON_CONTENT_UTILS_PATH.
   */
  private static final String SEASON_CONTENT_UTILS_PATH =
      "sauditourism/components/content/utils/season";
  /**
   * The constant PRIORITY_CONTENT_UTILS_PATH.
   */
  private static final String PRIORITY_CONTENT_UTILS_PATH =
      "sauditourism/components/content/utils/priority";
  /**
   * The constant FREE_PAID_CONTENT_ITEMS_PATH.
   */
  private static final String FREE_PAID_CONTENT_ITEMS_PATH =
      "sauditourism/components/commons/generic/content/items/freePaid/items";

  /**
   * The constant CALENDAR_END_DATE.
   */
  private static final String CALENDAR_END_DATE = "calendarEndDate";
  /**
   * The constant CALENDAR_START_DATE.
   */
  private static final String CALENDAR_START_DATE = "calendarStartDate";
  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;
  /**
   * The memCache.
   */
  @Reference
  private Cache memCache;

  /**
   * The regionCityService.
   */
  @Reference
  private RegionCityService regionCityService;

  /**
   * ResourceBundleProvider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  @Override
  public EventListModel getFilteredEvents(EventsRequestParams eventsRequestParams) {

    EventListModel eventListModel =
        getAllEvents(eventsRequestParams.getLocale(), eventsRequestParams.isAllExpired());
    // city filter to Filter on both City and Region
    if (Objects.nonNull(eventsRequestParams.getCity())) {
      eventsRequestParams.setRegion(eventsRequestParams.getCity());
      eventsRequestParams.setCity(eventsRequestParams.getCity());
    }

    //if the header includes key=all, value=true, retrieve all the events for the locale
    if (eventsRequestParams.isAll()) {
      //get the list of related paths for each event
      for (EventDetail event : eventListModel.getData()) {
        event.setRelatedEventsPaths(getRelatedEventsPaths(eventsRequestParams.getLocale(),
            eventsRequestParams.isAllExpired(), event));
      }
      return eventListModel;
    } else {
      return filterEventsOnParams(eventListModel, eventsRequestParams);
    }

  }

  @Override
  public EventFilterModel getEventFilters(EventsRequestParams eventsRequestParams) {

    EventListModel eventListModel =
        getAllEvents(eventsRequestParams.getLocale(), eventsRequestParams.isAllExpired());

    return eventListModel.getFilters();
  }

  @Override
  public EventAppFilterModel getEventAppFilters(final String locale) {
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      EventAppFilterModel eventListModel = new EventAppFilterModel();
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      eventListModel.setCategories(getAppTagItems(tagManager, "sauditourism:events", locale));
      eventListModel.setTarget(getAppTagItems(tagManager, "sauditourism:audience", locale));
      List<RegionCity> regionCityList = regionCityService.getAll(locale);
      if (regionCityList != null) {
        eventListModel.setCities(regionCityList.stream()
            .map(regionCity -> new AppFilterItem(regionCity.getId(), regionCity.getName()))
            .collect(Collectors.toList()));
      }
      eventListModel.setSeason(getAppDialogItems(resolver, SEASON_CONTENT_UTILS_PATH, i18n));
      eventListModel.setPriorities(getAppDialogItems(resolver, PRIORITY_CONTENT_UTILS_PATH, i18n));
      eventListModel.setFreePaid(getAppDialogItems(resolver, FREE_PAID_CONTENT_ITEMS_PATH, i18n));
      eventListModel.setAgeRange(getAppDialogItems(resolver,
          SaudiConstants.AGE_RANGES_CONTENT_UTILS_PATH, i18n,
          true));
      eventListModel.setGender(getAppDialogItems(resolver,
          SaudiConstants.GENDERS_CONTENT_UTILS_PATH, i18n, true));
      eventListModel.setInterests(getAppDialogItems(resolver,
          SaudiConstants.INTERESTS_CONTENT_UTILS_PATH, i18n,
          true));
      eventListModel.setTravelPartner(getAppDialogItems(resolver,
          SaudiConstants.TRAVEL_PARTNERS_CONTENT_UTILS_PATH, i18n, true));
      return eventListModel;
    }
  }

  /**
   * Gets app dialog items.
   *
   * @param resolver        the current resource resolver
   * @param dialogItemsPath the dialog items path
   * @param i18n            the current locale resource bundle
   * @return the app dialog items
   */
  private List<AppFilterItem> getAppDialogItems(final ResourceResolver resolver,
      final String dialogItemsPath, final ResourceBundle i18n) {
    return getAppDialogItems(resolver, dialogItemsPath, i18n, false);
  }

  /**
   * Gets app dialog items.
   *
   * @param resolver        the current resource resolver
   * @param dialogItemsPath the dialog items path
   * @param i18n            the current locale resource bundle
   * @param camelCase       should id be formatted to camel case
   * @return the app dialog items
   */
  private List<AppFilterItem> getAppDialogItems(final ResourceResolver resolver,
      final String dialogItemsPath, final ResourceBundle i18n, final boolean camelCase) {

    try {
      Stream<Resource> resourceStream =
          Optional.ofNullable(dialogItemsPath).map(path -> dialogItemsPath)
              .filter(StringUtils::isNotBlank).map(resolver::getResource).map(Resource::getChildren)
              .map(res -> StreamSupport.stream(res.spliterator(), false)).orElse(Stream.empty());

      return resourceStream.map(Resource::getValueMap)
          .map(valueMap -> this.getAppFilterItem(valueMap, i18n, camelCase))
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error in APP filter listing");

    }
    return new ArrayList<>();
  }

  /**
   * Gets app filter item.
   *
   * @param valueMap   the value map
   * @param i18nBundle i18n bundle for the current language
   * @param camelCase  should id be formatted to camel case
   * @return the app filter item
   */
  private AppFilterItem getAppFilterItem(final ValueMap valueMap,
      final ResourceBundle i18nBundle, boolean camelCase) {
    String id = valueMap.get("value").toString();
    String value = valueMap.get("text").toString();
    if ("".equals(id)) {
      id = AppUtils.stringToID(value, camelCase);
    }
    if (Objects.nonNull(i18nBundle)) { // update city to i18n value
      value = i18nBundle.getString(id);
    }
    return new AppFilterItem(id, value, camelCase);
  }

  /**
   * Gets app dialog items.
   *
   * @param tagManager   the request
   * @param tagItemsPath the tag items path
   * @param language     the current locale
   * @return the app dialog items
   */
  private List<AppFilterItem> getAppTagItems(final TagManager tagManager, final String tagItemsPath,
      final String language) {


    List<AppFilterItem> appFilterItems = new ArrayList<>();
    try {
      Iterator<Tag> tagIterator = tagManager.resolve(tagItemsPath).listChildren();

      while (tagIterator.hasNext()) {

        Tag tag = tagIterator.next();
        AppFilterItem appFilterItem = new AppFilterItem(AppUtils.stringToID(tag.getTitle()),
            tag.getTitle(new Locale(language)));
        appFilterItems.add(appFilterItem);
      }

      return appFilterItems;
    } catch (Exception e) {
      LOGGER.error("Error in APP filter listing", e);
    }
    return new ArrayList<>();
  }

  @Override
  public List<EventDetail> getRelatedEvents(String language, Boolean isAllExpired,
      EventDetail eventDetail) {

    EventListModel eventListModel = getAllEvents(language, isAllExpired);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();

    if (Objects.isNull(eventDetail)) {
      eventDetail = new EventDetail();
    }
    // Filter based on current city or region
    eventsRequestParams.setCity(Arrays.asList(eventDetail.getCityId()));
    eventsRequestParams.setPath(eventDetail.getPath());
    eventsRequestParams.setLimit(SLIDER_COUNT);
    return filterEventsOnParams(eventListModel, eventsRequestParams).getData();

  }

  @Override
  public List<String> getRelatedEventsPaths(String language, Boolean isAllExpired,
      EventDetail eventDetail) {

    List<String> ouputList = new ArrayList<>();

    EventListModel eventListModel = getAllEvents(language, isAllExpired);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();

    // Filter based on current city or region
    if (eventDetail.getCity() != null) {
      eventsRequestParams.setCity(Arrays.asList(eventDetail.getCity()));
    }
    if (eventDetail.getRegion() != null) {
      eventsRequestParams.setRegion(Arrays.asList(eventDetail.getRegion()));
    }
    eventsRequestParams.setPath(eventDetail.getPath());
    eventsRequestParams.setLimit(SLIDER_COUNT);

    List<EventDetail> filteredModel =
        filterEventsOnParams(eventListModel, eventsRequestParams).getData();
    for (EventDetail event : filteredModel) {
      ouputList.add(event.getPath());
    }
    return ouputList;

  }

  @Override
  public EventListModel getAllEvents(String language, boolean isAllExpired) {
    String cacheKey = Constants.EVENTS_CACHE_KEY + language;

    if (isAllExpired) {
      cacheKey += "-expired";
    }
    EventListModel eventListModel = (EventListModel) memCache.get(cacheKey);
    if (eventListModel != null) {
      return eventListModel;
    }
    eventListModel = new EventListModel();
    List<EventDetail> eventsList = new ArrayList<>();

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      AdminPageOption adminOptions = AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
      Query query = queryBuilder.createQuery(
          PredicateGroup.create(getPredicateQueryMap(adminOptions.getEventsPath(), isAllExpired)),
          resourceResolver.adaptTo(Session.class));
      SearchResult searchResult = query.getResult();
      Iterator<Node> nodeIterator = searchResult.getNodes();

      int count = 0;
      count = getAllEventDetails(resourceResolver, nodeIterator, eventsList, count);
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

      eventListModel.setData(eventsList);
      eventListModel.setFilters(getFiltersFromResult(language, searchResult, tagManager));
      Pagination pagination = new Pagination();
      pagination.setTotal(count);
      eventListModel.setPagination(pagination);
    }

    if (!eventsList.isEmpty()) {
      memCache.add(cacheKey, eventListModel);
    }
    return eventListModel;

  }

  /**
   * update all events details list from query builder iterator items.
   *
   * @param resolver     the resolver
   * @param nodeIterator the node iterator
   * @param eventsList   the events list
   * @param count        the count
   * @return the all event details
   */
  private int getAllEventDetails(final ResourceResolver resolver, final Iterator<Node> nodeIterator,
      final List<EventDetail> eventsList, int count) {
    while (nodeIterator.hasNext()) {
      try {
        Resource res = resolver.getResource(nodeIterator.next().getPath());
        if (res != null) {
          EventDetail event = res.adaptTo(EventDetail.class);
          if (event != null) {
            eventsList.add(event);
            count++;
          } else {
            LOGGER.error("Error in adapting EventDetail, path: {}", res.getPath());
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in forming states events list", e);
      }
    }
    return count;
  }

  /**
   * Filter events on params.
   *
   * @param listModel           the event list
   * @param eventsRequestParams the eventsRequestParams
   * @return filteredEventList the event selected filters
   */
  private EventListModel filterEventsOnParams(EventListModel listModel,
      EventsRequestParams eventsRequestParams) {
    int limit = eventsRequestParams.getLimit();

    EventListModel filteredEventList = new EventListModel();
    if (null != listModel.getData()) {
      List<EventDetail> events = listModel.getData();
      List<EventDetail> filterdEvents = new ArrayList<>();
      for (EventDetail event : events) {
        if (matchFilters(event, eventsRequestParams) && !event.getPath()
            .equals(eventsRequestParams.getPath())) {
          filterdEvents.add(event);
        }
      }

      if (StringUtils.isNotEmpty(eventsRequestParams.getFeatured())
          && eventsRequestParams.getFeatured().equals("true") && filterdEvents.isEmpty()
          && !events.isEmpty() && StringUtils.isNotEmpty(eventsRequestParams.getIsTop())
          && eventsRequestParams.getIsTop().equals("true")) {
        LocalDate date = LocalDate.now();
        for (EventDetail eventDetail :events) {
          if (null != eventDetail.getStartDateCal()) {
            LocalDate calDate = CommonUtils.calendarToLocalDate(eventDetail.getStartDateCal());
            if (date.isBefore(calDate)) {
              filterdEvents.add(eventDetail);
            }
          }
        }
        if (!filterdEvents.isEmpty()) {
          List<EventDetail> nearestEventList = new ArrayList<>();
          EventDetail nearestEvent = filterdEvents.stream()
              .min(Comparator.comparing(EventDetail::getStartDate))
              .get();
          nearestEventList.add(nearestEvent);
          filteredEventList.setData(nearestEventList);
          filteredEventList.setFilters(listModel.getFilters());
          Pagination pagination = listModel.getPagination();
          pagination.setLimit(limit);
          pagination.setTotal(nearestEventList.size());
          filteredEventList.setPagination(pagination);
        }
        return filteredEventList;
      }

      if (CollectionUtils.isNotEmpty(eventsRequestParams.getSortBy())) {
        Comparator<EventDetail> comparator = sortComparator(eventsRequestParams.getSortBy());
        filterdEvents = filterdEvents.stream().sorted(comparator).collect(Collectors.toList());
      }
      int total = filterdEvents.size();
      if (limit == 0) {
        filterdEvents = filterdEvents.stream()
          .skip(eventsRequestParams.getOffset())
          .collect(Collectors.toList());
      } else {
        filterdEvents = filterdEvents.stream()
          .skip(eventsRequestParams.getOffset())
          .limit(limit)
          .collect(Collectors.toList());
      }

      Pagination pagination = listModel.getPagination();
      pagination.setLimit(eventsRequestParams.getLimit());
      pagination.setTotal(total);

      pagination.setOffset(eventsRequestParams.getOffset());
      filteredEventList.setData(filterdEvents);
      filteredEventList.setPagination(pagination);
      filteredEventList.setFilters(listModel.getFilters());
    }

    return filteredEventList;
  }

  /**
   * Comparator by multiple fields.
   *
   * @param sortList fields
   * @return Comparator
   */
  private Comparator<EventDetail> sortComparator(List<String> sortList) {
    Comparator<EventDetail> comparator = null;
    for (String sortBy : sortList) {
      if (sortBy.equals("date")) {
        Function<EventDetail, String> extractor = EventBase::getStartDate;
        comparator = Comparator.comparing(extractor);
      }
      if (sortBy.equals("featured")) {
        Function<EventDetail, Boolean> extractor = e -> !e.isFeatured();
        if (comparator == null) {
          comparator = Comparator.comparing(extractor);
        } else {
          comparator.thenComparing(extractor);
        }
      }
    }
    return comparator;
  }

  /**
   * Match filters boolean.
   *
   * @param event               the event
   * @param eventsRequestParams the event selected filters
   * @return the boolean
   */
  @SuppressWarnings("java:S3776")
  private boolean matchFilters(EventDetail event, EventsRequestParams eventsRequestParams) {

    // keyword filtering
    if (StringUtils.isNotEmpty(eventsRequestParams.getKeyword()) && !matchKeywords(event,
        eventsRequestParams.getKeyword())) {
      return false;
    }

    if (StringUtils.isNotEmpty(eventsRequestParams.getId())) {
      List<String> eventParamIds = Stream.of(eventsRequestParams.getId()
          .split(Constants.COMMA)).collect(Collectors.toList());

      String eventsRequestParamsId = eventsRequestParams.getId();

      List<String> eventParamIdsProcessed = eventParamIds.stream().map(eventParam -> {
        String eventProcessed = eventParam;
        if (eventParam.startsWith(Constants.FORWARD_SLASH_CHARACTER)) {
          eventProcessed = eventParam.substring(1);
        }
        return eventProcessed;
      }).collect(Collectors.toList());

      if (!eventParamIdsProcessed.contains(event.getId())) {
        return false;
      }
    }
    // City or Region match single match
    if (CollectionUtils.isNotEmpty(eventsRequestParams.getCity())) {
      if (!containsIgnoreCaseWithContains(eventsRequestParams.getCity(), event.getCityId()) && (
          CollectionUtils.isNotEmpty(eventsRequestParams.getRegion()) && !containsIgnoreCaseWithContains(
              eventsRequestParams.getRegion(), event.getRegion()))) {
        return false;
      }
    } else if (CollectionUtils.isNotEmpty(eventsRequestParams.getRegion()) && !containsIgnoreCaseWithContains(
        eventsRequestParams.getRegion(), event.getRegion())) {
      return false;
    }

    if (Objects.nonNull(eventsRequestParams.getFreePaid()) && !eventsRequestParams.getFreePaid()
        .equalsIgnoreCase(event.getFreePaidId())) {
      return false;
    }

    if (CollectionUtils.isNotEmpty(eventsRequestParams.getSeason())) {
      //some events are not supposed to map with season so season will return null.
      if (StringUtils.isEmpty(event.getSeasonId())) {
        return false;
      } else if (!eventsRequestParams.getSeason().stream()
            .anyMatch(seasonFilterItem -> seasonFilterItem.contains(event.getSeasonId()))) {
        if (!event.getSeasonId().equals("all-seasons")) {
          return false;
        }
      }
    }


    if (CollectionUtils.isNotEmpty(eventsRequestParams.getTarget())
        && !inArrayContains(event.getTargetTitles(), eventsRequestParams.getTarget())) {
      return false;
    }

    if (CollectionUtils.isNotEmpty(eventsRequestParams.getCategory())
        && !inArrayContains(event.getCategoryTitles(), eventsRequestParams.getCategory())) {
      return false;
    }
    if (CollectionUtils.isNotEmpty(eventsRequestParams.getVenue())
        && !inArrayContains(event.getVenueTitles(), eventsRequestParams.getVenue())) {
      return false;
    }
    if (Objects.nonNull(eventsRequestParams.getStartDate()) || Objects
        .nonNull(eventsRequestParams.getEndDate())) {
      return CommonUtils.isDateBetweenStartEnd(eventsRequestParams.getStartDate(),
          eventsRequestParams.getEndDate(), event.getCalendarStartDate(),
          event.getCalendarEndDate());
    }
    if (StringUtils.isNotEmpty(eventsRequestParams.getFeatured())
        && ((eventsRequestParams.getFeatured().equals("true") && !event.isFeatured())
        || (eventsRequestParams.getFeatured().equals("false") && event.isFeatured()))) {
      return false;
    }

    if (StringUtils.isNotBlank(eventsRequestParams.getChannel())
        && CollectionUtils.isNotEmpty(event.getChannel()) && !containsIgnoreCase(event.getChannel(),
        eventsRequestParams.getChannel())) {
      return false;
    }
    return true;
  }

  /**
   * Find value in String list.
   *
   * @param list  list
   * @param value value for search
   * @return the boolean
   */
  private boolean containsIgnoreCase(List<String> list, String value) {
    for (String l : list) {
      if (l.equalsIgnoreCase(value)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Find value contained  String list.
   *
   * @param list  list
   * @param value value for search
   * @return the boolean
   */
  private boolean containsIgnoreCaseWithContains(List<String> list, String value) {

    if (list == null) {
      return false;
    }
    return list.stream().anyMatch(l -> StringUtils.containsIgnoreCase(l, value));
  }

  /**
   * Find tag value in String array.
   *
   * @param list    list of tags in event
   * @param filters list of tags in filter
   * @return the boolean
   */
  private boolean inArray(List<String> list, List<String> filters) {
    if (list == null) {
      return false;
    }
    return list.stream().anyMatch(s -> filters.contains(AppUtils.stringToID(s)));
  }

  /**
   * Find tag value contained String array.
   *
   * @param list    list of tags in event
   * @param filters list of tags in filter
   * @return the boolean
   */
  private boolean inArrayContains(List<String> list, List<String> filters) {
    if (list == null) {
      return false;
    }
    return list.stream().anyMatch(s ->  filters.stream()
        .anyMatch(filterItem -> filterItem.contains(AppUtils.stringToID(s))));
  }


  /**
   * Match keywords for any of title, copy, region, city, category fields.
   *
   * @param event   the event
   * @param keyword the keyword
   * @return the boolean
   */
  private boolean matchKeywords(final EventDetail event, final String keyword) {

    final String keywordLowercase = keyword.toLowerCase();
    return event.getTitle().toLowerCase().contains(keywordLowercase);
  }

  /**
   * Gets filters from search result.
   *
   * @param language   the current locale
   * @param result     the result
   * @param tagManager the tagManager
   * @return the string
   */
  private EventFilterModel getFiltersFromResult(final String language, SearchResult result,
      final TagManager tagManager) {
    EventFilterModel filters = new EventFilterModel();
    try {
      Map<String, Facet> facets = result.getFacets();
      for (String grp : FILTER_GRPS) {
        // 0th element is filtername and 1st element is queryparam number
        String[] filterSplit = grp.split(Constants.COLON_SLASH_CHARACTER);
        String key = filterSplit[1] + "_property";
        if (Constants.CITY.equals(filterSplit[0])) {
          filters.setCity(getFilteritems(facets, key, tagManager, true, language));
        } else if (Constants.PN_REGION.equals(filterSplit[0])) {
          filters.setRegion(getFilteritems(facets, key, tagManager, false, language));
        } else if (CATEGORY_TAGS.equals(filterSplit[0])) {
          filters.setCategory(getFilteritems(facets, key, tagManager, false, language));
        } else if (TARGET_GROUP_TAGS.equals(filterSplit[0])) {
          filters.setTarget(getFilteritems(facets, key, tagManager, false, language));
        } else if (SEASON.equals(filterSplit[0])) {
          filters.setSeason(getFilteritems(facets, key, tagManager, true, language));
        } else if (VENUE.equals(filterSplit[0])) {
          filters.setVenue(getFilteritems(facets, key, tagManager, true, language));
        }

      }
      filters.setFreePaid(getFreePaiditems(language));
      filters.setDate(getDateitems(language));
      filters.setSortBy(getSortByItems(language));
    } catch (RepositoryException e) {
      LOGGER.error("RepositoryException during getFiltersFromResult() ", e);
    }
    return filters;
  }

  /**
   * Gets freepaid filteritems.
   *
   * @param language the locale to localise
   * @return the filteritems
   */
  private List<AppFilterItem> getFreePaiditems(final String language) {
    final Locale locale = new Locale(language);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);

    String value = "only-free-events";
    if (Objects.nonNull(i18nBundle)) {
      value = i18nBundle.getString(value);
    }

    List<AppFilterItem> items = new ArrayList<>();
    items.add(new AppFilterItem("free", value));

    return items;
  }

  /**
   * Gets sortBy items.
   *
   * @param language language
   * @return the filteritems
   */
  private List<AppFilterItem> getSortByItems(String language) {
    List<AppFilterItem> items = new ArrayList<>();
    items.add(new AppFilterItem("date", "Date"));
    items.add(new AppFilterItem("featured", "Featured"));
    final Locale locale = new Locale(language);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
    if (Objects.nonNull(i18nBundle)) {
      items.stream().forEach(item -> item.setValue(i18nBundle.getString(item.getValue())));
    }
    return items;
  }

  /**
   * Gets filteritems.
   *
   * @param facets     the facets
   * @param key        the key
   * @param tagManager the tagManager
   * @param localise   the localise
   * @param language   the locale to localise
   * @return the filteritems
   */
  private List<AppFilterItem> getFilteritems(Map<String, Facet> facets, String key,
      final TagManager tagManager, final boolean localise, final String language) {
    final Locale locale = new Locale(language);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);

    List<AppFilterItem> items = new ArrayList<>();
    if ((!facets.isEmpty()) && (facets.containsKey(key))) {
      Facet fc = facets.get(key);

      for (Bucket bucket : fc.getBuckets()) {
        String bc = bucket.getValue();
        AppFilterItem item = new AppFilterItem(bc, bc);
        if (item.getId().contains("/")) { // handle tag names
          Tag tag = tagManager.resolve(item.getId());

          if (Objects.nonNull(tag)) {
            item.setId(AppUtils.stringToID(tag.getTitle()));
            item.setValue(tag.getTitle(locale));
          }
        }
        //in case if tag not found but the translation needed
        if (Objects.nonNull(i18nBundle) && localise) { // update city to i18n value
          item.setValue(i18nBundle.getString(item.getValue()));
        }
        items.add(item);
      }

    }
    return items;
  }

  /**
   * Gets date filteritems.
   *
   * @param language the locale to localise
   * @return the date filteritems
   */
  private List<AppFilterItem> getDateitems(final String language) {
    final Locale locale = new Locale(language);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);

    List<AppFilterItem> items = new ArrayList<>();
    items.add(new AppFilterItem("today", "today"));
    items.add(new AppFilterItem("this-week", "this-week"));
    items.add(new AppFilterItem("this-month", "this-month"));
    items.add(new AppFilterItem("custom", "custom"));

    if (Objects.nonNull(i18nBundle)) {
      items.forEach(item -> item.setValue(i18nBundle.getString(item.getValue())));
    }
    return items;
  }

  /**
   * Querybuilder map for getting all events.
   * (Template == Event AND (StartDate < currentDate + 3months && EndDate > currentDate)
   * OR (StartDate >= currentDate AND no EndDate))
   *
   * @param path         events path
   * @param isAllExpired Boolean
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path, boolean isAllExpired) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);

    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.EVENT_DETAIL_RES_TYPE);
    map.put("2_property", PN_TITLE);
    map.put("2_property.operation", "exists");
    map.put("3_property", Constants.CITY);
    map.put("4_property", Constants.PN_REGION);
    map.put("5_property", CATEGORY_TAGS);
    map.put("6_property", FREE_PAID);
    map.put("7_property", TARGET_GROUP_TAGS);
    map.put("8_property", SEASON);
    map.put("9_property", VENUE_TAGS);
    map.put("orderby", "@calendarStartDate");
    map.put("orderby.sort", "asc");
    if (isAllExpired) { // for expired events and order them in recent expired
      map.put("notexpired", "false");
      map.put("notexpired.property", CALENDAR_END_DATE);
      map.put("orderby", "@calendarEndDate");
      map.put("orderby.sort", "desc");
    } else {
      map.put("1_group.1_group.p.or", "true");
      map.put("1_group.1_group.1_group.1_relativedaterange.property", CALENDAR_END_DATE);
      map.put("1_group.1_group.1_group.1_relativedaterange.lowerBound", "-1d");
      map.put("1_group.1_group.1_group.2_relativedaterange.property", CALENDAR_START_DATE);
      map.put("1_group.1_group.1_group.2_relativedaterange.upperBound", "90d");
      map.put("1_group.1_group.2_group.1_property", CALENDAR_END_DATE);
      map.put("1_group.1_group.2_group.1_property.operation", "not");
      map.put("1_group.1_group.2_group.2_relativedaterange.property", CALENDAR_START_DATE);
      map.put("1_group.1_group.2_group.2_relativedaterange.lowerBound", "-1d");
    }

    map.put("p.limit", "-1");
    return map;
  }

  /**
   * Get all experiences filters from admin page package settings.
   * @param locale locale.
   * @return Object experience filters data
   * @throws IOException IOException.
   */
  @Override
  public EventsFiltersSettings getDynamicEventsFilters(String locale) throws IOException {
    EventsFiltersSettings settings = AdminUtil.getEventsFilters(locale);
    return settings;
  }
}
