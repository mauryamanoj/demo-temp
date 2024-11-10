package com.saudi.tourism.core.servlets.app;

import com.day.cq.commons.Externalizer;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import pl.pojo.tester.internal.utils.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class LocationServletTest {

  private LocationServlet servlet;
  private UserService userService;
  private ResourceBundleProvider i18nProvider;
  private ResourceBundle i18nBundle;

  @BeforeEach
  public void setup(AemContext context) {
    userService = mock(UserService.class);
    context.registerService(UserService.class, userService);
    doReturn(context.resourceResolver()).when(userService).getResourceResolver();

    final SlingSettingsService settingsService = Mockito.mock(SlingSettingsService.class);
    context.registerService(SlingSettingsService.class, settingsService);
    doReturn(CollectionUtils.asSet(Externalizer.PUBLISH)).when(settingsService).getRunModes();

    servlet = new LocationServlet();

    i18nProvider = mock(ResourceBundleProvider.class);
    i18nBundle = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any());
    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");

    // Cities service (to get cities by id).
    context.registerService(RegionCityService.class, mock(RegionCityService.class));
  }

  @Test
  public void testValidPath(AemContext context) throws  IOException {
    context.load()
        .json("/servlets/app/location-page.json", "/content/sauditourism/app/en/location-page");
    context.currentResource("/content/sauditourism/app/en/location-page");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/location-page");

    servlet.doGet(request, context.response());

    final String responseOutput = context.response().getOutputAsString();
    Assertions.assertNotNull(responseOutput);
    assertFalse(StringUtils.contains(responseOutput, "No location information"));
  }

  @Test
  public void tesInvalidPath(AemContext context) throws IOException {
    context.load().json("/servlets/app/location-page.json", "/temp");
    context.currentResource("/temp");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/location-page");

    servlet.doGet(request, context.response());
    final String responseOutput = context.response().getOutputAsString();
    Assertions.assertNotNull(responseOutput);
    assertTrue(StringUtils.contains(responseOutput, "No location information"));
  }
}
