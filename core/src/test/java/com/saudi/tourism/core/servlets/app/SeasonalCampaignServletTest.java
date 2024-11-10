package com.saudi.tourism.core.servlets.app;

import com.day.cq.commons.Externalizer;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import pl.pojo.tester.internal.utils.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class SeasonalCampaignServletTest {

  private SeasonalCampaignServlet servlet;
  private UserService userService;
  private ResourceBundleProvider i18nProvider;
  private ResourceBundle i18nBundle;
  private ResourceResolver resourceResolver;

  @BeforeEach public void setup(AemContext context) {
    userService = mock(UserService.class);
    resourceResolver = mock(ResourceResolver.class);
    context.registerService(UserService.class, userService);
    doReturn(resourceResolver).when(userService).getResourceResolver();

    final SlingSettingsService settingsService = Mockito.mock(SlingSettingsService.class);
    context.registerService(SlingSettingsService.class, settingsService);
    doReturn(CollectionUtils.asSet(Externalizer.PUBLISH)).when(settingsService).getRunModes();

    servlet = new SeasonalCampaignServlet();

    i18nProvider = mock(ResourceBundleProvider.class);
    i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any());
    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");

    context.registerService(RegionCityService.class, mock(RegionCityService.class));
  }

  @Test public void testValidPath(final AemContext context) throws IOException {
    context.load()
        .json("/servlets/app/location-campaign-page.json", "/content/sauditourism/app1/en");
    context.currentResource("/content/sauditourism/app1/en");
    MockSlingHttpServletRequest request = context.request();
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    request.setParameterMap(parameters);

    servlet.doGet(request, context.response());

    final String responseOutput = context.response().getOutputAsString();
    Assertions.assertNotNull(responseOutput);
    assertTrue(StringUtils.contains(responseOutput, "seasonal campaign page was not found"));
  }

  @Test public void tesInvalidPath(final AemContext context) throws IOException {
    context.load()
        .json("/servlets/app/location-campaign-page.json", "/content/sauditourism/app1/en");
    context.currentResource("/content/sauditourism/app1/en");
    MockSlingHttpServletRequest request = context.request();

    servlet.doGet(request, context.response());
    final String responseOutput = context.response().getOutputAsString();
    Assertions.assertNotNull(responseOutput);
    assertTrue(StringUtils.contains(responseOutput, "Undefined locale"));
  }
}
