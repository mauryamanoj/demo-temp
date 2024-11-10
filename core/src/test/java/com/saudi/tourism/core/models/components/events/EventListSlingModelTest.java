package com.saudi.tourism.core.models.components.events;


import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.WCMException;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.nav.v1.BreadcrumbModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.impl.AdminSettingsServiceImpl;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.I18N_MULTIPLE_DESTINATION_KEY;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_APPLY;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_CLEAR_ALL;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_DETAIL_DATE_FROM;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_DETAIL_DATE_TO;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_EVENTS_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_EVENT_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_FILTERS;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SHOW_ALL;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SHOW_LESS;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_THREE_TO_TEN_EVENTS_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_TWO_EVENTS_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventListSlingModelTest {

  private static final String CURRENT_PAGE = "/content/sauditourism/en/calendar";
  private static final String CURRENT_RESOURCE = "/content/sauditourism/en/calendar/jcr:content/root/responsivegrid/c08_events";

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private AdminSettingsServiceImpl adminSettingsService;

  @Mock
  private AdminPageOption adminPageOption;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RunModeService runModeService;

  @Mock
  private PathProcessor pathProcessor;

  private ResourceBundle i18nBundle = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key){
        case I18_KEY_SHOW_ALL:
          return "Show all";
        case I18_KEY_SHOW_LESS:
          return "Show less";
        case I18_KEY_DETAIL_DATE_FROM:
          return "From";
        case I18_KEY_DETAIL_DATE_TO:
          return "To";
        case I18_KEY_EVENTS_FOUND:
          return "$0 Events";
        case I18_KEY_EVENT_FOUND:
          return "$0 Event";
        case I18_KEY_TWO_EVENTS_FOUND:
          return "$0 Events";
        case I18_KEY_THREE_TO_TEN_EVENTS_FOUND:
          return "$0 Events";
        case I18_KEY_FILTERS:
          return "Filters";
        case I18_KEY_CLEAR_ALL:
          return "Clear all";
        case I18_KEY_APPLY:
          return "Apply";
        case I18N_MULTIPLE_DESTINATION_KEY:
          return "Multiple destinations";
        default:
          return "fake_translation";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  void setUp(final AemContext aemContext) throws WCMException {
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider,
      ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(PathProcessor.class, pathProcessor);
    aemContext.registerService(RunModeService.class, runModeService);

    aemContext.pageManager().create("/", "content", "cq:Page", "content");
    aemContext.pageManager().create("/content", "sauditourism", "cq:Page", "sauditourism root page");
    aemContext.pageManager().create("/content/sauditourism", "en", "cq:Page", "Home");
    aemContext.load().json("/pages/calendar.json", CURRENT_PAGE);

    aemContext.addModelsForClasses(EventListSlingModel.class);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");
    when(pathProcessor.accepts(any(String.class), any(SlingHttpServletRequest.class))).thenReturn(Boolean.TRUE);
    when(pathProcessor.map(eq("/content/sauditourism/en.html"), any(SlingHttpServletRequest.class))).thenReturn("/en.html");
    when(pathProcessor.map(eq("/content/sauditourism/en/calendar.html"), any(SlingHttpServletRequest.class))).thenReturn("/en/calendar.html");
    when(runModeService.isPublishRunMode()).thenReturn(true);
    lenient().when(adminSettingsService.getAdminOptions(anyString(), anyString())).thenReturn(adminPageOption);
    AdminUtil.setAdminSettingsService(adminSettingsService);

  }

  @Test
  void testEventListSlingModel(final AemContext aemContext){
    //Arrange
    aemContext.currentResource(CURRENT_RESOURCE);

    //Act
    final EventListSlingModel model = aemContext.request().adaptTo(EventListSlingModel.class);
    final String json = model.getJson();
    final EventListSlingModel data = gson.fromJson(json, EventListSlingModel.class);

    //Assert
    //Filters
    final List<EventsFilterModel> filters = data.getFilters();
    assertNotNull(filters);
    assertEquals(6, filters.size());
    assertEquals("City, Region", filters.get(0).getPlaceHolderText());
    assertEquals("Location", filters.get(0).getFilterTitle());
    assertEquals("city", filters.get(0).getType());
    assertEquals("checkbox-group", filters.get(0).getTypeGroup());
    assertEquals("location", filters.get(0).getIcon());
    assertEquals("Show all", filters.get(0).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(0).getShowAllButton().getShowLessLabel());

    assertNull(filters.get(1).getPlaceHolderText());
    assertEquals("Season", filters.get(1).getFilterTitle());
    assertEquals("season", filters.get(1).getType());
    assertEquals("checkbox-group", filters.get(1).getTypeGroup());
    assertEquals("calendar", filters.get(1).getIcon());
    assertEquals("Show all", filters.get(1).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(1).getShowAllButton().getShowLessLabel());

    assertNull(filters.get(2).getPlaceHolderText());
    assertEquals("Event Type", filters.get(2).getFilterTitle());
    assertEquals("freePaid", filters.get(2).getType());
    assertEquals("checkbox-toggle", filters.get(2).getTypeGroup());
    assertEquals("money", filters.get(2).getIcon());
    assertEquals("Show all", filters.get(2).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(2).getShowAllButton().getShowLessLabel());

    assertNull(filters.get(3).getPlaceHolderText());
    assertNotNull(filters.get(3).getDatePlaceholder());
    assertEquals("From", filters.get(3).getDatePlaceholder().getStartDate());
    assertEquals("To", filters.get(3).getDatePlaceholder().getEndDate());
    assertEquals("custom", filters.get(3).getDatePlaceholder().getCustom());
    assertEquals("Date", filters.get(3).getFilterTitle());
    assertEquals("date", filters.get(3).getType());
    assertEquals("checkbox-group-with-date", filters.get(3).getTypeGroup());
    assertEquals("calendar", filters.get(3).getIcon());
    assertEquals("Show all", filters.get(3).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(3).getShowAllButton().getShowLessLabel());

    assertEquals("Event Category", filters.get(4).getPlaceHolderText());
    assertEquals("Interests", filters.get(4).getFilterTitle());
    assertEquals("category", filters.get(4).getType());
    assertEquals("checkbox-group", filters.get(4).getTypeGroup());
    assertEquals("interest", filters.get(4).getIcon());
    assertEquals("Show all", filters.get(4).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(4).getShowAllButton().getShowLessLabel());

    assertNull(filters.get(5).getPlaceHolderText());
    assertEquals("Audience", filters.get(5).getFilterTitle());
    assertEquals("target", filters.get(5).getType());
    assertEquals("checkbox-group", filters.get(5).getTypeGroup());
    assertEquals("user", filters.get(5).getIcon());
    assertEquals("Show all", filters.get(5).getShowAllButton().getShowAllLabel());
    assertEquals("Show less", filters.get(5).getShowAllButton().getShowLessLabel());

    // Other properties
    assertEquals("Events just for you", data.getTitle());
    assertEquals("Load More", data.getLoadMoreButtonLabel());
    assertEquals("Learn More", data.getEventDetailsLinkLabel());
    assertEquals("/bin/api/v1/events?locale=en", data.getEndpoint());
    assertEquals("29", data.getResultsPerPageAuth());
    assertEquals(29, data.getResultsPerPage());
    assertNotNull(data.getNoResultsMessage());
    assertEquals("Sorry!", data.getNoResultsMessage().getFirstLine());
    assertEquals("\uD83D\uDE1E", data.getNoResultsMessage().getIcon());
    assertEquals("There are no results. Try looking up for something else.", data.getNoResultsMessage().getSmallMessage());
    assertEquals("No Events Found", data.getNoResultTitle());
    assertEquals("Search for an event", data.getSearchEventPlaceholder());
    assertEquals("$0 Events", data.getResultsTitle());
    assertEquals("$0 Event", data.getResultTitle());
    assertEquals("$0 Events", data.getTwoResultsTitle());
    assertEquals("$0 Events", data.getThreeToTenResultsTitle());
    assertEquals("Filters", data.getFiltersTitle());
    assertEquals("Clear all", data.getClearFiltersButtonLabel());
    assertEquals("Apply", data.getApplyFiltersButtonLabel());
    assertEquals("Multiple destinations", data.getMultipleLocationsCopy());
    assertEquals("EventsFiltersForm", data.getDataLayerName());
    assertEquals("Found Text", data.getFoundText());
    assertEquals("Search for an event", data.getSearchEventPlaceholder());

    final BreadcrumbModel breadcrumbs = data.getBreadcrumbs();
    assertNotNull(breadcrumbs);
    assertEquals(2, breadcrumbs.getItems().size());

    assertEquals("/en", breadcrumbs.getItems().get(0).getLink().getUrl());
    assertEquals("Home", breadcrumbs.getItems().get(0).getTitle());

    assertEquals("/en/calendar", breadcrumbs.getItems().get(1).getLink().getUrl());
    assertEquals("Festivals & Events", breadcrumbs.getItems().get(1).getTitle());
  }
}