package com.saudi.tourism.core.models.components.hero.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
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

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_BEST_FOR;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CommonHeroModelTest {

  private static final String HOMEPAGE_PATH = "/content/sauditourism/en";

  private static final String SECONDARY_HERO_CONTENT_PATH =
      "/content/sauditourism/en/jcr:content/root/responsivegrid/h02_secondary_hero";

  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfig;

  @Mock
  private FavoritesService favoritesService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(final String key) {
          switch (key) {
            case SAVE_TO_FAV_TEXT:
              return "Save to my favorites";
            case I18_KEY_SAVED_TO_FAV:
              return "Saved to my favorites";
            case I18_KEY_BEST_FOR:
              return "Best for";
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
  public void setUp(AemContext context) {
    context.load().json("/components/h-02-secondary-hero/en-home-page.json", HOMEPAGE_PATH);

    context.registerService(FavoritesService.class, favoritesService);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
    context.registerService(
        ResourceBundleProvider.class,
        i18nProvider,
        ComponentConstants.COMPONENT_NAME,
        Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);

    final FavoritesApiEndpoints favoritesApiEndpoints =
        FavoritesApiEndpoints.builder()
            .updateFavUrl("/bin/api/v2/user/update.favorites")
            .deleteFavUrl("/bin/api/v2/user/delete.favorites")
            .getFavUrl("/bin/api/v2/user/get.favorites?locale=en")
            .build();

    when(favoritesService.computeFavoritesApiEndpoints(any(String.class)))
        .thenReturn(favoritesApiEndpoints);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test
  public void testCommonHeroModelInSecondaryHeroConfig(final AemContext context) {
    // Arrange
    context.currentPage(HOMEPAGE_PATH);
    context.currentResource(SECONDARY_HERO_CONTENT_PATH);

    // Act
    final CommonHeroModel model = context.request().adaptTo(CommonHeroModel.class);
    final String json = model.getJson();
    final CommonHeroModel data = gson.fromJson(json, CommonHeroModel.class);

    // Assert
    assertEquals("Get Your Tourism eVisa Now", data.getTitle());
    assertEquals("h1", data.getTitleWeight());
    assertEquals(
        "/content/dam/travel-regulations/tow-girls-in-souq-en.jpg",
        data.getImage().getFileReference());
    assertEquals(
        "/content/dam/travel-regulations/tow-girls-in-souq-en.jpg",
        data.getImage().getMobileImageReference());
    assertEquals("AlUla Season", data.getImage().getAlt());
    assertEquals(
        "/content/dam/travel-regulations/tow-girls-in-souq-en.jpg",
        data.getImage().getS7fileReference());
    assertEquals(
        "/content/dam/travel-regulations/tow-girls-in-souq-en.jpg",
        data.getImage().getS7mobileImageReference());
    assertEquals(
        "/content/sauditourism/en/travel-regulations.html", data.getLink().getUrlWithExtension());
    assertEquals("/travel-regulations", data.getLink().getUrlSlingExporter());
    assertEquals("Learn More", data.getLink().getCopy());
    assertFalse(data.getHasFixedButton());
    assertFalse(data.getLink().isTargetInNewWindow());
    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
    assertEquals("Save to my favorites", data.getSaveToFavoritesText());
    assertEquals("Saved to my favorites", data.getSavedToFavoritesText());
    assertEquals("Best for", data.getBestForText());
  }


}
