package com.saudi.tourism.core.models.components.events.eventscards.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
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

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventsCardsModelTest {

  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(final String key) {
        switch (key) {
          case LOAD_MORE_TEXT:
            return "Load More";
          default:
            return null;
        }
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };


  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(EventsCardsModel.class, Link.class, EventsCardsFilterModel.class, EventsCardsHandPickModel.class);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);

    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test
  public void testEventsCardsModel(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/events-cards/events-cards.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/events_cards");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/events_cards")
        .adaptTo(EventsCardsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, EventsCardsModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("This month", data.getTitle());

    assertNotNull(data.getLink());
    assertEquals("View all", data.getLink().getText());
    assertEquals("/content/sauditourism/en/winter-season", data.getLink().getUrl());

    assertNotNull(data.getFilter());
    assertEquals("this-month", data.getFilter().getPeriod());
    assertEquals("/content/dam/sauditourism/cf/en/destinations/makkah", data.getFilter().getDestination());
    assertEquals("/content/dam/sauditourism/cf/en/seasons/riyadh-season", data.getFilter().getSeason());

    assertNotNull(data.getSort());
    assertEquals("startDate", data.getSort().getSortBy());

    assertNotNull(data.getDisplay());
    assertEquals("small", data.getDisplay().getCardSize());
    assertEquals("scrollable", data.getDisplay().getDisplayType());

    assertNotNull(data.getLoadMore());
    assertEquals("Load More", data.getLoadMore().getLoadMoreLabel());

    assertEquals("/bin/api/v1/events", data.getApiUrl());
  }

  @Test
  public void testHandPickedEventsCardsModel(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/events-cards/handpicked-events-cards.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/events_cards");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/events_cards")
        .adaptTo(EventsCardsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, EventsCardsModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("This month", data.getTitle());

    assertNotNull(data.getLink());
    assertEquals("View all", data.getLink().getText());
    assertEquals("/content/sauditourism/en/winter-season", data.getLink().getUrl());

    assertNull(data.getFilter());

    assertNull(data.getSort());

    assertNotNull(data.getDisplay());
    assertEquals("small", data.getDisplay().getCardSize());
    assertEquals("scrollable", data.getDisplay().getDisplayType());

    assertNotNull(data.getLoadMore());
    assertEquals("Load More", data.getLoadMore().getLoadMoreLabel());

    assertEquals("/bin/api/v1/events", data.getApiUrl());

    assertNotNull(data.getHandpick());
    assertNotNull(data.getHandpick().getEventCFPaths());
    assertEquals("/content/dam/sauditourism/cf/en/events/sheikh-rouhani", data.getHandpick().getEventCFPaths().get(0));
    assertEquals("/content/dam/sauditourism/cf/en/events/novemberevent", data.getHandpick().getEventCFPaths().get(1));
  }


}