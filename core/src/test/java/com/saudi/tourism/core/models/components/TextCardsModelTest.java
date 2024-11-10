package com.saudi.tourism.core.models.components;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class TextCardsModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/do/winter/snow-adventures";
  private static final String CONTENT_PATH =
      "/content/sauditourism/en/do/winter/snow-adventures/wintry-rides/jcr:content/root/responsivegrid/c27_teaser_with_card.infinity";

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private EventService eventService;

  @Mock
  private RegionCityService regionCityService;

  @Mock
  private FavoritesService favoritesService;

  @Mock
  private WeatherService weatherService;

  @Mock
  private ActivityService activityService;

  @Mock
  private PackageService packageService;

  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(final String key) {
          switch (key) {
            default:
              return "FAKE_TEXT";
          }
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/components/c27-teaser-with-cards/page.json", PAGE_PATH);
    aemContext.load().json("/components/c27-teaser-with-cards/content.json", CONTENT_PATH);

    aemContext.registerService(
      ResourceBundleProvider.class,
      i18nProvider,
      ComponentConstants.COMPONENT_NAME,
      Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(EventService.class, eventService);
    aemContext.registerService(RegionCityService.class, regionCityService);
    aemContext.registerService(FavoritesService.class, favoritesService);
    aemContext.registerService(WeatherService.class, weatherService);
    aemContext.registerService(ActivityService.class, activityService);
    aemContext.registerService(PackageService.class, packageService);


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
  void testTextCardsModel(final AemContext aemContext) {
    // Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(CONTENT_PATH);

    // Act
    final TextCardsModel model =
        aemContext.currentResource().adaptTo(TextCardsModel.class);
    final String json = model.getJson();
    final TextCardsModel data = gson.fromJson(json, TextCardsModel.class);

    // Assert
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());

    assertEquals("/do/winter/snow-adventures/snowy-hikes", data.getCards().get(0).getFavoritePath());
    assertEquals("/do/winter/snow-adventures/snow-sledding", data.getCards().get(1).getFavoritePath());
    assertEquals("/do/winter/snow-adventures/kashta-in-the-snow", data.getCards().get(2).getFavoritePath());

    assertEquals("/en/see-do/destinations/jeddah/jeddah_top_attractions", data.getHeader().getLink().getUrl());
  }
}
