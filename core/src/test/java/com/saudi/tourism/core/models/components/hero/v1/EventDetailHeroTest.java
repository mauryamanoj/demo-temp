package com.saudi.tourism.core.models.components.hero.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_BEST_FOR;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SHARE_EVENT;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventDetailHeroTest {
  private static final String SCENE_7_DOMAIN = "https://scene7.adobe.com/";
  private static final String EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH = "/content/sauditourism/en/events/the-italian-circus";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RegionCityService citiesService;

  @Mock
  private FavoritesService favoritesService;

  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(final String key) {
          switch (key) {
            case "all-seasons":
              return "All seasons";
            default:
              return "dummy_i18n_traduction";
          }
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext
        .load()
        .json("/components/events/the-italian-circus.json", EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH);

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(RegionCityService.class, citiesService);
    aemContext.registerService(FavoritesService.class, favoritesService);

    final FavoritesApiEndpoints favoritesApiEndpoints =
        FavoritesApiEndpoints.builder()
            .updateFavUrl("/bin/api/v2/user/update.favorites")
            .deleteFavUrl("/bin/api/v2/user/delete.favorites")
            .getFavUrl("/bin/api/v2/user/get.favorites?locale=en")
            .build();

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
    when(saudiTourismConfigs.getScene7Domain()).thenReturn(SCENE_7_DOMAIN);
    when(favoritesService.computeFavoritesApiEndpoints(any(String.class)))
        .thenReturn(favoritesApiEndpoints);
  }

  @Test
  void testEventDetailHero(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(EVENT_THE_ITALIAN_CIRCUS_PAGE_PATH);

    // Act
    final EventDetailHero model = aemContext.request().adaptTo(EventDetailHero.class);
    final String json = model.getHeroModel().getJson();
    final CommonHeroModel data = gson.fromJson(json, CommonHeroModel.class);

    // Assert
    assertEquals(
        "/content/dam/saudi-tourism/media/events-/shutterstock_518827906.jpg",
        data.getImage().getS7fileReference());
    assertEquals(
        "/content/dam/saudi-tourism/media/events-/shutterstock_518827906.jpg",
        data.getImage().getFileReference());
    assertEquals(
        "/content/dam/saudi-tourism/media/events-/shutterstock_518827906.jpg",
        data.getImage().getS7mobileImageReference());
    assertEquals(
        "/content/dam/saudi-tourism/media/events-/shutterstock_518827906.jpg",
        data.getImage().getMobileImageReference());
    assertEquals("The Italian Circus", data.getTitle());
    assertEquals("All seasons", data.getSeason());
  }
}
