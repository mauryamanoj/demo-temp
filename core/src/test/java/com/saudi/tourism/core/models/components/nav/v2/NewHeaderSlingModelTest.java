package com.saudi.tourism.core.models.components.nav.v2;

import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.NavigationService;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.services.impl.AdminSettingsServiceImpl;
import com.saudi.tourism.core.services.impl.NavigationServiceImpl;
import com.saudi.tourism.core.services.impl.SaudiModeConfigImpl;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @noinspection FieldCanBeLocal
 */
@ExtendWith(AemContextExtension.class)
@Getter
public class NewHeaderSlingModelTest {

  private static final String LANG = "en";

  /**
   * /content/sauditourism/
   */
  private static final String CONTENT_PATH = Constants.ROOT_CONTENT_PATH;

  /**
   * /content/sauditourism/{lang}
   */
  private static final String PAGE_PATH = CONTENT_PATH + SpecialCharConstants.FORWARD_SLASH + LANG;

  /**
   * /content/sauditourism/{lang}/Configs/header
   */
  private static final String HEADER_PATH = PAGE_PATH + "/Configs/header";
  public static final String SITE_NAME = "sauditourism";

  private NavigationService navigationService;
  private SaudiModeConfig saudiModeConfig;
  private NavigationHeader header;
  private MockSlingHttpServletRequest request;
  private ResourceResolver resourceResolver;
  private AdminPageOption adminPageOption;
  private AdminSettingsServiceImpl adminSettingsService;

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/header-config-model/sauditourism.2.json", CONTENT_PATH);


    // Set current page
    context.currentPage(PAGE_PATH);

    request = context.request();
    resourceResolver = context.resourceResolver();

    header = Mockito.mock(NavigationHeader.class);
    navigationService = Mockito.mock(NavigationServiceImpl.class);
    adminPageOption = Mockito.mock(AdminPageOption.class);
    adminSettingsService = Mockito.mock(AdminSettingsServiceImpl.class);
    saudiModeConfig = Mockito.mock(SaudiModeConfigImpl.class);

    doReturn(header)
        .when(navigationService)
        .getNavigationHeader(request, resourceResolver, LANG, HEADER_PATH, SITE_NAME);
    when(adminPageOption.getHeaderPath()).thenReturn(HEADER_PATH);
    when(adminSettingsService.getAdminOptions(anyString(), anyString())).thenReturn(adminPageOption);
    AdminUtil.setAdminSettingsService(adminSettingsService);

    context.registerService(navigationService);
    context.registerService(saudiModeConfig);
  }

  @Test public void testNewHeaderSlingModel(AemContext context) {
    NewHeaderSlingModel newHeaderSlingModel = request.adaptTo(NewHeaderSlingModel.class);

    Assertions.assertEquals(LANG, newHeaderSlingModel.getLanguage());
    Assertions.assertEquals(header, newHeaderSlingModel.getNavigationHeader());
  }

}
