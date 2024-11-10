package com.saudi.tourism.core.models.components.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.SliderDataLayer;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HandPickedEventsModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/saudi-calendar";

  private final String CURRENT_RESOURCE = "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/events_cards_1407652442";

  private final String EVENT_DETAIL_1_PAGE = "/content/sauditourism/en/events/via-ferrata-and-hammock";

  private final String EVENT_DETAIL_2_PAGE = "/content/sauditourism/en/events/the-groves";

  private final String EVENT_DETAIL_3_PAGE = "/content/sauditourism/en/events/old-town-market-road";

  private final String EVENT_DETAIL_4_PAGE = "/content/sauditourism/en/events/elephant-rock-visit";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private UserService userService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RegionCityService citiesService;

  private ResourceBundle i18nBundle = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      return "fake_translation";
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @BeforeEach
  void setUp(final AemContext aemContext){
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider,
      ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(SlingSettingsService.class, settingsService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(RegionCityService.class, citiesService);
    aemContext.registerService(UserService.class, userService);

    aemContext.load().json("/pages/saudi-calendar.json", PAGE_PATH);
    aemContext.load().json("/components/events/via-ferrata-and-hammock.json", EVENT_DETAIL_1_PAGE);
    aemContext.load().json("/components/events/the-groves.json", EVENT_DETAIL_2_PAGE);
    aemContext.load().json("/components/events/old-town-market-road.json", EVENT_DETAIL_3_PAGE);
    aemContext.load().json("/components/events/elephant-rock-visit.json", EVENT_DETAIL_4_PAGE);

    aemContext.addModelsForClasses(
      HandPickedEventsModel.class,
      HandPickedEventModel.class,
      EventDetail.class,
      CategoryTag.class,
      Link.class,
      RegionCity.class,
      SliderDataLayer.class);

    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
    when(saudiTourismConfigs.getScene7Domain()).thenReturn("https://scth.scene7.com/");
  }

  @Test
  void testHandPickedEventsModel(final AemContext aemContext) {
    //Arrange
    aemContext.currentResource(CURRENT_RESOURCE);

    //Act
    final HandPickedEventsModel model = aemContext.request().adaptTo(HandPickedEventsModel.class);
    final String json = model.getJson();
    final HandPickedEventsModel data = gson.fromJson(json, HandPickedEventsModel.class);

    //Assert
    assertEquals("calendar", data.getCardType());
    assertEquals("hand-picked-events", data.getCardStyle());
    assertEquals("Hand Picked Events", data.getHeadline());
    assertEquals("/en/calendar?season=all-seasons&startDate=2022-09-15&endDate=2022-09-17&type=this-week", data.getLink().getUrl());
    assertEquals("View All", data.getLink().getCopy());

    assertEquals(4, data.getEventsIds().size());
    assertEquals("events/via-ferrata-and-hammock", data.getEventsIds().get(0));
    assertEquals("events/the-groves", data.getEventsIds().get(1));
    assertEquals("events/old-town-market-road", data.getEventsIds().get(2));
    assertEquals("events/elephant-rock-visit", data.getEventsIds().get(3));
  }
}