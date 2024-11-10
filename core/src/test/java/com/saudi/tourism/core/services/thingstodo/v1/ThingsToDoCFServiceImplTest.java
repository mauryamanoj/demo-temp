package com.saudi.tourism.core.services.thingstodo.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.attraction.AttractionCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.filters.v1.FiltersService;
import com.saudi.tourism.core.services.thingstodo.v1.adapters.ThingToDoAttractionCFAdapter;
import com.saudi.tourism.core.services.thingstodo.v1.adapters.ThingToDoCFAdapter;
import com.saudi.tourism.core.services.thingstodo.v1.adapters.ThingToDoModelAdapterFactory;
import com.saudi.tourism.core.services.thingstodo.v1.comparators.ThingToDoComparatorsChain;
import com.saudi.tourism.core.services.thingstodo.v1.filters.CategoriesFilter;
import com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter;
import com.saudi.tourism.core.services.thingstodo.v1.filters.DestinationsFilter;
import com.saudi.tourism.core.services.thingstodo.v1.filters.SeasonsFilter;
import com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter;
import com.saudi.tourism.core.services.thingstodo.v1.filters.ThingsToDoFiltersChain;
import com.saudi.tourism.core.services.thingstodo.v1.filters.ThingsToDoFiltersChainImpl;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ThingsToDoCFServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private FragmentTemplate ft;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private ExperienceService experienceService;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private SearchResult searchResult;

  @Mock
  private ThingToDoComparatorsChain comparatorsChain;

  @Mock
  private ThingsToDoFiltersChainImpl thingsToDoFiltersChain;

  @Mock
  private RunModeService runModeService;

  @Mock
  private FiltersService filtersService;

  @Mock
  private Cache memCache;

  private ThingsToDoCFService service = new ThingsToDoCFServiceImpl();

  private final ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case "attraction":
          return "Attraction";
        case "experience":
          return "experience";
        default:
          return "fake_i18n_value";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  public void setUp(final AemContext context) throws InvalidTagFormatException {

    context.load().json("/services/things-todo-service/attractions-root.json", "/content/dam/sauditourism/cf/en/attractions");
    context.load().json("/services/things-todo-service/medina-attraction.json", "/content/dam/sauditourism/cf/en/attractions/medina-attraction");

    context.addModelsForClasses(ThingToDoModel.class, AttractionCFModel.class);

    var attractionAdapter = new ThingToDoAttractionCFAdapter();

    var adapterFactory = new ThingToDoModelAdapterFactory();
    List<ThingToDoCFAdapter> adapters = new ArrayList<>();
    adapters.add(attractionAdapter);

    adapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel");

    context.registerService(AdapterFactory.class, adapterFactory, properties);

    properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);

    thingsToDoFiltersChain = new ThingsToDoFiltersChainImpl();
    thingsToDoFiltersChain.setFilters(Arrays.asList(new DestinationsFilter(), new CategoriesFilter(), new SeasonsFilter(), new StartDateFilter(), new EndDateFilter()));

    context.registerService(UserService.class, userService);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(AdminSettingsService.class, adminSettingsService);
    context.registerService(QueryBuilder.class, queryBuilder);
    context.registerService(ThingToDoComparatorsChain.class, comparatorsChain);
    context.registerService(ThingsToDoFiltersChain.class, thingsToDoFiltersChain);
    context.registerService(RunModeService.class, runModeService);
    context.registerService(ExperienceService.class, experienceService);
    context.registerService(FiltersService.class, filtersService);
    context.registerService(Cache.class, memCache);
    context.registerInjectActivateService(service);

    TagManager tagManager = context.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:classes-and-training/kayaking", "Kayaking", "Kayaking");

  }

  @Test
  public void getFilteredTTDShouldRaiseIfRequestIsNull(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
      service.getFilteredThingsToDo(null);
    });


    //Assert
    assertEquals("request is marked non-null but is null", exception.getMessage());
  }


  @Test
  public void getFilteredTTDShouldFilterByPaths(final AemContext aemContext){
    // Arrange
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(runModeService.isPublishRunMode()).thenReturn(true);
    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/medina-attraction").adaptTo(
      ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    var currentCalendar = Calendar.getInstance();
    Utils.mockContentFragmentElement(spyFragment, "title", "Medina Attraction");
    Utils.mockContentFragmentElement(spyFragment, "subtitle", "Subtitle");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "<p>this is event about descreption</p>");
    Utils.mockContentFragmentElement(spyFragment, "lat", "24.672612957506793");
    Utils.mockContentFragmentElement(spyFragment, "lng", "46.625595");
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "hideShareIcon", Boolean.FALSE);
    Utils.mockContentFragmentElement(spyFragment, "ticketType", "getTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "1000 SAR");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "/ per person");
    Utils.mockContentFragmentElement(
      spyFragment, "ticketDetails", "<p>Minumum number of 3 person</p>\\n");
    Utils.mockContentFragmentElement(spyFragment, "freePaid", "paid");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "https://dc.moc.gov.sa/home/event-tickets/90/food-culture/");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Buy Now");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    Utils.mockContentFragmentElement(spyFragment, "image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "s7image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "alt", "Author Image");
    Utils.mockContentFragmentElement(spyFragment, "sameTimeAcrossWeek", Boolean.FALSE);
    Utils.mockContentFragmentElement(spyFragment, "dailyStartTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "dailyEndTime", currentCalendar);
    Utils.mockContentFragmentElement(
      spyFragment,
      "timings",
      new String[] {
        "{\"dayLabel\":\"SUN\",\"startTimeLabel\":\"20:00\",\"endTimeLabel\":\"20:00\"}"
      });
    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/banner.png\",\"s7image\":\"https://scth.scene7.com/is/image/scth/banner.png\",\"alt\":\"Banner Alt\"}",
        "{\"type\":\"video\",\"video\":\"/content/dam/sauditourism/banner.mp4\",\"s7video\":\"/content/dam/sauditourism/banner.mp4\",\"thumbnail\":\"/content/dam/sauditourism/banner.png\",\"location\":\"Riyadh\"}"
      });
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/content/sauditourism/en/attractions/medina-attraction");
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});
    Utils.mockContentFragmentElement(spyFragment, "agesValue", "sauditourism:audience/16");
    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .paths(Arrays.asList("/content/dam/sauditourism/cf/en/attractions/medina-attraction"))
      .limit(10)
      .offset(0)
      .build();

    // Act
    final var response = service.getFilteredThingsToDo(request);

    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());

    final var thingToDo = response.getData().get(0);

    assertEquals("ATTRACTION", thingToDo.getType());
    assertEquals("Medina Attraction", thingToDo.getTitle());
    assertEquals("Subtitle", thingToDo.getSubtitle());
    assertEquals("<p>this is event about descreption</p>", thingToDo.getDescription());
    assertEquals("24.672612957506793", thingToDo.getLat());
    assertEquals("46.625595", thingToDo.getLng());
    assertTrue(thingToDo.getHideFavorite());
    assertFalse(thingToDo.getHideShareIcon());
    assertEquals("getTicket", thingToDo.getTicketType());
    assertEquals("1000 SAR", thingToDo.getTicketPrice());
    assertEquals("/ per person", thingToDo.getTicketPriceSuffix());
    assertEquals("<p>Minumum number of 3 person</p>\\n", thingToDo.getTicketDetails());

    assertNotNull(thingToDo.getTicketsCtaLink());
    assertEquals("https://dc.moc.gov.sa/home/event-tickets/90/food-culture/", thingToDo.getTicketsCtaLink().getUrl());
    assertEquals("Buy Now", thingToDo.getTicketsCtaLink().getText());

    assertNotNull(thingToDo.getAuthorImage());
    assertEquals("/content/dam/sauditourism/author.png", thingToDo.getAuthorImage().getFileReference());
    assertEquals("/content/dam/sauditourism/author.png", thingToDo.getAuthorImage().getS7fileReference());
    assertEquals("Author Image", thingToDo.getAuthorImage().getAlt());

    assertNotNull(thingToDo.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", thingToDo.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", thingToDo.getBannerImages().get(0).getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=260&hei=260 260w", thingToDo.getBannerImages().get(0).getBreakpoints().get(0).getSrcset());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=390&hei=390 390w", thingToDo.getBannerImages().get(0).getBreakpoints().get(1).getSrcset());
    assertEquals("Banner Alt", thingToDo.getBannerImages().get(0).getAlt());

    assertNotNull(thingToDo.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", thingToDo.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", thingToDo.getBannerImages().get(0).getS7fileReference());
    assertEquals("Banner Alt", thingToDo.getBannerImages().get(0).getAlt());

    assertNotNull(thingToDo.getPageLink());
    assertEquals("/content/sauditourism/en/attractions/medina-attraction", thingToDo.getPageLink().getUrl());

    assertNotNull(thingToDo.getCategories());
    assertEquals("Kayaking", thingToDo.getCategories().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/classes-and-training/kayaking.svg", thingToDo.getCategories().get(0).getIcon());


    assertEquals(false, thingToDo.getSameTimeAcrossWeek());
    assertEquals(currentCalendar, thingToDo.getDailyStartTime());
    assertEquals(currentCalendar, thingToDo.getDailyEndTime());

    assertEquals(1, thingToDo.getTimings().size());
    assertEquals("SUN", thingToDo.getTimings().get(0).getDayLabel());
    assertEquals("20:00", thingToDo.getTimings().get(0).getStartTimeLabel());
    assertEquals("20:00", thingToDo.getTimings().get(0).getEndTimeLabel());

    assertEquals("16", thingToDo.getAge());
    assertNotNull(thingToDo.getIdealFor());
    assertEquals("Kayaking", thingToDo.getIdealFor().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/classes-and-training/kayaking.svg", thingToDo.getCategories().get(0).getIcon());

    assertNotNull(response.getPagination());
    assertEquals(1, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }

  @Test
  public void getFilteredTTDShouldFilterAuto(final AemContext aemContext){
    //Arrange
    final var thingToDoResource= aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/medina-attraction");

    when(saudiTourismConfigs.getAttractionsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/attractions");
    when(saudiTourismConfigs.getActivitiesCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/activities");
    when(saudiTourismConfigs.getToursCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/tours");
    when(saudiTourismConfigs.getEventsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/events");
    when(saudiTourismConfigs.getPoisCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/pois");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {thingToDoResource}).iterator());
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(runModeService.isPublishRunMode()).thenReturn(true);

    final Function<ThingToDoModel, Calendar> extractor = ThingToDoModel::getPublishedDate;
    final var defaultComparator = Comparator.comparing(extractor).reversed();
    when(comparatorsChain.buildComparator(any(FetchThingsToDoRequest.class))).thenReturn(defaultComparator);

    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/medina-attraction").adaptTo(
      ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    var currentCalendar = Calendar.getInstance();
    Utils.mockContentFragmentElement(spyFragment, "title", "Medina Attraction");
    Utils.mockContentFragmentElement(spyFragment, "subtitle", "Subtitle");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "<p>this is event about descreption</p>");
    Utils.mockContentFragmentElement(spyFragment, "lat", "24.672612957506793");
    Utils.mockContentFragmentElement(spyFragment, "lng", "46.625595");
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "hideShareIcon", Boolean.FALSE);
    Utils.mockContentFragmentElement(spyFragment, "ticketType", "getTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "1000 SAR");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "/ per person");
    Utils.mockContentFragmentElement(
      spyFragment, "ticketDetails", "<p>Minumum number of 3 person</p>\\n");
    Utils.mockContentFragmentElement(spyFragment, "freePaid", "paid");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "https://dc.moc.gov.sa/home/event-tickets/90/food-culture/");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Buy Now");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    Utils.mockContentFragmentElement(spyFragment, "image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "s7image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "alt", "Author Image");
    Utils.mockContentFragmentElement(spyFragment, "sameTimeAcrossWeek", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "dailyStartTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "dailyEndTime", currentCalendar);
    Utils.mockContentFragmentElement(
      spyFragment,
      "timings",
      new String[] {
        "{\"dayLabel\":\"SUN\",\"startTimeLabel\":\"20:00\",\"endTimeLabel\":\"20:00\"}"
      });
    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/banner.png\",\"s7image\":\"https://scth.scene7.com/is/image/scth/banner.png\",\"alt\":\"Banner Alt\"}",
        "{\"type\":\"video\",\"video\":\"/content/dam/sauditourism/banner.mp4\",\"s7video\":\"/content/dam/sauditourism/banner.mp4\",\"thumbnail\":\"/content/dam/sauditourism/banner.png\",\"location\":\"Riyadh\"}"
      });
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/content/sauditourism/en/attractions/medina-attraction");
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});
    Utils.mockContentFragmentElement(spyFragment, "agesValue", "sauditourism:audience/16");
    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .type(Arrays.asList("attraction"))
      .limit(10)
      .offset(0)
      .build();

    //Act
    final var response = service.getFilteredThingsToDo(request);


    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());

    final var thingToDo = response.getData().get(0);

    assertEquals("ATTRACTION", thingToDo.getType());
    assertEquals("Medina Attraction", thingToDo.getTitle());
    assertEquals("Subtitle", thingToDo.getSubtitle());
    assertEquals("<p>this is event about descreption</p>", thingToDo.getDescription());
    assertEquals("24.672612957506793", thingToDo.getLat());
    assertEquals("46.625595", thingToDo.getLng());
    assertTrue(thingToDo.getHideFavorite());
    assertFalse(thingToDo.getHideShareIcon());
    assertEquals("getTicket", thingToDo.getTicketType());
    assertEquals("1000 SAR", thingToDo.getTicketPrice());
    assertEquals("/ per person", thingToDo.getTicketPriceSuffix());
    assertEquals("<p>Minumum number of 3 person</p>\\n", thingToDo.getTicketDetails());

    assertNotNull(thingToDo.getTicketsCtaLink());
    assertEquals("https://dc.moc.gov.sa/home/event-tickets/90/food-culture/", thingToDo.getTicketsCtaLink().getUrl());
    assertEquals("Buy Now", thingToDo.getTicketsCtaLink().getText());

    assertNotNull(thingToDo.getAuthorImage());
    assertEquals("/content/dam/sauditourism/author.png", thingToDo.getAuthorImage().getFileReference());
    assertEquals("/content/dam/sauditourism/author.png", thingToDo.getAuthorImage().getS7fileReference());
    assertEquals("Author Image", thingToDo.getAuthorImage().getAlt());

    assertNotNull(thingToDo.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", thingToDo.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", thingToDo.getBannerImages().get(0).getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=260&hei=260 260w", thingToDo.getBannerImages().get(0).getBreakpoints().get(0).getSrcset());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=390&hei=390 390w", thingToDo.getBannerImages().get(0).getBreakpoints().get(1).getSrcset());
    assertEquals("Banner Alt", thingToDo.getBannerImages().get(0).getAlt());

    assertNotNull(thingToDo.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", thingToDo.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", thingToDo.getBannerImages().get(0).getS7fileReference());
    assertEquals("Banner Alt", thingToDo.getBannerImages().get(0).getAlt());

    assertNotNull(thingToDo.getPageLink());
    assertEquals("/content/sauditourism/en/attractions/medina-attraction", thingToDo.getPageLink().getUrl());

    assertNotNull(thingToDo.getCategories());
    assertEquals("Kayaking", thingToDo.getCategories().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/classes-and-training/kayaking.svg", thingToDo.getCategories().get(0).getIcon());

    assertEquals(true, thingToDo.getSameTimeAcrossWeek());
    assertEquals(currentCalendar, thingToDo.getDailyStartTime());
    assertEquals(currentCalendar, thingToDo.getDailyEndTime());

    assertEquals("16", thingToDo.getAge());
    assertNotNull(thingToDo.getIdealFor());
    assertEquals("Kayaking", thingToDo.getIdealFor().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/classes-and-training/kayaking.svg", thingToDo.getCategories().get(0).getIcon());

    assertNotNull(response.getPagination());
    assertEquals(1, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }

  @Test
  public void getFilteredTTDShouldFilterExperiences(final AemContext aemContext) throws IOException {

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .type(Arrays.asList("experience"))
      .destination("riyadh")
      .limit(10)
      .offset(0)
      .build();
    Map<String, Object> queryStrings = new HashMap<String, Object>();
    queryStrings.put("lang", "en");
    queryStrings.put("city", "riyadh");
    String mockJsonResponse = "{"
      + "\"data\": ["
      + "    {"
      + "        \"id\": \"12345\","
      + "        \"name\": \"Safari Tour in Riyadh Dune\","
      + "        \"city\": \"Riyadh\","
      + "        \"description\": \"Join us in an adventure...\","
      + "        \"background_image\": \"/path/to/image\","
      + "        \"package_url\": \"/path/to/page\","
      + "        \"list_price\": 2334"
      + "    }"
      + "]"
      + "}";
    JsonElement mockJsonElement = JsonParser.parseString(mockJsonResponse);
    when(experienceService.getAllExperiences(queryStrings)).thenReturn(mockJsonElement);;

    // Act
    final var response = service.getFilteredThingsToDo(request);

    // Verify that the mock service was called with the correct parameters
    verify(experienceService).getAllExperiences(queryStrings);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getData());
    assertEquals(1, response.getData().size()); // check if one item is returned

    final var thingToDo = response.getData().get(0);
    assertEquals("EXPERIENCE", thingToDo.getType());
    assertEquals("Safari Tour in Riyadh Dune", thingToDo.getTitle());
    assertEquals("Join us in an adventure...", thingToDo.getDescription());
    assertEquals("Riyadh", thingToDo.getDestination().getTitle());
    assertEquals("2334", thingToDo.getTicketPrice());
    assertEquals("/path/to/image", thingToDo.getBannerImages().get(0).getDesktopImage());
    assertEquals("/path/to/image", thingToDo.getBannerImages().get(0).getMobileImage());
    assertEquals("/path/to/page", thingToDo.getTicketsCtaLink().getUrl());
    assertEquals(true, thingToDo.getTicketsCtaLink().isTargetInNewWindow());
    assertEquals("/path/to/page", thingToDo.getPageLink().getUrl());
    assertEquals(true, thingToDo.getPageLink().isTargetInNewWindow());
  }
}