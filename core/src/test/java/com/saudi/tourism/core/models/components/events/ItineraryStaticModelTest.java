package com.saudi.tourism.core.models.components.events;

import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.impl.RegionCityServiceImpl;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class ItineraryStaticModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";
  private static final String EVENT_PATH = "/content/sauditourism/en/event";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/c99-itinerary-static/content.json", CONTENT_PATH);
    context.load().json("/components/events/content.json", EVENT_PATH);

    final ResourceBundle i18n = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return key;
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };
    final ResourceBundleProvider i18nProvider = mock(ResourceBundleProvider.class);
    doReturn(i18n).when(i18nProvider).getResourceBundle(any(Locale.class));
    context.registerService(ResourceBundleProvider.class, i18nProvider,
        ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);

    final RegionCityService mockCitiesService = mock(RegionCityServiceImpl.class);
    context.registerService(mockCitiesService);
    final RegionCity testCity = new RegionCity("test", "test");
    doReturn(testCity).when(mockCitiesService).getRegionCityById(anyString(), anyString());
  }

  @Test public void testEventStatic(AemContext context) {
    String RESOURCE_PATH = CONTENT_PATH + "/jcr:content/root/responsivegrid/c99-itinerary-static";

    ItineraryStaticModel model =
        context.currentResource(RESOURCE_PATH).adaptTo(ItineraryStaticModel.class);

    assertEquals("Book this experience", model.getCtaText());
    assertEquals("/content/sauditourism/en/event", model.getEventDetails().get(0).getPath());
    assertEquals("Omar Khairat", model.getEventDetails().get(0).getTitle());

  }

}
