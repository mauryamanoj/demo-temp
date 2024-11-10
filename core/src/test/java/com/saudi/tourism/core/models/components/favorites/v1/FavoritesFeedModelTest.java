package com.saudi.tourism.core.models.components.favorites.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.FavoritesService;
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

import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FavoritesFeedModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/your-favourites";
  private static final String RESOURCE_PATH =
      "/content/sauditourism/en/your-favourites/jcr:content/root/responsivegrid/c15_favorites_feed";

  private final Gson gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create();

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private FavoritesService favoritesService;

  private ResourceBundle i18nBundle =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(final String key) {
        switch (key) {
          case LOAD_MORE_TEXT:
            return "Load More";
          case SAVE_TO_FAV_TEXT:
            return "save to favorites";
          case I18_KEY_SAVED_TO_FAV:
            return "saved to favorites";
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
  public void setUp(final AemContext context) {
    context.load().json("/components/c15-favorites-feed/v1/content.json", PAGE_PATH);

    context.addModelsForClasses(FavoritesFeedModel.class);

    context.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(FavoritesService.class, favoritesService);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);

    final FavoritesApiEndpoints favoritesApiEndpoints = FavoritesApiEndpoints.builder()
      .updateFavUrl("/bin/api/v2/user/update.favorites")
      .deleteFavUrl("/bin/api/v2/user/delete.favorites")
      .getFavUrl("/bin/api/v2/user/get.favorites?locale=en")
      .build();

    when(favoritesService.computeFavoritesApiEndpoints(any(String.class)))
      .thenReturn(favoritesApiEndpoints);
  }

  @Test
  public void testFavoritesFeedModelSlingModel(final AemContext context) {
    // Arrange
    context.currentPage(PAGE_PATH);
    context.currentResource(RESOURCE_PATH);

    // Act
    final FavoritesFeedModel model = context.request().adaptTo(FavoritesFeedModel.class);
    final String json = model.getJson();
    final FavoritesFeedModel data = gson.fromJson(json, FavoritesFeedModel.class);

    // Assert
    assertEquals(
        "You don’t have any favorites saved yet. Please add some favorites to save them here.",
        data.getNoResultCopy());
    assertEquals("No saved favorites yet", data.getNoResultTitle());
    assertEquals("Load More", data.getLoadMore());
    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
    assertEquals("save to favorites", data.getSaveToFavoritesText());
    assertEquals("saved to favorites", data.getSavedToFavoritesText());
  }
}
