package com.saudi.tourism.core.utils;

import com.day.crx.JcrConstants;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.ComponentConstants;

import java.util.TimeZone;

/** PrimConstants. */
public class PrimConstants {

  /** Default constructor. */
  public PrimConstants() {

  }

  /** The constant SERVICE_DESCRIPTION. */
  public static final String SERVICE_DESCRIPTION = org.osgi.framework.Constants.SERVICE_DESCRIPTION;

  /** The Constant SEASONS_PARTIAL. */
  public static final String SEASONS_PARTIAL =
      Constants.FORWARD_SLASH_CHARACTER + "upcoming-seasons";

  /** The Constant RT_ADMIN_SEASONS. */
  public static final String RT_ADMIN_SEASONS =
      "sauditourism/components/structure/admin" + SEASONS_PARTIAL;

  /** The Constant RT_CONTENT_PAGE_RESOURCE_TYPE. */
  public static final String RT_CONTENT_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/page";

  /** The Constant RT_APP_PRODUCT_PAGE_RESOURCE_TYPE. */
  public static final String RT_APP_PRODUCT_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-product-page";

  /** The Constant CALENDAR . */
  public static final String CALENDAR = "calendar";

  /** The Constant article . */
  public static final String ARTICLE = "article";

  /** Default Time Zone for new Calendar instances. */
  public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Asia/Riyadh");

  /** Is internal link. */
  public static final String IS_INTERNAL = "isInternal";

  /** Link title. */
  public static final String LINK_TITLE = "linkTitle";

  /** Url. */
  public static final String URL = "url";

  /** LOCALE. */
  public static final String LOCALE = "locale";

  /** The constant HEADER_KEY. */
  public static final String HEADER_KEY = "header";

  /** The constant HEADER_KEY. */
  public static final long HEADER_CACHE_TIME = 28800000;

  /** Target in new window. */
  public static final String TARGET_IN_NEW_WINDOW = "targetInNewWindow";

  /** Empty space. */
  public static final String BLANK = "";

  /** HORIZONTAL_LABEL. */
  public static final String HORIZONTAL_LABEL = "highlight";

  /** VERTICAL_LABEL. */
  public static final String VERTICAL_LABEL = "vertical";

  /** Left bracket. */
  public static final String LEFT_BRACKET = "(";

  /** Right bracket. */
  public static final String RIGHT_BRACKET = ")";

  /** HTML extension. */
  public static final String HTML_EXTENSION = ".html";

  /** DOT. */
  public static final String DOT = ".";

  /** Sling model exporter selector for our models ("search"). */
  public static final String MODEL_EXPORTER_SELECTOR = Constants.SEARCH;

  /** CONTENT. */
  public static final String CONTENT = "content";

  /** The constant SOME_PARAMETERS_IS_EMPTY_OR_NULL. */
  public static final String SOME_PARAMETERS_IS_EMPTY_OR_NULL = "Some Parameters is empty or null";

  /** The constant EVENTS_SLIDER_COUNT. */
  public static final int EVENTS_SLIDER_COUNT = 3;

  /** The constant AEM_LANGAUAGE_PAGE_PATH_POSITION. */
  public static final int AEM_LANGAUAGE_PAGE_PATH_POSITION = 3;

  /** The constant AEM_APP_LANGAUAGE_PAGE_PATH_POSITION. */
  public static final int AEM_APP_LANGAUAGE_PAGE_PATH_POSITION = 4;

  /** The constant AEM_VENDOR_PAGE_PATH_POSITION. */
  public static final int AEM_VENDOR_PAGE_PATH_POSITION = 5;

  /** RESPONSIVE_GRID. */
  public static final String RESPONSIVE_GRID = "root/responsivegrid";

  /** The constant DEFAULT_LOCALE. */
  public static final String DEFAULT_LOCALE = "en";

  /** The constant BEST_FOR_LABEL. */
  public static final String BEST_FOR_LABEL = "Best for";

  /** The constant FEATURE_IMAGE. */
  public static final String NAV_IMAGE = "navImage";

  /** The constant FEATURE_IMAGE. */
  public static final String MOBILE_NAV_IMAGE = "mobileNavImage";

  /** The constant FEATURE_IMAGE. */
  public static final String FEATURE_IMAGE = "featureImage";

  /** Link node in Quick Links. */
  public static final String LINKS = "links";

  /** The constant EQUAL. */
  public static final String EQUAL = "=";

  /** The constant MINUS. */
  public static final String MINUS = "-";

  /** The constant DOTTED. */
  public static final String DOTTED = ".";

  /** The constant COMMA. */
  public static final String COMMA = ",";

  /** The constant SPACE. */
  public static final String SPACE = " ";

  /** The constant SPACE_MINUS_DASH. */
  public static final String SPACE_MINUS_DASH = " - ";

  /** The constant QUESTION_MARK. */
  public static final String QUESTION_MARK = "?";

  /** Characters constants. */
  public static final String FORWARD_SLASH_CHARACTER = "/";

  /** The constant DAM. */
  public static final String DAM = "/dam/";

  /** The constant COLON_SLASH_CHARACTER. */
  public static final String COLON_SLASH_CHARACTER = ":";

  /** The constant PATH_PROPERTY. */
  public static final String PATH_PROPERTY = "path";

  /** Property name "image". */
  public static final String PN_IMAGE = "image";

  /** Property name "itineraryDetails". */
  public static final String PN_ITINERARY_DETAILS = "itineraryDetails";

  /** Property name "bannerImage". */
  public static final String PN_BANNER_IMAGE = "bannerImage";

  /** Property name "bookNow". */
  public static final String PN_BOOK_NOW = "bookNow";

  /** Property name "featureEventImage". */
  public static final String PN_FEATURE_EVENT_IMAGE = "featureEventImage";

  /** Property for icon name "icon". */
  public static final String PN_ICON = "icon";

  /** The constant NUMBER_OF_SECONDS. */
  public static final int NUMBER_OF_SECONDS = 1000;

  /** The constant NUMBER_OF_MINUTES_IN_HOUR. */
  public static final int NUMBER_OF_SECONDS_IN_MINUTE = 60;

  /** The constant NUMBER_OF_MINUTES_IN_HOUR. */
  public static final int NUMBER_OF_MINUTES_IN_HOUR = 60;

  /** The constant DO_GET_MENTHOD. */
  public static final String DO_GET_MENTHOD = "[doGet] {}";

  /** Parameter is missing error (message format). */
  public static final String ERR_MISSING_PARAMETERS = "Missing required parameters: {0}";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_STATES. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_LOCALE =
      "Parameters [locale] is empty or null";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_START_DATE. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_START_DATE =
      "Parameter [startDate] is empty or null";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_END_DATE. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_END_DATE =
      "Parameter [endDate] is empty or null";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_AREA. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_AREA = "Parameters [area] is empty or null";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_CATEGORY. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_TYPE = "Parameters [type] for card is empty or null";

