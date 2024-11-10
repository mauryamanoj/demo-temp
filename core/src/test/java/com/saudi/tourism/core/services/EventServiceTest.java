package com.saudi.tourism.core.services;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventFilterModel;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.impl.EventServiceImpl;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EventServiceTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/event";

  @Mock
  private QueryBuilder queryBuilder;

  private EventsRequestParams eventsRequestParams;

  @Mock
  private Query mockQuery;

  @Mock
  private Node mockNode;

  @Mock
  private SearchResult mockSearchResult;

  @Mock
  private Cache memCache;

  @Mock
  private UserService userService;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private AdminPageOption adminPageOption;

  @Mock
  private ResourceBundleProvider i18nProvider;


  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
          if ("riyadh".equalsIgnoreCase(key)) {
            return "Riyadh";
          } else if ("test".equalsIgnoreCase(key)) {
            return "Test";
          }

          return "fake_translated_value";
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @Mock
  private RegionCityService regionCityService;

  private EventService eventService;

  @BeforeEach
  public void setUp(final AemContext context) throws RepositoryException, InvalidTagFormatException {
    AdminUtil.setAdminSettingsService(adminSettingsService);

    eventService = new EventServiceImpl();

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    context.registerService(Cache.class, memCache);
    context.registerService(AdminSettingsService.class, adminSettingsService);
    context.registerService(UserService.class, userService);
    context.registerService(RegionCityService.class, regionCityService);
    context.registerService(QueryBuilder.class, queryBuilder);
    context.registerInjectActivateService(eventService);

    context.load().json("/components/events/content.json", CONTENT_PATH);


    lenient().when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);

    lenient().when(userService.getResourceResolver()).thenReturn(context.resourceResolver());

    lenient().when(adminSettingsService.getAdminOptions(anyString(), anyString())).thenReturn(adminPageOption);

    final RegionCity cityTest = new RegionCity("test", "Test");
    final RegionCity cityRiyadh = new RegionCity("riyadh", "Riyadh");
    final List<RegionCity> testCitiesList = Arrays.asList(cityTest, cityRiyadh);
    lenient().when(regionCityService.getAll(anyString())).thenReturn(testCitiesList);
    lenient().when(regionCityService.getRegionCityById(eq("test"), anyString())).thenReturn(cityTest);
    lenient().when(regionCityService.getRegionCityById(eq("Riyadh"), anyString())).thenReturn(cityRiyadh);

    lenient().when(queryBuilder.createQuery(any(), any())).thenReturn(mockQuery);
    lenient().when(mockQuery.getResult()).thenReturn(mockSearchResult);
    lenient().when(mockSearchResult.getNodes()).thenReturn(Arrays.asList(new Node[] {mockNode}).iterator());
    lenient().when(mockNode.getPath()).thenReturn("/content/sauditourism/en/events/event1");

    TagManager tagManager = context.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:events/Convention & Exhibition", "Convention & Exhibition",
        "Convention & Exhibition");
    tagManager.createTag("sauditourism:events/Shows & Performing Arts", "Shows & Performing Arts",
        "Shows & Performing Arts");

    tagManager.createTag("sauditourism:audience/12+", "12+", "12+");

    eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setLocale("en");
  }

  @Test
  public void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(0, eventlist.getData().size());
  }

  @Test
  public void getFilteredEvents(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en/event/jcr:content");
    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(1, eventlist.getData().size());
  }

  @Test
  public void getFilteredEventsCity(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en/event/jcr:content");
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setCity(param);

    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(1, eventlist.getData().size());
  }

  @Test
  public void getFilteredEventsKeyword(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en/event/jcr:content");
    eventsRequestParams.setKeyword("Khai");
    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(1, eventlist.getData().size());
  }

  @Test
  public void getFilteredEventsKeywordNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en/event/jcr:content");
    eventsRequestParams.setKeyword("Riyadhsss");
    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(0, eventlist.getData().size());
  }

  @Test
  public void getFilteredAllEvents(AemContext context)
      throws IllegalArgumentException, RepositoryException {
    when(mockNode.getPath()).thenReturn("/content/sauditourism/en/event/jcr:content");
    eventsRequestParams.setAll(true);
    EventListModel eventlist = eventService.getFilteredEvents(eventsRequestParams);
    Assertions.assertEquals(1, eventlist.getData().size());
  }


  /**
   * Reflection method of DisclaimersFilter.updateDisclaimers(text,Map)
   *
   * @return
   */
  private Method matchFilters() {
    Method generatePageId = null;
    try {
      generatePageId = EventServiceImpl.class
          .getDeclaredMethod("matchFilters", EventDetail.class, EventsRequestParams.class);
    } catch (Exception e) {
    }
    generatePageId.setAccessible(true);
    return generatePageId;
  }

  /**
   * Reflection method of DisclaimersFilter.updateDisclaimers(text,Map)
   *
   * @return
   */
  private Method matchKeywords() {
    Method generatePageId = null;
    try {
      generatePageId = EventServiceImpl.class
          .getDeclaredMethod("matchKeywords", EventDetail.class, String.class);
    } catch (Exception e) {
    }
    generatePageId.setAccessible(true);
    return generatePageId;
  }

  @Test
  public void matchFilters(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    EventDetail eventDetail = new EventDetail();
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersCityEmpty(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    EventDetail eventDetail = new EventDetail();
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setCity(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersCity(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    EventDetail eventDetail = new EventDetail();
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setCity(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersRegion(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/event1/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setRegion(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersRegionTrue(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/event1/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setRegion(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersCityTrue(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/event1/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Al-Ula");
    eventsRequestParams.setCity(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersRegionCityTrue(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/event1/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Al-Ula");
    eventsRequestParams.setCity(param);

    List<String> paramRegion = new ArrayList<>();
    paramRegion.add("Riyadh");
    eventsRequestParams.setRegion(paramRegion);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersRegionCityCombined(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/event1/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh");
    eventsRequestParams.setCity(param);

    List<String> paramRegion = new ArrayList<>();
    paramRegion.add("Riyadh");
    eventsRequestParams.setRegion(paramRegion);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersCategory(final AemContext context)
      throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {

    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    eventDetail.setCategoryTitles(new ArrayList<>());
    eventDetail.getCategoryTitles().add("music");
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> category = new ArrayList<>();
    category.add("music");
    eventsRequestParams.setCategory(category);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersCategoryNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Tennis");
    eventsRequestParams.setCategory(param);
    Assertions
        .assertEquals(false, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersSeason(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Riyadh Season");
    eventsRequestParams.setSeason(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersSeasonNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("Alula Season");
    eventsRequestParams.setSeason(param);
    Assertions
        .assertEquals(false, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersFrePaid(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setFreePaid("free");
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersFrePaidNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setFreePaid("paid");
    Assertions
        .assertEquals(false, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersTarget(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    eventDetail.setTargetTitles(new ArrayList<>());
    eventDetail.getTargetTitles().add("families");
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("families");
    eventsRequestParams.setTarget(param);
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersTargetNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    List<String> param = new ArrayList<>();
    param.add("all");
    eventsRequestParams.setTarget(param);
    Assertions
        .assertEquals(false, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void shouldNotMatchKeywordOnCategory(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setKeyword("Music");
    assertFalse((boolean)matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersKeywordNeg(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setKeyword("Tennis");
    Assertions
        .assertEquals(false, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersDate(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setStartDate("2019-12-03");
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void matchFiltersDatePos(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setStartDate("2019-12-09");
    Assertions
        .assertEquals(true, matchFilters().invoke(eventService, eventDetail, eventsRequestParams));
  }

  @Test
  public void shouldMatchKeywordOnTitle(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    assertTrue((boolean) matchKeywords().invoke(eventService, eventDetail, "Khai"));
  }

  @Test
  public void souldNotMatchKeywordOnCategory(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    assertFalse((boolean)matchKeywords().invoke(eventService, eventDetail, "football"));
  }

  @Test
  public void shouldNotMatchKeywordOnCity(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    assertFalse((boolean)matchKeywords().invoke(eventService, eventDetail, "riyadh"));
  }

  @Test
  public void shouldNotMatchKeywordOnSeason(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException,
      InvocationTargetException, IllegalAccessException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    assertFalse((boolean)matchKeywords().invoke(eventService, eventDetail, "Riyadh Season"));
  }

  @Test
  public void getRelatedEvents(AemContext context)
      throws IllegalArgumentException, RepositoryException {

    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setStartDate("2019-12-09");
    Assertions
        .assertEquals(0, eventService.getRelatedEvents("en", anyBoolean(), eventDetail).size());
  }

  @Test
  public void getRelatedEventsPaths(AemContext context)
      throws IllegalArgumentException, RepositoryException {

    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    EventDetail eventDetail = context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);
    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setStartDate("2019-12-09");
    Assertions.assertEquals(0,
        eventService.getRelatedEventsPaths("en", anyBoolean(), eventDetail).size());
  }

  @Test
  public void getEventFiltersTest(AemContext context) throws RepositoryException {
    String RESOURCE_PATH = "/content/sauditourism/en/event/jcr:content";
    context.currentResource(RESOURCE_PATH).adaptTo(EventDetail.class);

    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setLocale("en");
    eventsRequestParams.setStartDate("2019-12-09");
    EventFilterModel eventFilterModel = eventService.getEventFilters(eventsRequestParams);
    Assertions.assertNotNull(eventFilterModel);
  }

  @Test
  public void getEventAppFilters(AemContext context) {
    context.load().json("/utils/city.json", "/apps/sauditourism/components/content/utils/city"
        + "-fixed");
    context.currentResource("/apps");

    MockSlingHttpServletRequest request = context.request();

    EventAppFilterModel eventAppFilterModel = eventService.getEventAppFilters("en");
    Assertions.assertEquals("convention-&-exhibition", eventAppFilterModel.getCategories().get(0).getId());

  }

}