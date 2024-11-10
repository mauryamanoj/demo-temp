package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.impl.AdminSettingsServiceImpl;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) class AlertSlingModelTest {

  private static final String EVENT_PATH = "/content/sauditourism/en/event";

  @BeforeEach public void setUp(AemContext context) {
    final AdminSettingsService adminSettingsService = new AdminSettingsServiceImpl();
    AdminUtil.setAdminSettingsService(adminSettingsService);

    final Cache memCache = mock(Cache.class);
    Utils.setInternalState(adminSettingsService, "memCache", memCache);

    final UserService userService = mock(UserService.class);
    when(userService.getResourceResolver()).thenReturn(context.resourceResolver());
    Utils.setInternalState(adminSettingsService, "resolverProvider", userService);

    context.load().json("/components/events/content.json", EVENT_PATH);
  }

  @Test void testSimpleSliderModel(AemContext context) {
    context.currentResource(EVENT_PATH);
    AlertSlingModel alertSlingModel = context.request().adaptTo(AlertSlingModel.class);
    Assertions.assertNotNull(alertSlingModel);
    assertNotNull(alertSlingModel.getAdminPageAlert());
    assertFalse(alertSlingModel.isEnableAlertToast());
  }
}
