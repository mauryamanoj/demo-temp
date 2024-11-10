package com.saudi.tourism.core.services.weather;


import com.adobe.cq.social.community.api.CommunityConstants;
import com.day.cq.tagging.TagConstants;
import org.apache.jackrabbit.oak.commons.PathUtils;

/**
 * Constants used by Weather services.
 */
public final class WeatherConstants {

  /**
   * Weather memory cache prefix.
   */
  public static final String KEY_PREFIX_WEATHER = "weather:";

  /**
   * Mem cache key prefix for one call weather API responses.
   */
  public static final String PREFIX_EXTENDED = "extended";

  /**
   * Mem cache key prefix for simple / group call API responses.
   */
  public static final String PREFIX_SIMPLE = "simple";

  /**
   * Current.
   */
  public static final String CURRENT = "current";

  /**
   * History.
   */
  public static final String HISTORY = "history";

  /**
   * City property key.
   */
  public static final String CITY_NAME = "city";

  /**
   * Units parameter name.
   */
  public static final String PARAM_UNITS = "units";

  /**
   * Language parameter name.
   */
  public static final String PARAM_LANG = "lang";

  /**
   * Path prefix for city tag.
   */
  public static final String CITY_TAG_PREFIX = PathUtils.concat(
      CommunityConstants.CONTENT_ROOT_PATH, TagConstants.PN_TAGS,
      "sauditourism", WeatherConstants.CITY_NAME);

  /**
   * Number of strings that are separated by dash. This value is needed to be set to prevent city
   * names with dashes be split into separate values.
   */
  public static final int NUMBER_OF_VALUES_IN_KEY = 6;

  /**
   * Number of days to provide forecast for.
   */
  public static final int FORECAST_DAYS_LIMIT = 6;

  /**
   * Index of unit value.
   */
  public static final int INDEX_UNIT = 2;

  /**
   * Index of latitude value.
   */
  public static final int INDEX_LATITUDE = 3;

  /**
   * Index of longitude value.
   */
  public static final int INDEX_LONGITUDE = 4;

  /**
   * Index of city value.
   */
  public static final int INDEX_CITY = 5;

  /**
   * Number of hours in one day.
   */
  public static final int HOURS_IN_DAY = 24;

  /**
   * Default units for using in weather service.
   */
  public static final Unit DEFAULT_UNIT = Unit.METRIC;

  /**
   * Property name "list".
   */
  public static final String PN_LIST = "list";


  /**
   * Units enum.
   */
  public enum Unit {
    /**
     * Celsius.
     */
    METRIC,
    /**
     * Fahrenheit.
     */
    IMPERIAL,
    /**
     * Standard (open weather map standard settings - temperature in Kelvin and wind speed in
     * meter/sec).
     */
    STANDARD
  }

  /**
   * Hidden constructor.
   */
  private WeatherConstants() { }
}