  /** The constant ERROR_MESSAGE_UNABLE_TO_ACCESS_SSP_CONFIG. */
  public static final String ERROR_MESSAGE_UNABLE_TO_ACCESS_SSP_CONFIG = "Unable to access SSP config for this locale";
  /**
   * The constant ERROR_MESSAGE_ISNULL_PARAM_CITY.
   */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_CITY = "Parameters [city] is empty or null";

  /** The constant ERROR_MESSAGE_INVALID_SYSTEM_CONFIGS. */
  public static final String ERROR_MESSAGE_INVALID_SYSTEM_CONFIGS = "Invalid system configus found";

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_CURRENCY_SOURCE. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_CURRENCY_SOURCE =
      "Parameters [currency source] is empty or null";

  /** The constant CURRENCY_DATA_SERVLET. */
  public static final String CURRENCY_DATA_SERVLET = "Currency Data Servlet";

  /** The constant EXPERIENCES_SERVLET. */
  public static final String EXPERIENCES_SERVLET = "Experiences servlet";

  /** The constant EXPERIENCES_FILTER_SERVLET. */
  public static final String EXPERIENCES_FILTER_SERVLET = "Experiences filter servlet";

  /** The constant EVENT_FILTER_SERVLET. */
  public static final String EVENT_FILTER_SERVLET = "Event filter servlet";

  /** The constant EVENT_APP_FILTERS_SERVLET V2. */
  public static final String EVENT_APP_FILTERS_SERVLET = "Event App filters servlet V2";

  /** The constant SEARCH_CONFIG_SERVLET. */
  public static final String SEARCH_CONFIG_SERVLET = "Search Config servlet";

  /** The constant EXPERIENCEDETAILS_SERVLET. */
  public static final String EXPERIENCEDETAILS_SERVLET = "Experiences Details servlet";

  /** The constant EXPERIENCEBOOKINGOPTIONS_SERVLET. */
  public static final String EXPERIENCEBOOKINGOPTIONS_SERVLET =
      "Experiences Booking Options servlet";

  /** The constant EXPERIENCESUGGESTIONS_SERVLET. */
  public static final String EXPERIENCESUGGESTIONS_SERVLET = "Experiences Suggestions servlet";

  /** The constant EXPERIENCECATEGORIES_SERVLET. */
  public static final String EXPERIENCECATEGORIES_SERVLET = "Experiences Categories servlet";

  /** The constant HALAYALLA_USER_ID. */
  public static final String HALAYALLA_USER_ID = "halaYallaUserId";

  /** The constant WEATHER_API_DATA_SERVLET. */
  public static final String WEATHER_DATA_SERVLET = "Weather Data Servlet";

  /** The constant EVENT_DETAIL_RES_TYPE. */
  public static final String EVENT_DETAIL_RES_TYPE =
      "sauditourism/components/structure/event-detail-page";

  /** The constant PACKAGE_DETAIL_RES_TYPE. */
  public static final String PACKAGE_DETAIL_RES_TYPE =
      "sauditourism/components/structure/package-detail-page";

  /** The constant HOTELS_RES_TYPE. */
  public static final String HOTELS_RES_TYPE = "sauditourism/components/structure/hotels-page";

  /** The constant PLACEHOLDERS_RES_TYPE. */
  public static final String PLACEHOLDERS_RES_TYPE =
      "sauditourism/components/structure/admin/placeholder";

  /** The constant API_V1_BASE_PATH. */
  public static final String API_V1_BASE_PATH = "/bin/api/v1/";

  /** The constant API_V2_BASE_PATH. */
  public static final String API_V2_BASE_PATH = "/bin/api/v2/";

  /** The constant FORMAT_ORIGINAL_DATE. */
  public static final String FORMAT_ORIGINAL_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  /** The constant FORMAT_OUTPUT_DATE. */
  public static final String FORMAT_OUTPUT_DATE = "yyyy-MM-dd'T'HH:mm:ssZZ";

  /** The constant FORMAT_HOUR_MINUTE. */
  public static final String FORMAT_HOUR_MINUTE = "HH:mm";

  /** The constant FORMAT_EEE_MMM_D_YYYYY. */
  public static final String FORMAT_EEE_MMM_D_YYYYY = "EEE, MMM d, yyyy";

  /** The constant FORMAT_DATE. */
  public static final String FORMAT_DATE = "yyyy-MM-dd";

  /**
   * Format date with week day and month. Result date: Tue 28, Aug It is used in Trip planner
   * displaying.
   */
  public static final String FMT_WD_D_M = "E dd, MMM";

  /**
   * Format date with week day, month and year. Result date: Tue 28, Aug 2020 It is used in Trip
   * planner displaying.
   */
  public static final String FMT_WD_D_M_Y = "E dd, MMM YYYY";

  /** The constant FORMAT_STARTDATE. */
  public static final String FORMAT_STARTDATE = "M-d";

  /** The constant FORMAT_ARTICLEDATE. */
  public static final String FORMAT_ARTICLEDATE = "MMM d, yyyy";

  /** The constant FORMAT_ARTICLEDATE_MONTH_DAY. */
  public static final String FORMAT_ARTICLEDATE_DAY_MONTH = "d/M";

  /** The constant FORMAT_EVENT_DATE. */
  public static final String FORMAT_EVENT_DATE = "MM/dd";

  /** The constant FORMAT_EVENT_DATE. */
  public static final String FORMAT_EVENT_DATE_ARABIC = "dd/MM";

  /** The constant FORMAT_DAY. */
  public static final String FORMAT_DAY = "dd";

  /** The constant FORMAT_MONTH. */
  public static final String FORMAT_NAME_MONTH = "MMM";

  /** The constant FORMAT_MONTH. */
  public static final String FORMAT_MONTH = "MM";

  /** The constant FORMAT_FULL_MONTH. */
  public static final String FORMAT_FULL_MONTH = "MMMM";

  /** The constant FORMAT_YEAR. */
  public static final String FORMAT_YEAR = "YYYY";

  /** The constant TAGS_URL. */
  public static final String TAGS_URL = "/content/cq:tags/";

  /** The constant ROOT_CONTENT_PATH. */
  @SuppressWarnings("squid:S1075")
  public static final String ROOT_CONTENT_PATH = "/content/sauditourism";

  /** The constant ROOT_EXP_FRAGS_PATH. */
  @SuppressWarnings("squid:S1075")
  public static final String ROOT_EXP_FRAGS_PATH = "/content/experience-fragments";

  /** Root app page node name. */
  public static final String NN_APP = "app1";

