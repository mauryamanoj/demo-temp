package com.saudi.tourism.core.models.exporter;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.services.impl.SaudiModeConfigImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(AemContextExtension.class)
public class EventFavoritesExporterTest {

  private static final String CONTENT_PATH = "/content/sauditourism/ar/events";
  private SaudiModeConfig saudiModeConfig;
  private ResourceBundleProvider i18nProvider;

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/events/event-detail.json", CONTENT_PATH);
    saudiModeConfig = new SaudiModeConfigImpl();
    context.registerService(saudiModeConfig);

    i18nProvider = Mockito.mock(ResourceBundleProvider.class);
    Mockito.when(i18nProvider.getResourceBundle(Mockito.any())).thenReturn(null);

    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name","org.apache.sling.i18n.impl.JcrResourceBundleProvider");
  }

  @Test
  public void testEventFavoritesExporter(AemContext context) {

    String RESOURCE_PATH = "/enrique-iglesias-/";

    Resource resource = context.currentResource(CONTENT_PATH + RESOURCE_PATH);

    EventFavoritesExporter eventFavoritesExporter =
        resource.getChild(JcrConstants.JCR_CONTENT).adaptTo(EventFavoritesExporter.class);

    Assertions.assertNotNull(eventFavoritesExporter);
  }

}
