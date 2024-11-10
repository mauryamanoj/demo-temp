package com.saudi.tourism.core.models.components.tripplan.v1;

import com.saudi.tourism.core.utils.DynamicMediaUtils;

/**
 * Constants that are used only in Trip Planner.
 */
public final class TripPlanConstants {

  /**
   * Private constructor.
   */
  private TripPlanConstants() {
    // Do not instantiate.
  }

  /**
   * Constant for Day i18n bundle key.
   */
  static final String DAY = "Day";

  /**
   * Id for unsaved Trip Plan.
   */
  public static final String NEW_TRIP_PLAN_ID = "<new-unsaved-trip-plan>";

  /**
   * Default property name for a title of one tab of Tabs Component.
   */
  static final String PN_PANEL_TITLE = "cq:panelTitle";

  /**
   * The prefix for FE (react) day id.
   */
  static final String PREFIX_DAY = "day";

  /**
   * Serialization attribute name for the points list.
   */
  public static final String SCHEDULE = "schedule";

  /**
   * The default path to TripPlan component on a page.
   */
  static final String TRIP_DETAIL_PATH_ON_PAGE = "root/trip_detail";

  /**
   * Relative path from jcr:content to city property, to be able to add to search as facet.
   */
  public static final String CITY_PROP_PATH_ON_PAGE = TRIP_DETAIL_PATH_ON_PAGE + "/*/city";

  /**
   * Type of point: "event".
   */
  public static final String TYPE_EVENT = "event";

  /**
   * Type of point: "activity".
   */
  public static final String TYPE_ACTIVITY = "activity";

  /**
   * Param / json attribute name for city id "cityId".
   */
  public static final String CITY_ID = "cityId";

  /**
   * Default value of preferred trip days for city if it wasn't authored.
   */
  public static final Long DEFAULT_PREFERRED_TRIP_DAYS_COUNT = 3L;

  /**
   * Preferred trip days count for unknown cities.
   */
  public static final Long UNKNOWN_CITY_PREFERRED_TRIP_DAYS_COUNT = (long) Byte.MAX_VALUE;

  /**
   * Dynamic media cropping profile for trip plan hero banner.
   */
  public static final String CROP_HERO_DSK = DynamicMediaUtils.DM_CROP_1920_1080;

  /**
   * Dynamic media cropping profile for trip plan hero banner for mobile.
   */
  public static final String CROP_HERO_MOB = DynamicMediaUtils.DM_CROP_375_667;

  /**
   * Dynamic media cropping profile for events that were added into trip plan.
   */
  public static final String CROP_EVENT_POINT_DSK_CARD = DynamicMediaUtils.DM_CROP_1920_1080;

  /**
   * Dynamic media cropping profile for activities that were added into a trip plan.
   */
  public static final String CROP_ACTIVITY_POINT_DSK_CARD = DynamicMediaUtils.DM_CROP_1160_650;

  /**
   * Dynamic media cropping profile for cities displayed as cards in trip planner.
   */
  public static final String CROP_CITY_CARD_DSK = DynamicMediaUtils.DM_CROP_760_570;

  /**
   * Dynamic media cropping profile for cities displayed as cards in trip planner on mobile.
   */
  public static final String CROP_CITY_CARD_MOB = DynamicMediaUtils.DM_CROP_375_280;

  /**
   * Dynamic media cropping profile for activities displayed in adding activities window.
   */
  public static final String CROP_ACTIVITY_CARD_DSK = DynamicMediaUtils.DM_CROP_760_570;

  /**
   * Dynamic media cropping profile for activities displayed in adding activities window on mobile.
   */
  public static final String CROP_ACTIVITY_CARD_MOB = DynamicMediaUtils.DM_CROP_375_280;

}
