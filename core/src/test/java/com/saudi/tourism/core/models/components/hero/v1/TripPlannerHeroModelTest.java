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

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class TripPlannerHeroModelTest {
  private static final String HOMEPAGE_PATH = "/content/sauditourism/en";

  private static final String TRIP_PLANER_PATH =
      "/content/sauditourism/en/plan-your-trip/trip-planner";

  private static final String TRIP_PLANER_HERO_CONTENT_PATH =
      "/content/sauditourism/en/plan-your-trip/trip-planner/jcr:content/root/responsivegrid/h02_secondary_hero";

  private static final String CREATE_ITININERARY_CONFIG_PATH =
    "/content/sauditourism/en/Configs/admin/jcr:content/create-itinerary-config";

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
    context.load().json("/components/h-02-secondary-hero/trip-planer-page.json", TRIP_PLANER_PATH);
    context.load().json("/components/h-02-secondary-hero/create-itininerary-config.json", CREATE_ITININERARY_CONFIG_PATH);

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
  public void testTripPlannerHeroModel(AemContext context) {
    // Arrange
    context.currentPage(TRIP_PLANER_PATH);
    context.currentResource(TRIP_PLANER_HERO_CONTENT_PATH);

    // Act
    final TripPlannerHeroModel model = context.request().adaptTo(TripPlannerHeroModel.class);
    final String json = model.getJson();
    final TripPlannerHeroModel data = gson.fromJson(json, TripPlannerHeroModel.class);

    // Assert
    // SecondaryHeroData
    assertEquals("Trip Planner", data.getSecondaryHeroData().getTitle());
    assertEquals("h1", data.getSecondaryHeroData().getTitleWeight());
    assertEquals(
        "/content/dam/articles/batch-5a/open-air-attractions-in-saudi/full-size-images/gallery_05.jpg",
        data.getSecondaryHeroData().getImage().getFileReference());
    assertEquals(
        "/content/dam/articles/batch-5a/open-air-attractions-in-saudi/full-size-images/gallery_05.jpg",
        data.getSecondaryHeroData().getImage().getMobileImageReference());
    assertEquals("Trip Planner", data.getSecondaryHeroData().getImage().getAlt());
    assertEquals(
        "https://scth.scene7.com/is/image/scth/gallery_05",
        data.getSecondaryHeroData().getImage().getS7fileReference());
    assertEquals(
        "https://scth.scene7.com/is/image/scth/gallery_05",
        data.getSecondaryHeroData().getImage().getS7mobileImageReference());
    assertEquals("Create Itinerary", data.getSecondaryHeroData().getCreateItineraryLabel());
    assertTrue(data.getSecondaryHeroData().getHasFixedButton());
    assertFalse(data.getSecondaryHeroData().getLink().isTargetInNewWindow());
    assertEquals(
        "/bin/api/v2/user/update.favorites", data.getSecondaryHeroData().getUpdateFavUrl());
    assertEquals(
        "/bin/api/v2/user/delete.favorites", data.getSecondaryHeroData().getDeleteFavUrl());
    assertEquals(
        "/bin/api/v2/user/get.favorites?locale=en", data.getSecondaryHeroData().getGetFavUrl());
    assertEquals("/plan-your-trip/trip-planner", data.getSecondaryHeroData().getFavoritePath());
    assertEquals("Save to my favorites", data.getSecondaryHeroData().getSaveToFavoritesText());
    assertEquals("Saved to my favorites", data.getSecondaryHeroData().getSavedToFavoritesText());

    // tripPlannerStickyData
    assertEquals("Trip Planner", data.getTripPlannerStickyData().getTitle());
    assertEquals("planner", data.getTripPlannerStickyData().getCtaType());
    assertEquals("Create Itinerary", data.getTripPlannerStickyData().getLinkCopy());

    // tripPlannerModalData
    assertEquals("/content/dam/articles/batch-1/saudi-tourism-the-pros-of-guided-tours/guided_tours_hero_app_feature.jpg", data.getTripPlannerModalData().getBackgroundImage().get("mobileImage"));
    assertEquals("/content/dam/articles/batch-1/saudi-tourism-the-pros-of-guided-tours/guided_tours_hero_desktop_1920x860.jpg", data.getTripPlannerModalData().getBackgroundImage().get("desktopImage"));
    assertEquals("Placeholder", data.getTripPlannerModalData().getBackgroundImage().get("imageAlt"));
    assertEquals("/bin/api/v2/trip-plan", data.getTripPlannerModalData().getCreateTripPlanEndpoint());
    assertEquals("/bin/api/v2/trip-plan-filter.json", data.getTripPlannerModalData().getCitiesEndpoint());
    assertEquals("Where", data.getTripPlannerModalData().getStepsCopies().get("citiesStep").getLabel());
    assertEquals("Where are you travelling?", data.getTripPlannerModalData().getStepsCopies().get("citiesStep").getTitle());
    assertEquals("Save & Continue", data.getTripPlannerModalData().getStepsCopies().get("citiesStep").getButtonTitle());
    assertEquals("When", data.getTripPlannerModalData().getStepsCopies().get("dateSelectionStep").getLabel());
    assertEquals("When are you travelling?", data.getTripPlannerModalData().getStepsCopies().get("dateSelectionStep").getTitle());
    assertEquals("Create itinerary", data.getTripPlannerModalData().getStepsCopies().get("dateSelectionStep").getButtonTitle());
    assertEquals("Name", data.getTripPlannerModalData().getStepsCopies().get("tripNameStep").getLabel());
    assertEquals("Personalize your trip", data.getTripPlannerModalData().getStepsCopies().get("tripNameStep").getTitle());
    assertEquals("Save & Continue", data.getTripPlannerModalData().getStepsCopies().get("tripNameStep").getButtonTitle());
    assertEquals("Please login or Signup!", data.getTripPlannerModalData().getModalCopies().get("login").getTitle());
    assertEquals("You need to have an account in order to create your trip.", data.getTripPlannerModalData().getModalCopies().get("login").getParagraph());
    assertEquals("Login or Signup", data.getTripPlannerModalData().getModalCopies().get("login").getPrimaryButtonLabel());
    assertEquals("Oops!", data.getTripPlannerModalData().getModalCopies().get("error").getTitle());
    assertEquals("There was an error performing this action", data.getTripPlannerModalData().getModalCopies().get("error").getParagraph());
    assertEquals("Try Again", data.getTripPlannerModalData().getModalCopies().get("error").getPrimaryButtonLabel());
    assertEquals("Don´t leave!", data.getTripPlannerModalData().getModalCopies().get("close").getTitle());
    assertEquals("It looks like you are in the middle of creating an itinerary.\nChanges you made won´t be saved.",
        data.getTripPlannerModalData().getModalCopies().get("close").getParagraph());
    assertEquals("Leave", data.getTripPlannerModalData().getModalCopies().get("close").getPrimaryButtonLabel());

  }
}
