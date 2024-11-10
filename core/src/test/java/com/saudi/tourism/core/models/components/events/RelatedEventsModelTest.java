package com.saudi.tourism.core.models.components.events;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RelatedEventsModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/events/elephant-rock-visit";

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();



  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18n =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        switch (key) {
          case I18nConstants.I18_KEY_RELATED_EVENTS:
            return "Related Events";
          case I18nConstants.I18_KEY_VIEW_ALL:
            return "View all";
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
    aemContext.load().json("/components/events/elephant-rock-visit.json", PAGE_PATH);

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    TagManager tagManager = aemContext.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:events/themed-attractions", "themed-attractions", "themed-attractions");

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");
    when(saudiTourismConfigs.getEventsFilterPagePath()).thenReturn("/calendar/calendar-events");
  }

  @Test
  void testRelatedEventsModel(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(PAGE_PATH);

    // Act
    final RelatedEventsModel model = aemContext.request().adaptTo(RelatedEventsModel.class);
    final String json = model.getJson();
    final RelatedEventsModel data = gson.fromJson(json, RelatedEventsModel.class);

    // Assert
    assertEquals("Related Events", data.getHeadline());
    assertEquals("calendar", data.getCardType());
    assertEquals("related-events", data.getCardStyle());
    assertEquals("/bin/api/v1/events", data.getApiUrl());
    assertEquals("View all", data.getLink().getCopy());
    assertEquals("/en/calendar/calendar-events", data.getLink().getUrl());
    assertFalse(data.getLink().isTargetInNewWindow());
    assertEquals(List.of("themed-attractions"), data.getCategories());

  }
}