  /** The constant ROOT_CONTENT_PATH. */
  @SuppressWarnings("squid:S1075")
  public static final String ROOT_APP_CONTENT_PATH = "/content/sauditourism/" + NN_APP;

  /** The constant LANGUAGE_PLACEHOLDER. */
  public static final String LANGUAGE_PLACEHOLDER = "{language}";

  /** The node where the list of all languages is stored (/content/sauditourism/languages). */
  public static final String LANGS_PATH = ROOT_CONTENT_PATH + FORWARD_SLASH_CHARACTER + "languages";

  /** The constant for global configs path. */
  public static final String GLOBAL_CONFIG_PATH =
      ROOT_CONTENT_PATH + FORWARD_SLASH_CHARACTER + "global-configs";

  /** The constant CONFIG_URL. */
  public static final String CONFIG_URL = ROOT_CONTENT_PATH + "/{language}/Configs";

  /** The constant ADMIN_OPTIONS_PATH. */
  public static final String ADMIN_OPTIONS_PATH = CONFIG_URL + "/admin/jcr:content/options";

  /** The constant ADMIN_CONFIG_OPTIONS_PATH. */
  public static final String ADMIN_CONFIG_OPTIONS_PATH =
      "/{language}/Configs/admin/jcr:content/options";

  /** The constant ADMIN_SOLR_PATH. */
  public static final String ADMIN_SOLR_PATH = CONFIG_URL + "/admin/jcr:content/search";

  /** The constant ADMIN_ALERT_PATH. */
  public static final String ADMIN_ALERT_PATH = CONFIG_URL + "/admin/jcr:content/alerts";

  /** The constant ADMIN_ALERT_RAW_PATH. */
  public static final String ADMIN_ALERT_RAW_PATH = "/{language}/Configs/admin/jcr:content/alerts";

  /** The constant PACKAGE_SETTINGS_PATH. */
  public static final String PACKAGE_SETTINGS_PATH =
      CONFIG_URL + "/admin/jcr:content/packages-settings";

  /** The constant EVENTS_FILTERS_PATH. */
  public static final String EVENTS_FILTERS_PATH = CONFIG_URL + "/admin/jcr:content/events-filters";

  /** The constant Calendar_Path. */
  public static final String CALENDAR_INFO_PATH = CONFIG_URL + "/admin/jcr:content/calendar-info";

  /** The constant Chatbot_Path. */
  public static final String CHATBOT_INFO_PATH = CONFIG_URL + "/admin/jcr:content/chatbot";

  /** The constant USER_FEEDBACKS_INFO_PATH. */
  public static final String USER_FEEDBACKS_INFO_PATH =
      CONFIG_URL + "/admin/jcr:content/user-feedback-settings";

  /** The constant for path to package-form-config admin component. */
  public static final String ADMIN_PACKAGE_FORM_CONFIG_PATH =
      CONFIG_URL + "/admin/jcr:content/package-form-config";

  /** The constant for path to create-itinerary-config admin component. */
  public static final String ADMIN_ITINERARY_CONFIG_PATH =
      CONFIG_URL + "/admin/jcr:content/create-itinerary-config";

  /** Activities node name partial "/activities". */
  public static final String ACTIVITIES_PARTIAL = Constants.FORWARD_SLASH_CHARACTER + "activities";

  /** Holidays node name partial "/holidays". */
  public static final String HOLIDAYS_PARTIAL = Constants.FORWARD_SLASH_CHARACTER + "holidays";

  /** The path template for the holidays resource in admin config tree. */
  public static final String ADMIN_HOLIDAYS_PATH =
      CONFIG_URL + "/admin/jcr:content" + HOLIDAYS_PARTIAL;

  /**
   * Default path to view/edit user's trip plan, message format
   * (/content/sauditourism/{0}/trip-detail-page). TODO Fix this to correct value when is needed,
   * also corresponding `emptyText` in the file
   * /components/structure/admin/create-itinerary-config/_cq_dialog/.content.xml
   */
  public static final String DEFAULT_TRIP_PLAN_VIEW_EDIT_PATH =
      ROOT_CONTENT_PATH
          + FORWARD_SLASH_CHARACTER
          + "{0}"
          + FORWARD_SLASH_CHARACTER
          + "trip-detail-page";

  /** The memory cache key prefix for admin options. */
  public static final String KEY_PREFIX_ADMIN_OPTIONS = "admin-options:";

  /** The memory cache key prefix for admin alerts config. */
  public static final String KEY_PREFIX_ADMIN_ALERTS = "admin-alerts:";

  /** The memory cache key prefix for package settings config. */
  public static final String KEY_PREFIX_PACKAGE_SETTINGS = "package-settings:";

  /** The memory cache key prefix for events filters config. */
  public static final String KEY_PREFIX_EVENTS_FILTERS = "events-filters:";

  /** The memory cache key prefix for calendar data config. */
  public static final String KEY_PREFIX_CALENDAR_DATA = "calendar-info:";

  /** The memory cache key prefix for calendar data config. */
  public static final String KEY_PREFIX_CHATBOT = "chatbot:";

  /** The memory cache key prefix for user feedbacks config. */
  public static final String KEY_PREFIX_USER_FEEDBACKS = "user-feedbacks:";

  /** The memory cache key prefix for all configured holidays. */
  public static final String KEY_PREFIX_ADMIN_HOLIDAYS = "holidays:";

  /** The memory cache key prefix for all trip plans. */
  public static final String KEY_PREFIX_TRIP_PLANS = "trip-plans:";

  /** The memory cache key prefix for all activities. */
  public static final String KEY_PREFIX_ACTIVITIES = "activities:";

  /** The memory cache key prefix for all cities extended info. */
  public static final String KEY_PREFIX_CITIES = "cities:-";

  /** The constant EVENTS_CACHE_KEY. */
  public static final String EVENTS_CACHE_KEY = "events-";

  /** The constant PACKAGES. */
  public static final String PACKAGES = "packages";

  /** The constant PACKAGES_CACHE_KEY. */
  public static final String PACKAGES_CACHE_KEY = PACKAGES + "-";

  /** The constant HOTELS_CACHE_KEY. */
  public static final String HOTELS_CACHE_KEY = "hotels-";

  /** The constant PLACEHOLDERS_CACHE_KEY. */
  public static final String PLACEHOLDERS_CACHE_KEY = "placeholders";

  /** The constant EVENTS_CACHE. */
  public static final String EVENTS_CACHE = "eventlistModel";

  /** The constant READ_SERVICE. */
  public static final String READ_SERVICE = "readservice";

  /** The constant WRITE_SERVICE. */
  public static final String WRITE_SERVICE = "sitemapUser";

  /** The constant ZERO. */
  public static final int ZERO = 0;

  /** The constant MINUS_ONE. */
  public static final int MINUS_ONE = -1;

