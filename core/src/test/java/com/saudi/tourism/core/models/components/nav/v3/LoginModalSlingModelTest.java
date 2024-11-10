package com.saudi.tourism.core.models.components.nav.v3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.impl.AdminSettingsServiceImpl;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.models.components.nav.v3.LoginModalSlingModel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LoginModalSlingModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/summer-vibes/beaches";

  private final Gson gson = new GsonBuilder().create();

  @Mock
  private AdminSettingsServiceImpl adminSettingsService;

  @Mock
  private AdminPageOption adminPageOption;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(final String key) {
          switch (key) {
            case SIGN_IN_I18N_KEY:
              return SIGN_IN_I18N_KEY;
            case SIGN_OUT_I18N_KEY:
              return SIGN_OUT_I18N_KEY;
            case LOG_IN_I18N_KEY:
              return LOG_IN_I18N_KEY;
            case PERSONAL_INFORMATION_I18N_KEY:
              return PERSONAL_INFORMATION_I18N_KEY;
            case MY_FAVORITES_I18N_KEY:
              return MY_FAVORITES_I18N_KEY;
            case MY_TRIPS_I18N_KEY:
              return MY_TRIPS_I18N_KEY;
            default:
              return null;
          }
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    AdminUtil.setAdminSettingsService(adminSettingsService);

    aemContext.load().json("/components/nav/v3/login-user.json", PAGE_PATH);

    aemContext.addModelsForClasses(LoginModalSlingModel.class);

    aemContext.request().setAttribute("domain", "https://ssid.visitsaudi.com");
    aemContext.request().setAttribute("clientId", "ef3e43bf-44ec-418c-ba19-3c12208b1b28");
    aemContext.request().setAttribute("accountPath", "/saudi/en/account");
    aemContext.request().setAttribute("myFavorites", "/saudi/en/account/favorites");
    aemContext.request().setAttribute("myTrips", "/saudi/en/account/trips");

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);

  }

  @Test
  void testLoginModalSlingModelWithoutSerialization(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(PAGE_PATH);

    // Act
    final LoginModalSlingModel model = aemContext.request().adaptTo(LoginModalSlingModel.class);

    // Assert
    assertEquals("https://ssid.visitsaudi.com", model.getDomain());
    assertEquals("ef3e43bf-44ec-418c-ba19-3c12208b1b28", model.getClientId());
    assertEquals("media/account-page/avatar-36x36.png", model.getAvatar().getDesktop());
    assertEquals("media/account-page/avatar-60x60.png", model.getAvatar().getMobile());
    assertEquals(LOG_IN_I18N_KEY, model.getCtaLoginText());
    assertEquals("/bin/api/v2/", model.getFavoriteApiPath());
    assertEquals("Oops, something went wrong.", model.getApiError().getMessage());
    assertEquals("Try again", model.getApiError().getCtaText());
    assertEquals("/saudi/en/account", model.getAccountPath());
    assertEquals(SIGN_IN_I18N_KEY, model.getSignInCopy());
    assertEquals(SIGN_OUT_I18N_KEY, model.getSignOutCopy());
    assertEquals("/bin/api/v2/user/", model.getUserApi().getPath());
    assertEquals("get.profile", model.getUserApi().getGetProfile());
    assertEquals("get.trips", model.getUserApi().getGetTripPlansEndpoint());
    assertEquals("update.profile", model.getUserApi().getUpdateProfile());
    assertEquals("https://ssid.visitsaudi.com", model.getUserApi().getDomain());
    assertEquals("ef3e43bf-44ec-418c-ba19-3c12208b1b28", model.getUserApi().getClientId());
    assertEquals("/bin/api/v1/ssid/login-url", model.getUserApi().getSsidLoginUrl());
    assertEquals("/bin/api/v1/ssid/logout", model.getUserApi().getSsidLogoutUrl());
    assertEquals("file", model.getSubmenu().get(0).getCta().getIcon());
    assertEquals(PERSONAL_INFORMATION_I18N_KEY, model.getSubmenu().get(0).getCta().getCopy());
    assertEquals("/saudi/en/account", model.getSubmenu().get(0).getCta().getUrl());
    assertEquals("heart-inactive", model.getSubmenu().get(1).getCta().getIcon());
    assertEquals(MY_FAVORITES_I18N_KEY, model.getSubmenu().get(1).getCta().getCopy());
    assertEquals("/saudi/en/account/favorites", model.getSubmenu().get(1).getCta().getUrl());
    assertEquals("trip", model.getSubmenu().get(2).getCta().getIcon());
    assertEquals(MY_TRIPS_I18N_KEY, model.getSubmenu().get(2).getCta().getCopy());
    assertEquals("/saudi/en/account/trips", model.getSubmenu().get(2).getCta().getUrl());
  }


  @Test
  void testLoginModalSlingModelWithSerialization(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(PAGE_PATH);

    // Act
    final LoginModalSlingModel model = aemContext.request().adaptTo(LoginModalSlingModel.class);
    final String json = model.getJson();
    final LoginModalSlingModel data = gson.fromJson(json, LoginModalSlingModel.class);

    // Assert
    assertEquals("https://ssid.visitsaudi.com", data.getDomain());
    assertEquals("ef3e43bf-44ec-418c-ba19-3c12208b1b28", data.getClientId());
    assertEquals("media/account-page/avatar-36x36.png", data.getAvatar().getDesktop());
    assertEquals("media/account-page/avatar-60x60.png", data.getAvatar().getMobile());
    assertEquals(LOG_IN_I18N_KEY, data.getCtaLoginText());
    assertEquals("/bin/api/v2/", data.getFavoriteApiPath());
    assertEquals("Oops, something went wrong.", data.getApiError().getMessage());
    assertEquals("Try again", data.getApiError().getCtaText());
    assertEquals("/saudi/en/account", data.getAccountPath());
    assertEquals(SIGN_IN_I18N_KEY, data.getSignInCopy());
    assertEquals(SIGN_OUT_I18N_KEY, data.getSignOutCopy());
    assertEquals("/bin/api/v2/user/", data.getUserApi().getPath());
    assertEquals("get.profile", data.getUserApi().getGetProfile());
    assertEquals("get.trips", data.getUserApi().getGetTripPlansEndpoint());
    assertEquals("update.profile", data.getUserApi().getUpdateProfile());
    assertEquals("https://ssid.visitsaudi.com", data.getUserApi().getDomain());
    assertEquals("ef3e43bf-44ec-418c-ba19-3c12208b1b28", data.getUserApi().getClientId());
    assertEquals("/bin/api/v1/ssid/login-url", data.getUserApi().getSsidLoginUrl());
    assertEquals("/bin/api/v1/ssid/logout", data.getUserApi().getSsidLogoutUrl());
    assertEquals("file", data.getSubmenu().get(0).getCta().getIcon());
    assertEquals(PERSONAL_INFORMATION_I18N_KEY, data.getSubmenu().get(0).getCta().getCopy());
    assertEquals("/saudi/en/account", data.getSubmenu().get(0).getCta().getUrl());
    assertEquals("heart-inactive", data.getSubmenu().get(1).getCta().getIcon());
    assertEquals(MY_FAVORITES_I18N_KEY, data.getSubmenu().get(1).getCta().getCopy());
    assertEquals("/saudi/en/account/favorites", data.getSubmenu().get(1).getCta().getUrl());
    assertEquals("trip", data.getSubmenu().get(2).getCta().getIcon());
    assertEquals(MY_TRIPS_I18N_KEY, data.getSubmenu().get(2).getCta().getCopy());
    assertEquals("/saudi/en/account/trips", data.getSubmenu().get(2).getCta().getUrl());
  }
}
