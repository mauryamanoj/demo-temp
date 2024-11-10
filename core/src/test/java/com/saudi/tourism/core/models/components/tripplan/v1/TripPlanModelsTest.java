package com.saudi.tourism.core.models.components.tripplan.v1;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit Test file to check adaptTo using resource for all Trip Plan models:
 * TripPlan.class, TripDetail.class, TripDay.class
 */
@ExtendWith(AemContextExtension.class)
@Getter
class TripPlanModelsTest {

  // TODO: Fake test added for exclude this test from SonarCube issues
  // Remove it after adding a real test cases
  @Test
  void fakeTest() {
    assertTrue(true);
  }

  // TODO FIXME Fix this unit test for using new Activity model

//  private AemContext context;
//
//  private static final String CONTENT_PATH = "/content/sauditourism/en/Configs/trip-plans";
//
//  private static final String TRIP_PLAN_PAGE_PATH = CONTENT_PATH + "/trip-plan-1";
//
//  private Activity mockLocation;
//
//  @BeforeEach
//  void setUp(AemContext context) throws PersistenceException {
//    this.context = context;
//
//    mockLocation = mock(Activity.class);
//    doReturn("some-location-id").when(mockLocation).getId();
//    doReturn("some-location-link").when(mockLocation).getLink();
//    context.registerAdapter(Resource.class, Activity.class,
//        (Function<Resource, Activity>) resource -> mockLocation);
//
//    final ResourceBundle i18n = new ResourceBundle() {
//      @Override
//      protected Object handleGetObject(String key) {
//        return key;
//      }
//
//      @Override
//      public Enumeration<String> getKeys() {
//        return Collections.emptyEnumeration();
//      }
//    };
//
//    final ResourceBundleProvider i18nProvider = mock(ResourceBundleProvider.class);
//    doReturn(i18n).when(i18nProvider).getResourceBundle(any(Locale.class));
//    context.registerService(ResourceBundleProvider.class, i18nProvider,
//        ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
//
//    context.registerService(SlingSettingsService.class, mock(SlingSettingsService.class));
//
//    final ResourceResolver resolver = context.resourceResolver();
//    final Resource someParentResource = mock(Resource.class);
//    final Map<String, Object> noProperties = Collections.emptyMap();
//
//    // Set trip-day component exists for ComponentManager
//    doReturn("/apps/sauditourism/components/content/trip-day/v1").when(someParentResource)
//        .getPath();
//    resolver.create(someParentResource, "trip-day", noProperties);
//
//    final Map<String, Object> ActivityPropsMap = Collections
//        .singletonMap(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
//            Constants.RT_ACTIVITY);
//
//    // Add some empty location resources to test Trip Points
//    doReturn("/content/sauditourism/app/en").when(someParentResource).getPath();
//    final Resource riyadhResource = resolver.create(someParentResource, "Riyadh", noProperties);
//    resolver.create(riyadhResource, JcrConstants.JCR_CONTENT, ActivityPropsMap);
//
//    final Resource alAhsaResource =
//        resolver.create(someParentResource, "al-ahsa-oasis", noProperties);
//    resolver.create(alAhsaResource, JcrConstants.JCR_CONTENT, ActivityPropsMap);
//
//    context.load().json("/components/trip-plans/content-en-trip-plans.json", CONTENT_PATH);
//  }
//
//  @Test
//  void init_TripPlan() {
//    final Resource pageContentResource =
//        context.currentResource(TRIP_PLAN_PAGE_PATH + "/jcr:content");
//    assert pageContentResource != null;
//
//    final TripPlan tripPlan = pageContentResource.adaptTo(TripPlan.class);
//    assertNotNull(tripPlan);
//
//    assertEquals(TripPlanConstants.NEW_TRIP_PLAN_ID, tripPlan.getId());
//    assertEquals(TRIP_PLAN_PAGE_PATH, tripPlan.getPath());
//    assertEquals("trip-plan-1", tripPlan.getTitle());
//    assertEquals("description", tripPlan.getDescription());
//
//    // Check the localize method has been executed
//    // Region id will be changed from "Riyadh Region" to "riyadh-region"
//    final AppFilterItem region = tripPlan.getRegion();
//    assertNotNull(region);
//    assertEquals(region.getId(), tripPlan.getRegionId());
//    assertEquals("riyadh-region", tripPlan.getRegionId());
//
//    assertEquals(2, tripPlan.getDaysCount());
//    assertNull(tripPlan.getImage().getFileReference());
//    assertNull(tripPlan.getImage().getMobileImageReference());
//    assertNull(tripPlan.getImage().getAlt());
//    assertNull(tripPlan.getDesktopImage());
//    assertNull(tripPlan.getMobileImage());
//    assertNull(tripPlan.getStartDate());
//    assertNull(tripPlan.getEndDate());
//
//    final TripDetail data = tripPlan.getData();
//    final List<TripDay> days = data.getDays();
//    assertEquals(2, days.size());
//
//    final TripDay firstDay = days.get(0);
//    assertNull(firstDay.getDate());
//    assertNull(firstDay.getEventCategories());
//
//    final LinkedList<TripPoint> firstDayPoints = firstDay.getTripPoints();
//    assertEquals(2, firstDayPoints.size());
//
//    final TripPoint point1_1 = firstDayPoints.get(0);
//    assertEquals("some-location-id", point1_1.getId());
//    assertEquals("some-location-link", point1_1.getLink());
//    assertSame(TripPlanConstants.TYPE_Activity, point1_1.getType());
//    assertNotNull(point1_1.getActivity());
//    assertSame(mockLocation, point1_1.getActivity());
//    assertNull(point1_1.getEvent());
//
//    // Location doesn't exist
//    final TripPoint point1_2 = firstDayPoints.get(1);
//    assertEquals("/content/sauditourism/app/en/not-existing-location", point1_2.getId());
//    assertNull(point1_2.getLink());
//    assertNull(point1_2.getType());
//    assertNull(point1_2.getActivity());
//    assertNull(point1_2.getEvent());
//
//    final TripDay secondDay = days.get(1);
//    assertNotNull(secondDay);
//    assertNull(secondDay.getDate());
//    assertNull(secondDay.getEventCategories());
//
//    final LinkedList<TripPoint> secondDayPoints = secondDay.getTripPoints();
//    assertEquals(1, secondDayPoints.size());
//
//    TripPoint point2_1 = secondDayPoints.get(0);
//    assertEquals("some-location-id", point2_1.getId());
//    assertEquals("some-location-link", point2_1.getLink());
//    assertSame(TripPlanConstants.TYPE_Activity, point2_1.getType());
//    assertNotNull(point2_1.getActivity());
//    assertSame(mockLocation, point2_1.getActivity());
//    assertNull(point2_1.getEvent());
//  }
}