  /** The constant ONE. */
  public static final int ONE = 1;

  /** The constant TWO. */
  public static final int TWO = 2;

  /** The constant THREE. */
  public static final int THREE = 3;

  /** The constant FOUR. */
  public static final int FOUR = 4;

  /** The constant FIVE. */
  public static final int FIVE = 5;

  /** The constant IP. */
  public static final String IP = "ip";

  /** The constant LATITUDE. */
  public static final String LATITUDE = "latitude";

  /** The constant LONGITUDE. */
  public static final String LONGITUDE = "longitude";

  /** The constant START_DATE. */
  public static final String START_DATE = "startDate";

  /** The constant for end date parameter ("endDate"). */
  public static final String END_DATE = "endDate";

  /** The constant CLIENT_DATA. */
  public static final String CLIENT_DATA = "ClientData";

  /** The constant COUNT. */
  public static final String COUNT = "count";

  /** The constant INSERT. */
  public static final String INSERT = "insert";

  /** The constant M_TYPE. */
  public static final String M_TYPE = "mtype";

  /** The constant EVENT_CLIENT_DATA_CONFIG. */
  public static final String EVENT_CLIENT_DATA_CONFIG = "Event Client Data Config";

  /** The constant X_FORWARDED_FOR. */
  public static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

  /** The constant LIKES_COUNT. */
  public static final String LIKES_COUNT = "likesCount";

  /** The constant CHANGED. */
  public static final String CHANGED = "CHANGED";

  /** The constant ADDED. */
  public static final String ADDED = "ADDED";

  /** The constant REMOVED. */
  public static final String REMOVED = "REMOVED";

  /** Site root page. */
  public static final String SITE_ROOT = ROOT_CONTENT_PATH;

  /** Site root page. */
  public static final String SITE_VERSION_HISTORY = "/tmp/versionhistory";

  /** Templates root page. */
  public static final String TEMPLATES_ROOT = "/conf/sauditourism/settings/wcm/templates/";

  /** App root page. */
  public static final String APP_ROOT = "/content/sauditourism/app";

  /** New App root page. */
  public static final String NEW_APP_ROOT = "/content/sauditourism/app1";

  /** New App root page. */
  public static final String MOBILE_APP_ROOT = "/content/sauditourism/mobile";

  /** App root page. */
  public static final String APP_CONTACT_PAGE = "/contact/";

  /** App content template. */
  public static final String APP_CONTENT_TEMPLATE = TEMPLATES_ROOT + "app-content-page";

  /** App loyalty content template. */
  public static final String APP_LOYALTY_CONTENT_TEMPLATE =
      TEMPLATES_ROOT + "app-loyalty-content-page";

  /** App location template. */
  public static final String APP_LOCATION_TEMPLATE = TEMPLATES_ROOT + "app-location-page";

  /** App seasonal template. */
  public static final String APP_SEASONAL_TEMPLATE = TEMPLATES_ROOT + "app-seasonal-campaign";

  /** App package template. */
  public static final String APP_PACKAGE_TEMPLATE = TEMPLATES_ROOT + "app-package-page";

  /** App Cruise template. */
  public static final String APP_CRUISE_TEMPLATE = TEMPLATES_ROOT + "app-cruise-page";

  /** App location resource type. */
  public static final String APP_LOCATION_RESOURCE_TYPE =
      "sauditourism/components/structure/app-location-page";

  /** Activity page resource type. */
  public static final String RT_ACTIVITY = "sauditourism/components/structure/activity-page";

  /** App package resource type. */
  public static final String APP_PACKAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-package-page";

  /** Resource type of the the App Cruise page component. */
  public static final String RT_APP_CRUISE_PAGE =
      "sauditourism/components/structure/app-cruise-page";

  /** Resource type of the the App seasonal campaign page component. */
  public static final String RT_APP_SEASON_CAMPAIGN_PAGE =
      "sauditourism/components/structure/app-seasonal-campaign";

  /** Location type: "cruise" property value. */
  public static final String PV_CRUISE = "cruise";

  /** Resource type of the the App summer categories page component. */
  public static final String RT_APP_SUMMER_CATEGORIES =
      "sauditourism/components/structure/app-summer-categories";

  /** Parameter name "locale". */
  public static final String PN_LOCALE = "locale";

  /** Parameter name "region". */
  public static final String PN_REGION = "region";

  /** Parameter name "cityId". */
  public static final String PN_CITY_ID = "cityId";

  /** Parameter name "regionId". */
  public static final String PN_REGION_ID = "regionId";

  /** Parameter name "pageCategory". */
  public static final String PN_PAGE_CATEGORY = "pageCategory";

  /** Parameter name "category". */
  public static final String PN_CATEGORY = "category";

  /** The constant PARAM_FULLTEXT. */
  public static final String PARAM_QUERY_TEXT = "q";

  /** The constant PARAM_RESULTS_OFFSET. */
  public static final String PARAM_RESULTS_OFFSET = "offset";

  /** The constant LIMIT. */
  public static final String LIMIT = "limit";

  /** The constant PARAM_TAGS. */
  public static final String PARAM_TAGS = "tags";

  /** The constant PREDICATE_FULLTEXT. */
  public static final String PREDICATE_FULLTEXT = "fulltext";

  /** The constant PREDICATE_FULLTEXT. */
  public static final String FULLTEXT_REL_PATH = "_fulltext.relPath";

  /** Bool property for query builder predicate. */
  public static final String BOOL_PROPERTY = "boolproperty";

  /** hideInNav Constant. */
  public static final String JCR_CONTENT_HIDE_IN_NAV = "jcr:content/hideInNav";

  /** hideInNav property. */
  public static final String HIDE_IN_NAV = "hideInNav";

  /** notsolrindexable property. */
  public static final String NOT_SOLR_INDEXABLE = "notsolrindexable";

  /** searchHighlight property. */
  public static final String SEARCH_HIGHLIGHT = "searchHighlight";

  /** priority property. */
  public static final String PRIORITY = "priority";

  /** Bool property value for query builder predicate. */
  public static final String BOOLPROPERTY_VALUE = "boolproperty.value";

  /** The constant PREDICATE_TYPE. */
  public static final String PREDICATE_TYPE = "type";

  /** The constant PREDICATE_PATH. */
  public static final String PREDICATE_PATH = "path";

  /** The constant PROP_RESULTS_SIZE_DEFAULT. */
  public static final int DEFAULT_RESULTS_SIZE = 10;

  /** The constant APP_CONTENT_RESOURCE_TYPE. */
  public static final String APP_CONTENT_RESOURCE_TYPE =
      "sauditourism/components/structure/app-page";

  /** The constant LEGAL_CONTENT_PAGE_RESOURCE_TYPE. */
  public static final String APP_LEGAL_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-legal-page";

