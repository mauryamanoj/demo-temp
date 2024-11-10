package com.saudi.tourism.core.services.events.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.events.v1.comparators.EventComparatorsChain;
import com.saudi.tourism.core.services.events.v1.filters.DestinationFilter;
import com.saudi.tourism.core.services.events.v1.filters.EndDateFilter;
import com.saudi.tourism.core.services.events.v1.filters.EventFiltersChain;
import com.saudi.tourism.core.services.events.v1.filters.EventFiltersChainImpl;
import com.saudi.tourism.core.services.events.v1.filters.PathsFilter;
import com.saudi.tourism.core.services.events.v1.filters.SeasonFilter;
import com.saudi.tourism.core.services.events.v1.filters.StartDateFilter;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventsCFServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private SearchResult searchResult;

  @Mock
  private EventFiltersChainImpl eventFiltersChain;

  @Mock
  private EventComparatorsChain eventComparatorsChain;

  @Mock
  private RunModeService runModeService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private final ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case "event":
          return "Event";
        default:
          return "fake_i18n_value";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  private EventsCFService service = new EventsCFServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) throws InvalidTagFormatException {
    aemContext.load().json("/services/events-service/events-root.json", "/content/dam/sauditourism/cf/en/events");
    aemContext.load().json("/services/events-service/food-culture-festival.json", "/content/dam/sauditourism/cf/en/events/food-culture-festival");

    eventFiltersChain = new EventFiltersChainImpl();
    eventFiltersChain.setFilters(Arrays.asList(new DestinationFilter(), new EndDateFilter(), new PathsFilter(), new SeasonFilter(), new StartDateFilter()));
    aemContext.registerService(UserService.class, userService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(QueryBuilder.class, queryBuilder);
    aemContext.registerService(EventFiltersChain.class, eventFiltersChain);
    aemContext.registerService(EventComparatorsChain.class, eventComparatorsChain);
    aemContext.registerService(RunModeService.class, runModeService);
    var properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerInjectActivateService(service);

    aemContext.addModelsForClasses(EventCFModel.class);

    final var tagManager = aemContext.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:classes-and-training/kayaking", "Kayaking", "Kayaking");
  }

  @Test
  public void getFilteredEventsShouldRaiseIfRequestIsNull(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
        service.getFilteredEvents(null);
    });


    //Assert
    assertEquals("request is marked non-null but is null", exception.getMessage());
  }

  @Test
  public void getFilteredEventsShouldRaiseIfLocaleIsNull(){
    // Arrange

    // Act
    final Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              service.getFilteredEvents(FetchEventsRequest.builder().build());
            });

    //Assert
    assertEquals("resourceResolver is marked non-null but is null", exception.getMessage());
  }

  @Test
  public void getFilteredEventsShouldReturnEvents(final AemContext aemContext){
    // Arrange
    final var eventResource= aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/food-culture-festival");

    when(saudiTourismConfigs.getEventsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/events");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {eventResource}).iterator());
    when(runModeService.isPublishRunMode()).thenReturn(true);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    final Function<Event, Calendar> extractor = Event::getStartDate;
    final var defaultComparator = Comparator.comparing(extractor);
    when(eventComparatorsChain.buildComparator(any(FetchEventsRequest.class))).thenReturn(defaultComparator);

    final var currentCalendar = Calendar.getInstance();

    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/food-culture-festival").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    Utils.mockContentFragmentElement(spyFragment, "title", "Food Culture Festival");
    Utils.mockContentFragmentElement(spyFragment, "subtitle", "Subtitle");
    Utils.mockContentFragmentElement(spyFragment, "lat", "24.672612957506793");
    Utils.mockContentFragmentElement(spyFragment, "lng", "46.625595");
    Utils.mockContentFragmentElement(spyFragment, "startDate", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "endDate", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "hideShareIcon", Boolean.FALSE);
    Utils.mockContentFragmentElement(spyFragment, "dailyStartTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "dailyEndTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "weekendEvent", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "featuredInMap", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "sameTimeAcrossWeek", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/alula");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "<p>this is event about descreption</p>");
    Utils.mockContentFragmentElement(spyFragment, "image360", "/content/dam/sauditourism/placeholder.jpg");
    Utils.mockContentFragmentElement(spyFragment, "ticketType", "getTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "1000 SAR");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "/ per person");
    Utils.mockContentFragmentElement(
        spyFragment, "ticketDetails", "<p>Minumum number of 3 person</p>\\n");
    Utils.mockContentFragmentElement(spyFragment, "freePaid", "paid");
    Utils.mockContentFragmentElement(spyFragment, "eventType", "tier-1");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "https://dc.moc.gov.sa/home/event-tickets/90/food-culture/");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Buy Now");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    Utils.mockContentFragmentElement(spyFragment, "season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");
    Utils.mockContentFragmentElement(spyFragment, "titleImage", "/content/dam/sauditourism/favicon.png");
    Utils.mockContentFragmentElement(spyFragment, "s7titleImage", "/content/dam/sauditourism/favicon.png");
    Utils.mockContentFragmentElement(spyFragment, "altTitleImage", "Alt Title Image");
    Utils.mockContentFragmentElement(spyFragment, "image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "s7image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "alt", "Author Image");
    Utils.mockContentFragmentElement(
        spyFragment,
        "images",
        new String[] {
          "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/banner.png\",\"s7image\":\"https://scth.scene7.com/is/image/scth/banner.png\",\"alt\":\"Banner Alt\"}",
          "{\"type\":\"video\",\"video\":\"/content/dam/sauditourism/banner.mp4\",\"s7video\":\"/content/dam/sauditourism/banner.mp4\",\"thumbnail\":\"/content/dam/sauditourism/banner.png\",\"location\":\"Riyadh\"}"
        });
    Utils.mockContentFragmentElement(
      spyFragment,
      "timings",
      new String[] {
        "{\"dayLabel\":\"SUN\",\"startTimeLabel\":\"20:00\",\"endTimeLabel\":\"20:00\"}"
      });
    Utils.mockContentFragmentElement(
      spyFragment,
      "agesValue",
      new String[] {
        "sauditourism:audience/16+"
      });
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/conten/sauditourism/en/event");
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var request = FetchEventsRequest.builder()
      .locale("en")
      .limit(10)
      .offset(0)
      .build();

    // Act
    final var response = service.getFilteredEvents(request);

    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());

    final var event = response.getData().get(0);

    assertEquals("EVENT", event.getType());
    assertEquals("Food Culture Festival", event.getTitle());
    assertEquals("Subtitle", event.getSubtitle());
    assertEquals("24.672612957506793", event.getLat());
    assertEquals("46.625595", event.getLng());
    assertEquals(currentCalendar, event.getStartDate());
    assertEquals(currentCalendar, event.getEndDate());
    assertTrue(event.getHideFavorite());
    assertFalse(event.getHideShareIcon());
    assertEquals("getTicket", event.getTicketType());
    assertEquals("1000 SAR", event.getTicketPrice());
    assertEquals("/ per person", event.getTicketPriceSuffix());
    assertEquals("<p>Minumum number of 3 person</p>\\n", event.getTicketDetails());

    assertEquals("sauditourism:audience/16+", event.getTargetGroupTags().get(0));
    assertEquals(true, event.getWeekendEvent());
    assertEquals(true, event.getSameTimeAcrossWeek());
    assertEquals(true, event.getFeaturedInMap());
    assertEquals(currentCalendar, event.getDailyStartTime());
    assertEquals(currentCalendar, event.getDailyEndTime());
    assertEquals("OpeningHoursValue(dayLabel=SUN, startTimeLabel=20:00, endTimeLabel=20:00)",
      event.getTimings().get(0).toString());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", event.getImage360());
    assertEquals("al-ahsa", event.getCityId());
    assertEquals("riyadh-season", event.getSeasonId());
    assertEquals("<p>this is event about descreption</p>", event.getEventDescription());
    assertEquals("https://dc.moc.gov.sa/home/event-tickets/90/food-culture/", event.getTicketCTALink());

    assertNotNull(event.getTicketsCtaLink());
    assertEquals("https://dc.moc.gov.sa/home/event-tickets/90/food-culture/", event.getTicketsCtaLink().getUrl());
    assertEquals("Buy Now", event.getTicketsCtaLink().getText());

    assertNotNull(event.getTitleImage());
    assertEquals("/content/dam/sauditourism/favicon.png", event.getTitleImage().getFileReference());
    assertEquals("/content/dam/sauditourism/favicon.png", event.getTitleImage().getS7fileReference());
    assertEquals("Alt Title Image", event.getTitleImage().getAlt());

    assertNotNull(event.getAuthorImage());
    assertEquals("/content/dam/sauditourism/author.png", event.getAuthorImage().getFileReference());
    assertEquals("/content/dam/sauditourism/author.png", event.getAuthorImage().getS7fileReference());
    assertEquals("Author Image", event.getAuthorImage().getAlt());

    assertNotNull(event.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", event.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", event.getBannerImages().get(0).getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=260&hei=260 260w", event.getBannerImages().get(0).getBreakpoints().get(0).getSrcset());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png:crop-600x600?defaultImage=banner.png&wid=390&hei=390 390w", event.getBannerImages().get(0).getBreakpoints().get(1).getSrcset());
    assertEquals("Banner Alt", event.getBannerImages().get(0).getAlt());

    assertNotNull(event.getBannerImages());
    assertEquals("/content/dam/sauditourism/banner.png", event.getBannerImages().get(0).getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/banner.png", event.getBannerImages().get(0).getS7fileReference());
    assertEquals("Banner Alt", event.getBannerImages().get(0).getAlt());

    assertNotNull(event.getCategories());
    assertEquals("Kayaking", event.getCategories().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/classes-and-training/kayaking.svg", event.getCategories().get(0).getIcon());

    assertNotNull(response.getPagination());
    assertEquals(1, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }

  @Test
  public void getFilteredEventsShouldFilterByPaths(final AemContext aemContext){
    // Arrange
    final var eventResource= aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/food-culture-festival");

    when(saudiTourismConfigs.getEventsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/events");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {eventResource}).iterator());
    when(runModeService.isPublishRunMode()).thenReturn(true);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);

    final Function<Event, Calendar> extractor = Event::getStartDate;
    final var defaultComparator = Comparator.comparing(extractor);
    when(eventComparatorsChain.buildComparator(any(FetchEventsRequest.class))).thenReturn(defaultComparator);

    final var currentCalendar = Calendar.getInstance();

    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/food-culture-festival").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    Utils.mockContentFragmentElement(spyFragment, "title", "Food Culture Festival");
    Utils.mockContentFragmentElement(spyFragment, "subtitle", "Subtitle");
    Utils.mockContentFragmentElement(spyFragment, "lat", "24.672612957506793");
    Utils.mockContentFragmentElement(spyFragment, "lng", "46.625595");
    Utils.mockContentFragmentElement(spyFragment, "startDate", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "endDate", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "hideFavorite", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "hideShareIcon", Boolean.FALSE);
    Utils.mockContentFragmentElement(spyFragment, "ticketType", "getTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "1000 SAR");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "/ per person");
    Utils.mockContentFragmentElement(spyFragment, "dailyStartTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "dailyEndTime", currentCalendar);
    Utils.mockContentFragmentElement(spyFragment, "weekendEvent", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "featuredInMap", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "sameTimeAcrossWeek", Boolean.TRUE);
    Utils.mockContentFragmentElement(spyFragment, "season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/alula");
    Utils.mockContentFragmentElement(spyFragment, "aboutDescription", "<p>this is event about descreption</p>");
    Utils.mockContentFragmentElement(spyFragment, "image360", "/content/dam/sauditourism/placeholder.jpg");
    Utils.mockContentFragmentElement(
      spyFragment, "ticketDetails", "<p>Minumum number of 3 person</p>\\n");
    Utils.mockContentFragmentElement(spyFragment, "freePaid", "paid");
    Utils.mockContentFragmentElement(spyFragment, "eventType", "tier-1");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "https://dc.moc.gov.sa/home/event-tickets/90/food-culture/");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Buy Now");
    Utils.mockContentFragmentElement(spyFragment, "locationValue", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    Utils.mockContentFragmentElement(spyFragment, "season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");
    Utils.mockContentFragmentElement(spyFragment, "titleImage", "/content/dam/sauditourism/favicon.png");
    Utils.mockContentFragmentElement(spyFragment, "s7titleImage", "/content/dam/sauditourism/favicon.png");
    Utils.mockContentFragmentElement(spyFragment, "altTitleImage", "Alt Title Image");
    Utils.mockContentFragmentElement(spyFragment, "image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "s7image", "/content/dam/sauditourism/author.png");
    Utils.mockContentFragmentElement(spyFragment, "alt", "Author Image");
    Utils.mockContentFragmentElement(
      spyFragment,
      "images",
      new String[] {
        "{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/banner.png\",\"s7image\":\"https://scth.scene7.com/is/image/scth/banner.png\",\"alt\":\"Banner Alt\"}",
        "{\"type\":\"video\",\"video\":\"/content/dam/sauditourism/banner.mp4\",\"s7video\":\"/content/dam/sauditourism/banner.mp4\",\"thumbnail\":\"/content/dam/sauditourism/banner.png\",\"location\":\"Riyadh\"}"
      });
    Utils.mockContentFragmentElement(
      spyFragment,
      "timings",
      new String[] {
        "{\"dayLabel\":\"SUN\",\"startTimeLabel\":\"20:00\",\"endTimeLabel\":\"20:00\"}"
      });
    Utils.mockContentFragmentElement(
      spyFragment,
      "agesValue",
      new String[] {
        "sauditourism:audience/16+"
      });
    Utils.mockContentFragmentElement(spyFragment, "pagePath", "/conten/sauditourism/en/event");
    Utils.mockContentFragmentElement(spyFragment, "categories", new String[] {});

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    final var request = FetchEventsRequest.builder()
      .locale("en")
      .paths(Arrays.asList("/content/dam/sauditourism/cf/en/events/visit-saudi"))
      .limit(10)
      .offset(0)
      .build();

    // Act
    final var response = service.getFilteredEvents(request);

    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());
    assertTrue(CollectionUtils.isEmpty(response.getData()));

    assertNotNull(response.getPagination());
    assertEquals(0, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }

}