package com.saudi.tourism.core.models.components.events;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.gson.CalendarAdapter;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Calendar;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class StickyEventModelTest {
  private final Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .registerTypeHierarchyAdapter(Calendar.class, new CalendarAdapter())
          .create();
  private static final String SCENE_7_DOMAIN = "https://scene7.adobe.com/";
  private static final String EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH = "/content/sauditourism/en/events/the-italian-circus";
  private static final String EVENT_THE_ITALIAN_CIRCUS_CONTENT_PATH = "/content/sauditourism/en/events/the-italian-circus/jcr:content";

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  private ResourceBundle i18n =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
          switch (key) {
            case "About this event":
              return "About this event";
            case I18nConstants.I18_KEY_CATEGORY:
              return "Activities";
            case I18nConstants.I18_KEY_AUDIENCE:
              return "Audience";
            case I18nConstants.I18_KEY_ACCESSIBILTY:
              return "Accessibilty";
            case I18nConstants.I18_KEY_DURATION:
              return "Duration";
            case I18nConstants.I18_KEY_WORKING_HOURS:
              return "Working Hours";
            case I18nConstants.I18_KEY_DETAIL_EVENT:
              return "Event details";
            case I18nConstants.I18_KEY_SHARE_EVENT:
              return "Share Event";
            case I18nConstants.I18_KEY_COPY_LINK:
              return "Copy Link";
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
  void setUp(final AemContext aemContext) throws InvalidTagFormatException {
    aemContext.load().json("/components/events/the-italian-circus.json", EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH);

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerService(SlingSettingsService.class, settingsService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(saudiTourismConfigs.getScene7Domain()).thenReturn(SCENE_7_DOMAIN);
    when(saudiTourismConfigs.getGoogleMapsKey()).thenReturn("google_map_key");
    TagManager tagManager = aemContext.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:events/sightseeing", "sightseeing", "sightseeing");
    tagManager.createTag("sauditourism:events/shopping-retail", "shopping-retail", "shopping-retail");
    tagManager.createTag("sauditourism:events/adventure", "adventure", "adventure");
    tagManager.createTag("sauditourism:events/nature", "nature", "nature");
    tagManager.createTag("sauditourism:audience/12+", "12+", "12+");
  }

  @Test
  void shouldReturnSeasonsAndFestivalsData(final AemContext aemContext){
    //Arrange
    aemContext.currentPage(EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH);
    aemContext.currentResource(EVENT_THE_ITALIAN_CIRCUS_CONTENT_PATH);

    //Act
    final StickyEventModel model = aemContext.currentResource().adaptTo(StickyEventModel.class);
    final String json = model.getJson();
    final StickyEventModel data = gson.fromJson(json, StickyEventModel.class);

    //Assert
    //ClipBoard
    assertEquals("Copy Link", data.getCopyLinkText());
    assertEquals("Copy Clipboard Tooltip Text", data.getCopyClipboardTooltipText());

    //About calendar
    assertEquals("About this event", data.getTitleDetails().getMainTitle());
    assertEquals(
        "<p>In the Italian Circus, you will live its history and ancient background, with various shows along with family-friendly performances. \n"
            + "\n"
            + "*Entry is for covid immune only.</p>",
        data.getTitleDetails().getIntroDescription());
    assertEquals("Read more", data.getTitleDetails().getReadMoreButton());

    //Add to calendar
    assertEquals("The Italian Circus", data.getLocationName());
    assertEquals(4, data.getStartTimeCal().get(Calendar.HOUR));
    assertEquals(0, data.getStartTimeCal().get(Calendar.MINUTE));
    assertEquals(Calendar.PM, data.getStartTimeCal().get(Calendar.AM_PM));
    assertEquals(10, data.getEndTimeCal().get(Calendar.HOUR));
    assertEquals(0, data.getEndTimeCal().get(Calendar.MINUTE));
    assertEquals(Calendar.PM, data.getStartTimeCal().get(Calendar.AM_PM));
    assertFalse(data.getRoundTheClock());
    assertEquals("Add to calendar", data.getAddToCalendarText());

    //Event summary
    assertEquals("Event details", data.getEventSummaryTitle());
    assertEquals("Price", data.getPriceText());
    assertEquals("10", data.getPriceValue());
    assertEquals("Duration", data.getDurationText());
    assertEquals(2022, data.getCalendarStartDate().get(Calendar.YEAR));
    assertEquals(11, data.getCalendarStartDate().get(Calendar.MONTH));
    assertEquals(12, data.getCalendarStartDate().get(Calendar.DAY_OF_MONTH));
    assertEquals(2022, data.getCalendarEndDate().get(Calendar.YEAR));
    assertEquals(11, data.getCalendarEndDate().get(Calendar.MONTH));
    assertEquals(30, data.getCalendarEndDate().get(Calendar.DAY_OF_MONTH));
    assertEquals("Working Hours", data.getWorkingTimeText());
    assertEquals(4, data.getStartTimeCal().get(Calendar.HOUR));
    assertEquals(0, data.getStartTimeCal().get(Calendar.MINUTE));
    assertEquals(Calendar.PM, data.getStartTimeCal().get(Calendar.AM_PM));
    assertEquals(10, data.getEndTimeCal().get(Calendar.HOUR));
    assertEquals(0, data.getEndTimeCal().get(Calendar.MINUTE));
    assertEquals(Calendar.PM, data.getStartTimeCal().get(Calendar.AM_PM));
    assertFalse(data.getRoundTheClock());
    assertEquals("Activities", data.getActivitiesText());
    assertEquals(List.of("sightseeing", "shopping-retail", "adventure", "nature"), data.getActivitiesList());
    assertEquals("Audience", data.getSuitableForText());
    assertEquals(List.of("12+"), data.getSuitableForList());
    assertEquals("Accessibilty", data.getAccessibilityText());
    assertEquals("Ramps, etc", data.getAccessibiltyValue());

    //Location Info
    assertEquals("I am a location title, edit me", data.getLocationTitle());
    assertEquals("google", data.getMapType());
    assertEquals("42.490316622208574", data.getLongitude());
    assertEquals("18.223047365871544", data.getLatitude());
    assertEquals("1", data.getZoom());
    assertEquals("2293 King Abdul Aziz Rd، حي الفيصلية، 8475, Abha 62521, Arabie saoudite", data.getAddressInfo());
    assertEquals("Get Directions", data.getGetDirectionCtaText());

    //Book Now
    assertEquals("Buy Tickets", data.getEventLink().getCopy());
    assertEquals("https://e-ticket.app/p/italian-circus-28721", data.getEventLink().getUrl());
    assertFalse(data.getEventLink().isTargetInNewWindow());

    //Share Event
    assertEquals("Share Event", data.getShareButtonText());
    assertEquals("google_map_key", data.getGoogleMapsKey());
  }
}