  /** The constant APP_PRODUCT_PAGE_RESOURCE_TYPE. */
  public static final String APP_PRODUCT_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-product-page";

  /** App contacts page resource type. */
  public static final String APP_CONTACTS_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-contact-page";

  /** App trad page resource type. */
  public static final String APP_TRAD_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/app-trad-page";

  /** The constant PAGE_RESOURCE_TYPE. */
  public static final String PAGE_RESOURCE_TYPE = "sauditourism/components/structure/page";

  /** App GROUP_PREDICATE_1. */
  public static final String GROUP_PREDICATE_1 = "1_group.";

  /** App content component resource type. The constant GROUP_PREDICATE. */
  public static final String APP_CONTENT_COMPONENT_RESOURCE_TYPE =
      "sauditourism/components/content/text";

  /** App image component resource type. */
  public static final String APP_CONTENT_IMAGE_RESOURCE_TYPE =
      "sauditourism/components/content/image";

  /** Cards Grid component resource type. */
  public static final String CARDS_GRID_RESOURCE_TYPE =
      "sauditourism/components/content/cards-grid";

  /** Top Activities component resource type. */
  public static final String TOP_ACTIVITIES_RESOURCE_TYPE =
      "sauditourism/components/content/top-activities/v1/top-activities";

  /**
   * Holidays configuration component resource type
   * "sauditourism/components/structure/admin/holidays".
   */
  public static final String RT_ADMIN_HOLIDAYS =
      "sauditourism/components/structure/admin" + HOLIDAYS_PARTIAL;

  /** Trip Plan page (Trip Itinerary Page) resource type. */
  public static final String RT_TRIP_PLAN_PAGE =
      "sauditourism/components/structure/trip-itinerary-page";

  /** Trip Plan component resource type. */
  public static final String RT_TRIP_DETAIL =
      "sauditourism/components/content/trip-planner/v1/trip-detail";

  /** Trip Day component resource type. */
  public static final String RT_TRIP_DAY =
      "sauditourism/components/content/trip-planner/v1/trip-day";

  /** TripPlan: City Itinerary component resource type. */
  public static final String RT_CITY_ITINERARY =
      "sauditourism/components/content/trip-planner/v1/city-itinerary";

  /** Resource type of the city extended data editing component. */
  public static final String RT_CITY_COMPONENT = "sauditourism/components/content/city";

  /** Resource type of app's game page. */
  public static final String RT_APP_GAME_PAGE = "sauditourism/components/structure/app-game-page";

  /** Resource type of app's region component. */
  public static final String RT_APP_REGION_PAGE =
      "sauditourism/components/structure/app-region-page";

  /** Resource type of app's city component. */
  public static final String RT_APP_CITY_PAGE = "sauditourism/components/structure/app-city-page";

  /** i18n key. */
  public static final String SLING_KEY = "sling:key";

  /** i18n App Value. */
  public static final String SLING_VALUE = "sling:value";

  /** i18n translation. */
  public static final String SLING_MESSAGE = "sling:message";

  /** i18n path. */
  @SuppressWarnings("squid:S1075")
  public static final String APP_SAUDITOURISM_I18N_PATH = "/apps/sauditourism/i18n";

  /** App i18n path. */
  public static final String APP_SAUDITOURISM_APP_I18N_PATH = "/apps/sauditourism-app/i18n";

  /** App i18n path. */
  public static final String APP_SAUDITOURISM_APP_I18N_PATH_NATIVE_APP =
      "/apps/sauditourism-app/i18n/native-app";

  /** App json extension. */
  public static final String JSON_EXTENSION = ".json";

  /** App trips extension. */
  public static final String TRIPS_EXTENSION = ".trips";

  /** App publish date. */
  public static final String APP_PUBLISH_DATE = "publishDate";

  /** App location type. */
  public static final String APP_LOCATION_TYPE = "type";

  /** App location seasonal campaign type. */
  public static final String APP_SEASONAL_CAMPAIGN_TYPE = "seasonal-campaign";

  /** App location summer categories type. */
  public static final String APP_SUMMER_CATEGORIES_TYPE = "summer-categories";

  /** App _group. */
  public static final String GROUP_PREDICATE = "_group.";

  /** Search group Constant for p.or. */
  public static final String P_OR = "p.or";

  /** Search group Constant for p.not. */
  public static final String P_NOT = "p.not";

  /** Search group Constant for path.self. */
  public static final String PATH_SELF = "path.self";

  /** String constant false. */
  public static final String STR_FALSE = "false";

  /** Constant for Configs Page under locale. */
  public static final String CONFIGS = "Configs";

  /** The Constant SEASONS_PATH. */
  public static final String SEASONS_PATH =
      ROOT_CONTENT_PATH
          + FORWARD_SLASH_CHARACTER
          + "{0}"
          + FORWARD_SLASH_CHARACTER
          + CONFIGS
          + FORWARD_SLASH_CHARACTER
          + "admin/jcr:content/upcoming-seasons";

  /**
   * Default path to all trip plans, message format (/content/sauditourism/{0}/Configs/trip-plans).
   */
  public static final String TRIP_PLANS_PATH =
      ROOT_CONTENT_PATH
          + FORWARD_SLASH_CHARACTER
          + "{0}"
          + FORWARD_SLASH_CHARACTER
          + CONFIGS
          + FORWARD_SLASH_CHARACTER
          + "trip-plans";

  /**
   * Default path to all activities, message format (/content/sauditourism/{0}/Configs/activities).
   */
  public static final String ACTIVITIES_PATH =
      ROOT_CONTENT_PATH
          + FORWARD_SLASH_CHARACTER
          + "{0}"
          + FORWARD_SLASH_CHARACTER
          + CONFIGS
          + ACTIVITIES_PARTIAL;

  /** Constant for author run mode. */
  public static final String AUTHOR = "author";

  /** Constant for PAGE_TITLE. */
  public static final String PAGE_TITLE = "pageTitle";

  /** Constant for SEARCH. */
  public static final String SEARCH = "search";

  /** Constant for SEARCH_QUERY. */
  public static final String SEARCH_QUERY = "q";

  /** Constant for STATE. */
  public static final String STATE = "state";

  /** Constant for RESULTS. */
  public static final String RESULTS = "results";

  /** Constant for PREVIOUS. */
  public static final String PREVIOUS = "previous";

  /** Constant for NEXT. */
  public static final String NEXT = "next";

  /** The Constant to append on fulltext search word to enable partial text search. */
  public static final String QUERY_PARTIAL_TEXT_CHARACTER = "*";

  /** The constant LOCATION icon path. */
  public static final String LOCATION_ICON =
      "/etc.clientlibs/sauditourism/clientlibs/clientlib"
          + "-site/resources/default-assets/location-icon/location-icon.png";

