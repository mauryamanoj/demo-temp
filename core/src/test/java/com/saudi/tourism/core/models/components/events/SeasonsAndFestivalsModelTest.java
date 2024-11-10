package com.saudi.tourism.core.models.components.events;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.RepositoryException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SeasonsAndFestivalsModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/saudi-calendar";
  private static final String RESOURCE_PATH = "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/events_cards";
  private static final String EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH = "/content/sauditourism/en/events/the-italian-circus";
  private static final String EVENT_THE_ITALIAN_CIRCUS_CONTENT_PATH = "/content/sauditourism/en/events/the-italian-circus/jcr:content";
  private static final String EVENT_THE_BEACH_PAGE_PATH = "/content/sauditourism/en/events/the-beach";
  private static final String EVENT_THE_BEACH_CONTENT_PATH = "/content/sauditourism/en/events/the-beach/jcr:content";
  private static final String EVENT_BALLASMAR_AND_ALMAJARDA_ADVENTURE_PAGE_PATH = "/content/sauditourism/en/events/ballasmar-and-almajarda-adventures";
  private static final String EVENT_BALLASMAR_AND_ALMAJARDA_ADVENTURE_CONTENT_PATH = "/content/sauditourism/en/events/ballasmar-and-almajarda-adventures/jcr:content";
  private static final String SCENE_7_DOMAIN = "https://scene7.adobe.com/";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private EventService eventService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RegionCityService citiesService;

  private ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case "EVENTS":
          return "Events";
        default:
          return "dummy_i18n_traduction";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/pages/saudi-calendar.json", PAGE_PATH);
    aemContext.load().json("/components/events/the-italian-circus.json", EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH);
    aemContext.load().json("/components/events/the-beach.json", EVENT_THE_BEACH_PAGE_PATH);
    aemContext.load().json("/components/events/ballasmar-and-almajarda-adventures.json", EVENT_BALLASMAR_AND_ALMAJARDA_ADVENTURE_PAGE_PATH);

    aemContext.registerService(EventService.class, eventService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(RegionCityService.class, citiesService);
    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(saudiTourismConfigs.getScene7Domain()).thenReturn(SCENE_7_DOMAIN);
  }

  @Test
  void shouldReturnSeasonsAndFestivalsData(final AemContext aemContext) throws RepositoryException {
    // Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(RESOURCE_PATH);

    SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
    final Calendar startDateCalendar = Calendar.getInstance();
    final Calendar endDateCalendar = Calendar.getInstance();
    endDateCalendar.add(Calendar.YEAR, 1);

    final String startDate = aemDateFormat.format(startDateCalendar.getTime());
    final String endDate = aemDateFormat.format(endDateCalendar.getTime());

    final EventDetail italianCircusEvent = aemContext.resourceResolver().getResource(EVENT_THE_ITALIAN_CIRCUS_CONTENT_PATH).adaptTo(EventDetail.class);
    italianCircusEvent.setCalendarStartDate(startDate);
    italianCircusEvent.setStartDateCal(startDateCalendar);
    italianCircusEvent.setStartDateMonth(CommonUtils.getDateMonth(startDate, Constants.FORMAT_MONTH));
    italianCircusEvent.setStartDateDay(CommonUtils.convertDateToSTring(startDate, Constants.FORMAT_DAY));
    italianCircusEvent.setCalendarEndDate(endDate);
    italianCircusEvent.setEndDateCal(endDateCalendar);
    italianCircusEvent.setEndDateMonth(CommonUtils.getDateMonth(endDate, Constants.FORMAT_MONTH));
    italianCircusEvent.setEndDateDay(CommonUtils.convertDateToSTring(endDate, Constants.FORMAT_DAY));

    final EventDetail theBeachEvent = aemContext.resourceResolver().getResource(EVENT_THE_BEACH_CONTENT_PATH).adaptTo(EventDetail.class);
    theBeachEvent.setCalendarStartDate(startDate);
    theBeachEvent.setStartDateCal(startDateCalendar);
    theBeachEvent.setStartDateMonth(CommonUtils.getDateMonth(startDate, Constants.FORMAT_MONTH));
    theBeachEvent.setStartDateDay(CommonUtils.convertDateToSTring(startDate, Constants.FORMAT_DAY));
    theBeachEvent.setCalendarEndDate(endDate);
    theBeachEvent.setEndDateCal(endDateCalendar);
    theBeachEvent.setEndDateMonth(CommonUtils.getDateMonth(endDate, Constants.FORMAT_MONTH));
    theBeachEvent.setEndDateDay(CommonUtils.convertDateToSTring(endDate, Constants.FORMAT_DAY));

    final EventDetail ballaSmarEvent = aemContext.resourceResolver().getResource(EVENT_BALLASMAR_AND_ALMAJARDA_ADVENTURE_CONTENT_PATH).adaptTo(EventDetail.class);
    ballaSmarEvent.setCalendarStartDate(startDate);
    ballaSmarEvent.setStartDateCal(startDateCalendar);
    ballaSmarEvent.setStartDateMonth(CommonUtils.getDateMonth(startDate, Constants.FORMAT_MONTH));
    ballaSmarEvent.setStartDateDay(CommonUtils.convertDateToSTring(startDate, Constants.FORMAT_DAY));
    ballaSmarEvent.setCalendarEndDate(endDate);
    ballaSmarEvent.setEndDateCal(endDateCalendar);
    ballaSmarEvent.setEndDateMonth(CommonUtils.getDateMonth(endDate, Constants.FORMAT_MONTH));
    ballaSmarEvent.setEndDateDay(CommonUtils.convertDateToSTring(endDate, Constants.FORMAT_DAY));

    final List<EventDetail> eventDetails = new ArrayList<>();
    eventDetails.add(italianCircusEvent);
    eventDetails.add(theBeachEvent);
    eventDetails.add(ballaSmarEvent);

    final EventListModel eventListModel = new EventListModel();
    eventListModel.setData(eventDetails);

    when(eventService.getFilteredEvents(any(EventsRequestParams.class))).thenReturn(eventListModel);

    final String[] displayMonthName = new DateFormatSymbols().getShortMonths();

    // Act
    final SeasonsAndFestivalsModel model = aemContext.request().adaptTo(SeasonsAndFestivalsModel.class);
    final String json = model.getJson();
    final SeasonsAndFestivalsModel data = gson.fromJson(json, SeasonsAndFestivalsModel.class);

    // Assert
    assertEquals(2, data.getColumns());
    assertEquals("Seasons and Fest", model.getHeadline());
    assertEquals(2, data.getCards().size());
    assertEquals("false", data.getHideOrnament());

    assertEquals("riyadh-season", data.getCards().get(0).getSeasonsId());
    assertEquals("/en/calendar/saudi-calendar/riyadh-season", data.getCards().get(0).getFiltersPath());
    assertEquals("/content/dam/nB02IiBTQzwX1SGJDeL7fmTefMGybiM5GgrfsRPu.jpg", data.getCards().get(0).getImage().getS7fileReference());
    assertEquals("/content/dam/nB02IiBTQzwX1SGJDeL7fmTefMGybiM5GgrfsRPu.jpg", data.getCards().get(0).getImage().getFileReference());
    assertEquals(String.format("%02d", startDateCalendar.get(Calendar.DAY_OF_MONTH)), data.getCards().get(0).getStartDate());
    assertEquals(displayMonthName[startDateCalendar.get(Calendar.MONTH)], data.getCards().get(0).getStartMonthDisplayName());
    assertEquals(startDateCalendar.get(Calendar.YEAR), data.getCards().get(0).getStartYear());
    assertEquals(String.format("%02d", endDateCalendar.get(Calendar.DAY_OF_MONTH)), data.getCards().get(0).getEndDate());
    assertEquals(displayMonthName[endDateCalendar.get(Calendar.MONTH)], data.getCards().get(0).getEndMonthDisplayName());
    assertEquals(endDateCalendar.get(Calendar.YEAR), data.getCards().get(0).getEndYear());
    assertEquals("Riyadh Season", data.getCards().get(0).getTitle());
    assertEquals("A lot of interesting  events", data.getCards().get(0).getDescription());
    assertEquals(3, data.getCards().get(0).getEventCount());
    assertEquals(3, data.getCards().get(0).getEventImages().size());
    assertEquals("Events", data.getCards().get(0).getEventsText());
    assertEquals("/content/dam/saudi-tourism/media/events-/shutterstock_518827906.jpg", data.getCards().get(0).getEventImages().get(0).getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/events-/ذا بيتش جدة.JPG", data.getCards().get(0).getEventImages().get(1).getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/homepage/760x570/event-slide-mobile-01.jpg", data.getCards().get(0).getEventImages().get(2).getFileReference());

    assertEquals("jeddah-season", data.getCards().get(1).getSeasonsId());
    assertEquals("/en/calendar/saudi-calendar/jeddah-season", data.getCards().get(1).getFiltersPath());
    assertEquals("https://scth.scene7.com/is/image/scth/jeddah_hero_desktop-2", data.getCards().get(1).getImage().getS7fileReference());
    assertEquals("/content/dam/summer-season/jeddah_hero_desktop.png", data.getCards().get(1).getImage().getFileReference());
    assertEquals(String.format("%02d", startDateCalendar.get(Calendar.DAY_OF_MONTH)), data.getCards().get(1).getStartDate());
    assertEquals(displayMonthName[startDateCalendar.get(Calendar.MONTH)], data.getCards().get(1).getStartMonthDisplayName());
    assertEquals(startDateCalendar.get(Calendar.YEAR), data.getCards().get(1).getStartYear());
    assertEquals(String.format("%02d", endDateCalendar.get(Calendar.DAY_OF_MONTH)), data.getCards().get(1).getEndDate());
    assertEquals(displayMonthName[endDateCalendar.get(Calendar.MONTH)], data.getCards().get(1).getEndMonthDisplayName());
    assertEquals(endDateCalendar.get(Calendar.YEAR), data.getCards().get(0).getEndYear());
    assertEquals("Jeddah Season", data.getCards().get(1).getTitle());
    assertEquals("Jeddah Season", data.getCards().get(1).getDescription());
  }
}