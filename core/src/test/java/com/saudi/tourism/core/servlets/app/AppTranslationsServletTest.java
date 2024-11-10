package com.saudi.tourism.core.servlets.app;


import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class AppTranslationsServletTest {

  private AppTranslationsServlet appTranslationsServlet;
  private UserService userService;

  @BeforeEach
  public void setup(AemContext context) {
    appTranslationsServlet = new AppTranslationsServlet();
    context.load().json("/servlets/app/apptranslations.json", "/apps/sauditourism-app/i18n/en");
    context.currentResource("/apps/sauditourism-app/i18n/en");
    userService = mock(UserService.class);
    MockSlingHttpServletRequest request = context.request();
    ResourceResolver mockResourceResolver = mock(ResourceResolver.class);
    when(userService.getResourceResolver()).thenReturn(mockResourceResolver);
    Utils.setInternalState(appTranslationsServlet, "userService", userService); }

  @Test
  public void tesNoLocale(AemContext context) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);

    appTranslationsServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }
}
