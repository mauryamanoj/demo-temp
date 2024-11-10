package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.CountriesGroupsListConfigModel;
import com.saudi.tourism.core.models.components.AdminPageAlert;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.TestCacheImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AdminSettingsServiceTest {

  @Mock(answer= Answers.CALLS_REAL_METHODS)
  AdminSettingsServiceImpl mockService;

  @Mock(answer= Answers.CALLS_REAL_METHODS)
  TestCacheImpl memCache;

  @Mock
  private UserService userService;

  @Mock
  private EventService eventService;

  @Mock
  private RoadTripScenariosService roadTripService;

  @Mock
  private ExperienceService experienceService;

  private AdminSettingsService adminSettingsService;

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/pages/admin-config.json",
        "/content/sauditourism/en/Configs/admin");

    memCache.init();
    context.registerService(Cache.class, memCache);

    doReturn(context.resourceResolver()).when(userService).getResourceResolver();
    context.registerService(UserService.class, userService);
    context.registerService(EventService.class, eventService);
    context.registerService(RoadTripScenariosService.class, roadTripService);
    context.registerService(ExperienceService.class, experienceService);

    context.registerInjectActivateService(mockService);
    adminSettingsService = context.getService(AdminSettingsService.class);
  }

  @Test
  void getAdminOptions(AemContext context) {
    AdminPageOption adminPageOption = adminSettingsService.getAdminOptions("en", StringUtils.EMPTY);
    assertEquals("/content/sauditourism/en/events", adminPageOption.getEventsPath());
  }

  @Test
  void getAdminAlert(AemContext context) {
    AdminPageAlert adminPageAlert = adminSettingsService.getAdminAlert("en", StringUtils.EMPTY);
    assertEquals("#stayhome", adminPageAlert.getMiddleTextHash());
  }


}