  /** Search query syntax notexpired. */
  public static final String NOTEXPIRED = "notexpired";

  /** Search query syntax true. */
  public static final String STR_TRUE = "true";

  /** Search query syntax notexpired.property. */
  public static final String NOTEXPIRED_PROPERTY = "notexpired.property";

  /** Search query syntax JCR_CONTENT_CALENDAR_END_DATE. */
  public static final String JCR_CONTENT_CALENDAR_END_DATE = "jcr:content/calendarEndDate";

  /** Arabic Locale Constant. */
  public static final String ARABIC_LOCALE = "ar";

  /** Arabic Locale Constant. */
  public static final String DATE_TYPE = "date";

  /** Constant UNDERSCORE. */
  public static final String UNDERSCORE = "_";

  /** The constant NAVIGATION_TYPE. */
  public static final String NAVIGATION_TYPE = "navType";

  /** The constant ARTICLE_PATH. */
  public static final String ARTICLE_PATH = "articlePath";

  /** The constant CITIES. */
  public static final String CITIES = "cities";

  /** The constant CURRENCIES. */
  public static final String CURRENCIES = "currencies";

  /** The constant PLANS. */
  public static final String PLANS = "plans";

  /** The constant CODE. */
  public static final String CODE = "code";

  /** The constant VALUE. */
  public static final String VALUE = "value";

  /** The constant TEXT. */
  public static final String TEXT = "text";

  /** The constant PLAN_TITLE. */
  public static final String PLAN_TITLE = "planTitle";

  /** The constant PLAN_DESCRIPTION. */
  public static final String PLAN_DESCRIPTION = "planDescription";

  /** The constant PLAN_URL. */
  public static final String PLAN_URL = "planUrl";

  /** The constant PLAN_ICON. */
  public static final String PLAN_ICON = "planIcon";

  /** The constant EVENT_START. */
  public static final String EVENT_START = "calendarStartDate";

  /** The constant EVENT_END. */
  public static final String EVENT_END = "calendarEndDate";

  /** The constant CITY. */
  public static final String CITY = "city";

  /** The constant DESTINATIONS. */
  public static final String DESTINATIONS = "destinations";

  /** The constant CITY. */
  public static final String ACTIVITY = "activity";

  /** The constant CITY. */
  public static final String LEARNMORE = "learnmore";

  /** CONST_APPLICATION_JSON. */
  public static final String CONST_APPLICATION_JSON = "application/json";

  /** CONST_ACCEPT. */
  public static final String CONST_ACCEPT = "Accept";

  /** CONST_GET. */
  public static final String CONST_GET = "GET";

  /** PREFIX_CURRENCY. */
  public static final String PREFIX_CURRENCY = "currency-";

  /** CONST_STR_CURRENCIES. */
  public static final String CONST_STR_CURRENCIES = "currencies";

  /** CONST_STR_CURRENCY. */
  public static final String CONST_STR_CURRENCY = "currency";

  /** CONST_QUOTES. */
  public static final String CONST_QUOTES = "quotes";

  /** CONST_SOURCE. */
  public static final String CONST_SOURCE = "source";

  /** CONST_SOURCE. */
  public static final String LABEL = "label";

  /** CONST_WEATHER. */
  public static final String CONST_WEATHER = "weather";

  /** CONST_TEMP. */
  public static final String CONST_TEMP = "temp";

  /** CONST_TEMP_MIN. */
  public static final String CONST_TEMP_MIN = "temp_min";

  /** CONST_TEMP_MAX. */
  public static final String CONST_TEMP_MAX = "temp_max";

  /** CONST_TEMPMIN. */
  public static final String CONST_TEMPMIN = "tempMin";

  /** CONST_TEMPMAX. */
  public static final String CONST_TEMPMAX = "tempMax";

  /** CONST_MAIN. */
  public static final String CONST_MAIN = "main";

  /** CONST_CITY. */
  public static final String CONST_CITY = "city";

  /** CONST_NAME. */
  public static final String CONST_NAME = "name";

  /** CONST_PARAM_Q. */
  public static final String CONST_PARAM_Q = "q";

  /** CONST_PARAM_APPID. */
  public static final String CONST_PARAM_APPID = "APPID";

  /** CONST_PARAM_LAT. */
  public static final String CONST_PARAM_LAT = "lat";

  /** CONST_PARAM_LON. */
  public static final String CONST_PARAM_LON = "lon";

  /** CONST_AMPERSAND. */
  public static final String CONST_AMPERSAND = "&";

  /** CONST_ITEM_LABEL. */
  public static final String CONST_ITEM_LABEL = "itemLabel";

  /** CONST_ITEM_VALUE. */
  public static final String CONST_ITEM_VALUE = "itemValue";

  /** CONST_ITEM_VALUE. */
  public static final String CONST_MORNING = "morning";

  /** CONST_ITEM_VALUE. */
  public static final String CONST_NOON = "noon";

  /** CONST_ITEM_VALUE. */
  public static final String CONST_AFTERNOON = "afternoon";

  /** CONST_ITEM_VALUE. */
  public static final String CONST_EVENING = "evening";

  /** Sling type property value "cq:PageContent". */
  public static final String TYPE_PAGE_CONTENT = "cq:PageContent";

  /** Query param limit ("p.limit"). */
  public static final String QP_LIMIT = "p.limit";

  /** Query param "hits". */
  public static final String QP_HITS = "hits";

  /** Query hits param value "selective". */
  public static final String QPV_SELECTIVE = "selective";

  /** Jcr Query `p.limit` parameter value to disable limit ("-1"). */
  public static final String QPV_NO_LIMIT = "-1";

  /** The class of ResourceBundle provider service. */
  public static final String I18N_RESOURCE_BUNDLE_PROVIDER_CLASS =
      "org.apache.sling.i18n.impl.JcrResourceBundleProvider";

  /** The full target to the I18n provider. */
  public static final String I18N_PROVIDER_SERVICE_TARGET =
      "("
          + ComponentConstants.COMPONENT_NAME
          + Constants.EQUAL
          + Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS
          + ")";

  /** Property name "id". */
  public static final String PN_ID = "id";

  /** The constant ID. */
  public static final String ID = "{id}";

  /** Dynamic Media Profile Preset for URL. */
  public static final String DM_PROFILE_PRESET = ":{preset}";

  /** S7 Image Content path for image. */
  public static final String S7_IMAGE_CONTENT = "is/image/";

  /** S7 Image Content path for video. */
  public static final String S7_VIDEO_CONTENT = "is/content/";

  /** Image Profile Height. */
  public static final String PN_HEI = "hei";

  /** Image Profile width. */
  public static final String PN_WID = "wid";

