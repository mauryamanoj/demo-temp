package com.saudi.tourism.core.models.components.tripplan.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class TripDetailSlingModelTest {

  private static final String RESOURCE_PATH =
      "/content/sauditourism/en/trip-detail-page/jcr:content/root/responsivegrid/trip_editor";

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/trip-plans/trip-editor.json", RESOURCE_PATH);
  }

  @Test
  public void testTripDetailSlingModel(AemContext context) {
    // Arrange
    context.currentResource(RESOURCE_PATH);

    // Act
    final TripDetailSlingModel model =
        context.currentResource().adaptTo(TripDetailSlingModel.class);
    final String json = model.getJson();
    final TripDetailSlingModel data = gson.fromJson(json, TripDetailSlingModel.class);

    // Assert
    assertEquals("/bin/api/v1/activities.json", data.getAttractionsEndpoint());
    assertEquals("/bin/api/v2/user/get.trips", data.getTripPlanEndpoint());
    assertEquals("/bin/api/v2/user/update.trips", data.getUpdateTripPlanEndpoint());
    assertEquals("/bin/api/v1/holidays.json", data.getHolidaysEndpoint());
    assertEquals("/bin/api/v2/user/delete.trips", data.getDeleteTripPlanEndpoint());
    assertEquals("/bin/api/v1/events", data.getEventsEndpoint());
    assertEquals("/bin/api/v2/trip-plan-filter", data.getCitiesEndpoint());
    assertEquals("/bin/api/v1/", data.getFavoritesPath());
    assertEquals("/not-found", data.getNotFoundUrl());
    assertEquals("$0 Days", data.getCopies().getDaysCopy());
    assertEquals("$0 Nights", data.getCopies().getNightsCopy());
    assertEquals("Departure from $0", data.getCopies().getDepartureLabel());
    assertEquals("+ Add Activity", data.getCopies().getAddActivityLabel());
    assertEquals("Hours of service may differ", data.getCopies().getHolidayNotice());
    assertEquals("Move", data.getCopies().getMoveCtaLabel());
    assertEquals("Delete", data.getCopies().getDeleteCtaLabel());
    assertEquals("See More", data.getCopies().getSeeMoreCtaLabel());
    assertEquals(
        "Try deleting some activities if you wish to add more",
        data.getCopies().getDayFullActivities().getDescription());
    assertEquals("Your day is full", data.getCopies().getDayFullActivities().getTitle());
    assertEquals("Full day", data.getCopies().getDayFullLabelCopy());
    assertEquals("Select the start date", data.getCopies().getDatePickerPlaceholder());
    assertEquals("Add", data.getCopies().getAddCtaLabel());
    assertEquals("Remove", data.getCopies().getRemoveCtaLabel());
    assertEquals("The activity was added", data.getCopies().getActivityAdded());
    assertEquals("The activity was removed", data.getCopies().getActivityRemoved());
    assertEquals("Day", data.getCopies().getDayLabel());
    assertEquals("Week", data.getCopies().getWeekLabel());
    assertEquals("View next week", data.getCopies().getNextWeekButtonLabel());
    assertEquals("View previous week", data.getCopies().getPreviousWeekButtonLabel());
    assertEquals(
        "Start adding activities to this day", data.getCopies().getEmptyDay().getDescription());
    assertEquals("The day is empty", data.getCopies().getEmptyDay().getTitle());
    assertEquals("Edit", data.getCopies().getHero().getEditLabel());
    assertEquals("Multiple Destinations", data.getCopies().getHero().getMultipleDestinationsCopy());
    assertEquals("Single Destination", data.getCopies().getHero().getSingleDestinationCopy());
    assertEquals(
        "You need to have an account in order to create your trip.",
        data.getCopies().getLoginModal().getCopy());
    assertEquals("Please login or Signup!", data.getCopies().getLoginModal().getTitle());
    assertEquals("Login or Signup up", data.getCopies().getLoginModal().getCtaText());
    assertEquals(
        "Customise it day by day to make it your own",
        data.getCopies().getLoadingTripModal().getCopy());
    assertEquals("Loading trip itinerary", data.getCopies().getLoadingTripModal().getTitle());
    assertEquals("Day $0: $1", data.getCopies().getMoveScheduleItemModal().getDayLabel());
    assertEquals("Move Activity", data.getCopies().getMoveScheduleItemModal().getTitle());
    assertEquals("Day $0: $1", data.getCopies().getMoveScheduleItemModal().getDayLabel());
    assertEquals(
        "Select the day you want to move it to",
        data.getCopies().getMoveScheduleItemModal().getSubtitle());
    assertEquals("Confirm", data.getCopies().getMoveScheduleItemModal().getPrimaryButtonLabel());
    assertEquals("Cancel", data.getCopies().getMoveScheduleItemModal().getSecondaryButtonLabel());
    assertEquals("Attractions", data.getCopies().getScheduleModal().getAttractionsTabLabel());
    assertEquals("Events", data.getCopies().getScheduleModal().getEventsTabLabel());
    assertEquals("Favorites", data.getCopies().getScheduleModal().getFavoritesTabLabel());
    assertEquals(
        "Yes, remove it", data.getCopies().getRemoveActivityModal().getPrimaryButtonLabel());
    assertEquals("Hey!", data.getCopies().getRemoveActivityModal().getTitle());
    assertEquals(
        "You are about to remove an activity from your itinerary",
        data.getCopies().getRemoveActivityModal().getDescription());
    assertEquals(
        "Are you sure you want to remove this activity?",
        data.getCopies().getRemoveActivityModal().getSubtitle());
    assertEquals(
        "No, keep it", data.getCopies().getRemoveActivityModal().getSecondaryButtonLabel());
    assertEquals("Edit your trip", data.getCopies().getEditYourTripModal().getTitle());
    assertEquals(
        "Up to 5 days per destination",
        data.getCopies().getEditYourTripModal().getMaxDaysClarification());
    assertEquals("Save changes", data.getCopies().getEditYourTripModal().getSaveButtonLabel());
    assertEquals(
        "This city and all activities included will be removed from your itinerary",
        data.getCopies().getEditYourTripModal().getRegionDeleteClarification());
    assertEquals(
        "Edit Destination", data.getCopies().getEditYourTripModal().getEditDestinationsLabel());
    assertEquals(
        "Add Destinations", data.getCopies().getEditYourTripModal().getAddDestinationsLabel());
    assertEquals("Edit your trip", data.getCopies().getEditYourTripModal().getTitle());
    assertEquals("Hey!", data.getCopies().getDeleteTripPlanModal().getTitle());
    assertEquals(
        "Are you sure you want to delete your trip?",
        data.getCopies().getDeleteTripPlanModal().getSubtitle());
    assertEquals("Yes, Delete", data.getCopies().getDeleteTripPlanModal().getPrimaryButtonLabel());
    assertEquals(
        "No, Keep it", data.getCopies().getDeleteTripPlanModal().getSecondaryButtonLabel());
    assertEquals(
        "You are about to take an irreversible action.",
        data.getCopies().getDeleteTripPlanModal().getParagraph());
    assertEquals(
        "There was an error performing this action.",
        data.getCopies().getGenericErrorModal().getParagraph());
    assertEquals("Oops!", data.getCopies().getGenericErrorModal().getTitle());
    assertEquals("Try Again", data.getCopies().getGenericErrorModal().getPrimaryButtonLabel());
  }
}
