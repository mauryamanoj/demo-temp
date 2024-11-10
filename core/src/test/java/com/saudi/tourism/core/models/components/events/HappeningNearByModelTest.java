package com.saudi.tourism.core.models.components.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HappeningNearByModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/events/elephant-rock-visit";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18n =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        switch (key) {
          case I18nConstants.I18_KEY_HAPPENING_NEARBY:
            return "Happening Nearby";
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
    aemContext.load().json("/components/events/elephant-rock-visit.json", PAGE_PATH);

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");
  }

  @Test
  void shouldHappeningNearByModel(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(PAGE_PATH);

    // Act
    final HappeningNearByModel model = aemContext.request().adaptTo(HappeningNearByModel.class);
    final String json = model.getJson();
    final HappeningNearByModel data = gson.fromJson(json, HappeningNearByModel.class);

    // Assert
    assertEquals("Happening Nearby", data.getHeadline());
    assertEquals("calendar", data.getCardType());
    assertEquals("happening-nearby", data.getCardStyle());
    assertEquals("/bin/api/v1/events", data.getApiUrl());
    assertEquals("alula", data.getCity());
  }
}