  /** Dynamic Media profile key. */
  public static final String DYNAMIC_MEDIA_PROFILES = "dynamic-media-profiles";

  /** Image Profile base path. */
  public static final String IMAGE_PROFILE_BASE_PATH =
      "/conf/global/settings/dam/adminui-extension/imageprofile";

  /** Path to dam/sauditourism. */
  public static final String DAM_SAUDITOURISM_PATH = "/content/dam/sauditourism";

  /** URL param/suffix for Dynamic Media profile ":crop". */
  public static final String PARTIAL_CROP = ":crop";

  /** Scene7 domain. */
  public static final String SCENE7_DOMAIN = "https://scth.scene7.com/";

  /** TOKEN_HEADER_STR. */
  public static final String TOKEN_HEADER_STR = "token";

  /** CLIENTKEY_HEADER_STR. */
  public static final String CLIENTKEY_HEADER_STR = "x-client-key";

  /** CLIENTSECRET_HEADER_STR. */
  public static final String CLIENTSECRET_HEADER_STR = "x-client-secret";

  /** Scene7 domain. */
  public static final String SCENE7_AKAMAI_DOMAIN = "https://s7g10.scene7.com/";

  /** Scene7 CN domain. */
  public static final String SCENE7_CN_DOMAIN = "https://assets.visitsaudi.cn/";

  /** The constant VISITSAUDI_DOMAIN_COM. */
  public static final String VISITSAUDI_DOMAIN_COM = "https://www.visitsaudi.com";

  /** The constant Property Name PN_TITLE. */
  public static final String PN_TITLE = "title";

  /** The constant Property Name PN_GLOBAL_PACKAGE_TITLE. */
  public static final String PN_GLOBAL_PACKAGE_TITLE = "globalPackageTitle";

  /** The constant DEFAULT_PACKAGE_HERO_EVENT. */
  public static final String DEFAULT_TOUR_PACKAGE_HERO_EVENT = "Tour Package CTA Clicked";

  /** The constant STARTING_FROM_I18KEY for package price. */
  public static final String STARTING_FROM_I18KEY = "startingFromCapitalLabel";

  /** The constant FROM_SAR_I18KEY for package price. */
  public static final String FROM_SAR_I18KEY = "startingFrom2";

  /** The constant FROM3_SAR_I18KEY for package price. */
  public static final String FROM3_SAR_I18KEY = "startingFrom3";

  /** The constant AEM_PACKAGES_DMC_PATH_POSITION. */
  public static final int AEM_PACKAGES_DMC_PATH_POSITION = 5;

  /** The constant DEFAULT_ZOOM. */
  public static final String DEFAULT_ZOOM = "11";

  /** The Regex Pattern for blank tags. */
  public static final String BLANK_TAG_REGEX_PATTERN = "<(\\w)>(\\u00A0)*<\\/\\1>";

  /** The Regex Pattern for empty tags. */
  public static final String EMPTY_TAG_REGEX_PATTERN =
      "<(\\w)>\\s*\\n*\\t*(&nbsp;)\\s*\\n*\\t*<\\/\\1>";

  /** The Regex Pattern for Opening paragraph tags. */
  public static final String OPEN_PARAGRAPH_TAG = "<p>";

  /** The Regex Pattern for Closing paragraph tags. */
  public static final String CLOSE_PARAGRAPH_TAG = "</p>";

  /** The Regex Pattern for Line Break tags. */
  public static final String BR_TAG = "<br>";

  /** Property name PACKAGE AREA TAGS. */
  public static final String PN_PACKAGE_AREA_TAGS = "packageAreaTags";

  /** Property name 'variant'. */
  public static final String PN_VARIANT = "variant";

  /** I18N_MULTIPLE_LOCATION_KEY. */
  public static final String I18N_MULTIPLE_LOCATION_KEY = "multiplelocations";

  /** I18N_MULTIPLE_LOCATION_KEY. */
  public static final String I18N_MULTIPLE_DESTINATION_KEY = "multipledestinations";

  /** The constant SEARCH_RESULT_RESOURCE_TYPE. */
  public static final String SEARCH_RESULT_RESOURCE_TYPE =
      "sauditourism/components/structure/search-results-page";

  /** The constant SOLR_KEY. */
  public static final String SOLR_SEARCH = "search";

  /** The constant SOLR_KEY. */
  public static final String SOLR_KEY = "solrKey";

  /** The constant SUGGESTION_TYPE. */
  public static final String SUGGESTION_TYPE = "type";

  /** The constant SUGGESTION_TYPE. */
  public static final String SUGGESTION_URL_WEB = "urlWeb";

  /** The constant SUGGESTION_TYPE. */
  public static final String SUGGESTION_URL_APP = "urlApp";

  /** The constant TRENDING_PAGE. */
  public static final String TRENDING_PAGE = "trendingPage";

  /** The constant TRENDING_PAGE_APP. */
  public static final String TRENDING_PAGE_APP = "trendingPageApp";

  /** The constant FEATURED_PAGE. */
  public static final String FEATURED_PAGE = "featuredPage";

  /** The constant SEARCH_CATEGORY. */
  public static final String SEARCH_CATEGORY = "searchCategory";

  /** The constant SOLR_APP_SOURCE. */
  public static final String SOLR_APP_SOURCE = "app";

  /** The constant SOLR_MOBILE_SOURCE. */
  public static final String SOLR_MOBILE_SOURCE = "mobile";

  /** The constant SOLR_WEB_SOURCE. */
  public static final String SOLR_WEB_SOURCE = "web";

  /** The constant SOLR_TYPE_HIGHLIGHT. */
  public static final String SOLR_TYPE_HIGHLIGHT = "Highlight";

  /** The constant SOLR_TYPE_TRENDING. */
  public static final String SOLR_TYPE_TRENDING = "Trending";

  /** The constant SOLR_KEY. */
  public static final String SOLR_FREETEXTSUGGESTER = "freeTextSuggester";

  /** ZAHID_TRAVEL_GROUP. */
  public static final String ZAHID_TRAVEL_GROUP = "zahid-travel-group";

  /** PACKAGE_POWERED_BY. */
  public static final String PACKAGE_POWERED_BY = "package-powered-by";

  /** TYPE_EXTERNAL. */
  public static final String TYPE_EXTERNAL = "external";

  /** The constant "internal". */
  public static final String TYPE_INTERNAL = "internal";

  /** The constant "none". */
  public static final String TYPE_NONE = "none";

  /** Dash symbol. */
  public static final String DASH = "-";

  /** Region suffix "-region". */
  public static final String REGION_SUFFIX = DASH + PN_REGION;

  /** Project id. */
  public static final String PROJECT_ID = "sauditourism";

