package com.saudi.tourism.core.models.components.contentfragmentmenu;

import com.day.cq.wcm.api.WCMException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.models.components.contentfragment.menu.EvisaCFModel;
import com.saudi.tourism.core.models.components.contentfragment.menu.MenuCFModel;
import com.saudi.tourism.core.models.components.contentfragment.menu.MenuItemCFModel;
import com.saudi.tourism.core.models.components.contentfragment.menu.SearchBoxCFModel;
import com.saudi.tourism.core.models.components.contentfragment.menu.SubMenuCF;
import com.saudi.tourism.core.models.components.login.UserApiModel;
import com.saudi.tourism.core.models.components.menu.MenuModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContentFragmentMenuModelTest {
  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private SaudiSSIDConfig saudiSSIDConfig;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RunModeService runModeService;

  @BeforeEach
  void setUp(final AemContext context) throws WCMException {
    context.registerService(SlingSettingsService.class, settingsService);
    context.registerService(SaudiSSIDConfig.class, saudiSSIDConfig);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(RunModeService.class, runModeService);


    context.addModelsForClasses(
        MenuModel.class,
        MenuCFModel.class,
        MenuItemCFModel.class,
        SubMenuCF.class,
        EvisaCFModel.class,
        SearchBoxCFModel.class,
        UserApiModel.class);
    context.load().json("/components/contentfragmentmenu/menu.json", "/content/sauditourism/en/menu");
    context.load().json("/components/contentfragmentmenu/menuCF.json", "/content/dam/sauditourism/cf/en/menu");
    context.load().json("/components/contentfragmentmenu/sunMenu1.json", "/content/dam/sauditourism/cf/en/menu/subMenu1");
    context.load().json("/components/contentfragmentmenu/sunMenu2.json", "/content/dam/sauditourism/cf/en/menu/subMenu2");
    context.load().json("/components/contentfragmentmenu/destination.json", "/content/dam/sauditourism/cf/en/menu/destination");
    context.load().json("/components/contentfragmentmenu/page.json", "/content/sauditourism/en/page");

    when(saudiTourismConfigs.getSearchApiEndpoint()).thenReturn("/bin/api/v1/search");

    when(saudiSSIDConfig.getLoginRedirectEndpoint()).thenReturn("/bin/api/v1/ssid/login-url");
    when(saudiSSIDConfig.getSSIDDomain()).thenReturn("uat-ssid.visitsaudi.com");
    when(saudiSSIDConfig.getClientId()).thenReturn("f6d8b536-e329-443f-a134-58c3e0a5cd12");
    when(saudiSSIDConfig.getUserInfoEndpoint()).thenReturn("/bin/api/v1/ssid/userInfo");
    when(saudiSSIDConfig.getLogoutRedirectEndpoint()).thenReturn("/bin/api/v1/ssid/logout");
    when(saudiSSIDConfig.getGetProfileEndpoint()).thenReturn("/bin/api/v2/user/get.profile");
    when(saudiSSIDConfig.getGetFavoritesEndpoint()).thenReturn("/bin/api/v2/user/get.favorites");
    when(saudiSSIDConfig.getGetTripsEndpoint()).thenReturn("/bin/api/v2/user/get.trips");
    when(saudiSSIDConfig.getUpdateProfileEndpoint()).thenReturn("/bin/api/v2/user/update.profile");

    when(runModeService.isPublishRunMode()).thenReturn(true);
  }

  @Test
   void shouldReturnMenu(final AemContext aemContext) {

    aemContext.currentResource("/content/sauditourism/en/menu");
    final MenuModel model = aemContext.request().adaptTo(MenuModel.class);
    final String json = model.getJson();
    final var menuCFModel = gson.fromJson(json, MenuCFModel.class);
    //Assert
    assertNotNull(menuCFModel);
    assertNotNull(menuCFModel.getData());
    assertEquals(2, menuCFModel.getData().size());
    assertEquals("Destinations", menuCFModel.getData().get(0).getTitle());
    assertEquals("https://scth.scene7.com/is/image/scth/image:crop-1920x1080?defaultImage=image&fmt=png-alpha", menuCFModel.getData().get(0).getImage().getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/image:crop-1920x1080?defaultImage=image&fmt=png-alpha", menuCFModel.getData().get(0).getImage().getMobileImage());
    assertEquals("https://scth.scene7.com/is/image/scth/image", menuCFModel.getData().get(0).getImage().getS7fileReference());

    assertEquals("/content/sauditourism/en/see-do/destinations", menuCFModel.getData().get(0).getCtaLink());
    assertEquals("/content/sauditourism/en/see-do/destinations", menuCFModel.getData().get(0).getViewAllLink());
    assertNotNull(menuCFModel.getData().get(0).getData());
    assertEquals(2, menuCFModel.getData().get(0).getData().size());
    assertEquals("Nav Title", menuCFModel.getData().get(0).getData().get(0).getCtaLabel());
    assertEquals("/content/sauditourism/en/page", menuCFModel.getData().get(0).getData().get(0).getCtaLink());
    assertNotNull(menuCFModel.getData().get(1).getData());
    assertEquals("Destinations", menuCFModel.getData().get(1).getTitle());
    assertEquals("/content/sauditourism/en/see-do/destinations", menuCFModel.getData().get(1).getCtaLink());
    assertNull(menuCFModel.getData().get(1).getViewAllLink());
    assertEquals(0, menuCFModel.getData().get(1).getData().size());
    assertNotNull(menuCFModel.getLanguages());
    assertEquals(2, menuCFModel.getLanguages().getData().size());
    assertEquals("ar", menuCFModel.getLanguages().getData().get(1).getCtaLabel());
    assertEquals("/ar", menuCFModel.getLanguages().getData().get(1).getCtaLink());
    assertEquals("Un Available Lan Message", menuCFModel.getLanguages().getData().get(0).getUnAvailableLanMessage());
    assertEquals("/content/en", menuCFModel.getLanguages().getData().get(0).getUnAvailableLanLink());
    assertEquals("Un Available Lan Link", menuCFModel.getLanguages().getData().get(0).getUnAvailableLanLinkLabel());

    //Search Box
    assertEquals("/content/sauditourism/en/search", menuCFModel.getSearchBox().getSearchPagePath());
    assertEquals("/bin/api/v1/search", menuCFModel.getSearchBox().getSuggestionsEndpoint());
    assertEquals("search", menuCFModel.getSearchBox().getSearchLabel());
    assertEquals("searchPlaceholder", menuCFModel.getSearchBox().getSearchPlaceholder());
    assertEquals("searchPlaceholder", menuCFModel.getSearchBox().getSearchPlaceholder());

    //User Menu
    assertEquals("register", menuCFModel.getUserMenu().getRegisterLabel());
    assertEquals("signOut", menuCFModel.getUserMenu().getSignOutLabel());
    assertEquals("icon1", menuCFModel.getUserMenu().getSubMenu().get(0).getIconName());
    assertEquals("label1", menuCFModel.getUserMenu().getSubMenu().get(0).getLabel());
    assertEquals("/content/sauditourism/page1", menuCFModel.getUserMenu().getSubMenu().get(0).getUrl());
    assertEquals("icon2", menuCFModel.getUserMenu().getSubMenu().get(1).getIconName());
    assertEquals("label2", menuCFModel.getUserMenu().getSubMenu().get(1).getLabel());
    assertEquals("/content/sauditourism/page2", menuCFModel.getUserMenu().getSubMenu().get(1).getUrl());

    assertNotNull(menuCFModel.getUserMenu().getUserApi());
    assertNotNull(menuCFModel.getUserMenu().getUserApi().getSsidLoginUrl());
    assertEquals("/bin/api/v1/ssid/login-url", menuCFModel.getUserMenu().getUserApi().getSsidLoginUrl());
    assertEquals("uat-ssid.visitsaudi.com", menuCFModel.getUserMenu().getUserApi().getDomain());
    assertEquals("f6d8b536-e329-443f-a134-58c3e0a5cd12", menuCFModel.getUserMenu().getUserApi().getClientId());
    assertEquals("/bin/api/v1/ssid/userInfo", menuCFModel.getUserMenu().getUserApi().getSsidUserInfo());
    assertEquals("/bin/api/v1/ssid/userInfo", menuCFModel.getUserMenu().getUserApi().getSsidUserInfo());
    assertEquals("/bin/api/v1/ssid/logout", menuCFModel.getUserMenu().getUserApi().getSsidLogoutUrl());
    assertEquals("/bin/api/v2/user/get.profile", menuCFModel.getUserMenu().getUserApi().getGetProfile());
    assertEquals("/bin/api/v2/user/get.trips", menuCFModel.getUserMenu().getUserApi().getGetTripPlansEndpoint());
    assertEquals("/bin/api/v2/user/update.profile", menuCFModel.getUserMenu().getUserApi().getUpdateProfile());
    assertEquals("/bin/api/v2/user/get.favorites", menuCFModel.getUserMenu().getUserApi().getGetFavorites());

  }
}