  /** Path to tags: city. */
  public static final String TAG_CITY_PATH =
      TAGS_URL + PROJECT_ID + FORWARD_SLASH_CHARACTER + "city";

  /** Default Image Placeholder. */
  public static final String DEFAULT_IMAGE_PLACEHOLDER =
      "/content/dam/sauditourism/placeholder.jpg";

  /** Scene 7 image sharpen parameter. */
  public static final String DEFAULT_S7_IMAGE_SHARP_PARAM =
      "fmt=jpg&qlt=90,0&resMode=sharp2&op_usm=1.75,0.3,2,0";

  /** Date Format constant DD/MM/YY. */
  public static final String DATE_FORMAT_DD_MM_YY = "dd/mm/yy";

  /**
   * The constant for the path to the content resource of cities extended data editing page -
   * "/content/sauditourism/global-configs/Configs/cities/jcr:content".
   */
  public static final String GLOBAL_CITIES_EXT_DATA_CONTENT =
      StringUtils.join(
          new String[] {GLOBAL_CONFIG_PATH, CONFIGS, CITIES, JcrConstants.JCR_CONTENT},
          FORWARD_SLASH_CHARACTER);

  /** Cities memory cache key prefix. */
  public static final String EXT_CITY_MEM_CACHE_KEY_PREFIX = "cities-extended:";

  /** Https scheme. */
  public static final String HTTPS_SCHEME = "https";

  /** Scene 7 domain. */
  public static final String SCENE7_DOMAIN_FRAGMENT = "scth/ugc";

  /** Desktop crop for event detail page. */
  public static final String EVENT_DESKTOP_CROP = DynamicMediaUtils.DM_CROP_1920_1080;

  /** Mobile crop for event detail page. */
  public static final String EVENT_MOBILE_CROP = DynamicMediaUtils.DM_CROP_260_195;

  /** China server run mode. */
  public static final String CN_SERVER_RUN_MODE = "cn";

  private void call() {

  }

  /** EVENT_SLIDER_DEFAULT_ORNAMENT. */
  public static final String EVENT_SLIDER_DEFAULT_ORNAMENT = "02";

  /** SIMPLE_SLIDER_DEFAULT_ORNAMENT. */
  public static final String SIMPLE_SLIDER_DEFAULT_ORNAMENT = "01";

  /** MAP_SLIDER_DEFAULT_ORNAMENT. */
  public static final String MAP_SLIDER_DEFAULT_ORNAMENT = "03B";

  /** CATEGORY_SLIDER_DEFAULT_ORNAMENT. */
  public static final String CATEGORY_SLIDER_DEFAULT_ORNAMENT = "03A";

  /** MAP_COMPONENT_DEFAULT_ORNAMENT. */
  public static final String MAP_COMPONENT_DEFAULT_ORNAMENT = "10";

  /** MAP_COMPONENT_DEFAULT_VARIANT. */
  public static final String MAP_COMPONENT_DEFAULT_VARIANT = "color";

  /** Path to city components. */
  public static final String CITIES_PATH_FORMAT = ROOT_APP_CONTENT_PATH + "/%s/cities";

  /** The Constant SUCCESS_RESPONSE_CODE. */
  public static final Integer SUCCESS_RESPONSE_CODE = 200;

  /** The Constant FEATURED_IMAGE . */
  public static final String FEATURED_IMAGE = "featuredImage";

  /** The Constant PREVIEW_FEATURED_IMAGE. */
  public static final String PREVIEW_FEATURED_IMAGE = "previewImage";

  /** The Constant FEATURED. */
  public static final String FEATURED = "featured";

  /** The constant CARDS_RES_TYPE. */
  public static final String CARDS_RES_TYPE = "sauditourism/components/structure/cards-page";

  /** BRAND_ROOT_PAGE. */
  public static final String BRAND_ROOT_PAGE = "brands-earning-partner";

  /** BRANDS_RES_TYPE. */
  public static final String BRANDS_RES_TYPE =
      "sauditourism/components/structure/brand-detail-page";

  /** The constant BRANDS_CACHE_KEY. */
  public static final String BRANDS_CACHE_KEY = "brands-";

  /** The constant APP_SOURCE. */
  public static final String APP_SOURCE = "app";

  /** The constant WEB_SOURCE. */
  public static final String WEB_SOURCE = "web";

  /** The constant Internal server error. */
  public static final String INTERNAL_SERVER_ERROR = "500";

  /** The constant ROADTRIP_FILTER_SERVLET. */
  public static final String ROADTRIP_FILTER_SERVLET = "RoadTrip filter servlet";

  /** The constant ROADTRIP_SETTINGS_PATH. */
  public static final String ROADTRIP_SETTINGS_PATH =
      CONFIG_URL + "/admin/jcr:content/roadTrip-settings";

  /** The memory cache key prefix for road trip settings config. */
  public static final String KEY_PREFIX_ROADTRIP_SETTINGS = "roadTrip-settings:";

  /** The v1 version. */
  public static final String VERSION_V1 = "v1";

  /** The v2 version. */
  public static final String VERSION_V2 = "v2";

  /** The constant GREEN_TAXI_RES_TYPE. */
  public static final String GREEN_TAXI_RES_TYPE =
      "sauditourism/components/structure/green-taxi-page";

  /** The constant GREEN_TAXI_CACHE_KEY. */
  public static final String GREEN_TAXI_CACHE_KEY = "greenTaxis-";

  /** The constant GREEN_TAXI_DOWNLOAD_APP_CONFIG_PATH. */
  public static final String GREEN_TAXI_DOWNLOAD_APP_CONFIG_PATH =
      CONFIG_URL + "/admin/jcr:content/greenTaxi-settings";

  /** The Constant EXT_ERROR_CODE. */
  public static final Integer EXT_ERROR_CODE = 80;

  /** Highlight / TopResults Search Category. */
  public static final String HIGHLIGHT_SEARCH_CATEGORY = "Highlight";

  /** FestivalEvents Search Category. */
  public static final String FESTIVALS_EVENTS_SEARCH_CATEGORY = "FestivalsEvents";

  /** PlanYourTrip Search Category. */
  public static final String PLAN_YOUR_TRIP_SEARCH_CATEGORY = "PlanYourTrip";

  /** SeeDo/Attractions Search Category . */
  public static final String SEE_DO_SEARCH_CATEGORY = "SeeDo";

  /** Campaign Search Category. */
  public static final String CAMPAIGN_SEARCH_CATEGORY = "Campaign";

  /** The constant ERROR_MESSAGE_QUESTIONS_ONLY_VISA_DETAILS_ONLY. */
  public static final String ERROR_MESSAGE_QUESTIONS_ONLY_VISA_DETAILS_ONLY =
      "Parameters [visaDetailsOnly] and [questionsOnly] cannot be both [true] at same time";
}
