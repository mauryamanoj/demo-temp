package com.saudi.tourism.core.utils;

import com.day.crx.JcrConstants;

/**
 * This is used to define all String constants for AI&R.
 */
public final class AIRConstants {
  /**
   * Default constructor.
   */
  private AIRConstants() {
  }

  /**
   * CSV Content Type for output in servlet.
   */
  public static final String CSV_CONTENT_TYPE = "text/csv";

  /**
   * CONSTANT OF UTF.
   */
  public static final String UTF = "UTF-8";
  /**
   * Download attachment for output in servlet.
   */
  public static final String DOWNLOAD_RESPONSE_HEADER = "Content-Disposition";

  /**
   * CSV File Name for output in servlet.
   */
  public static final String FEEDS_RESPONSE_HEADER = "attachment; filename=\"feeds.csv\"";

  /**
   * CSV File Name for output in REGION_CITIES_RESPONSE_HEADER.
   */
  public static final String REGION_CITIES_RESPONSE_HEADER = "attachment; filename=\"regionandcities.csv\"";
  /**
   * CSV File Name for output in REGION_RESPONSE_HEADER.
   */
  public static final String REGION_RESPONSE_HEADER = "attachment; filename=\"exploreRegion.csv\"";
  /**
   * CSV File Name for output in servlet.
   */
  public static final String PACKAGE_RESPONSE_HEADER = "attachment; filename=\"package.csv\"";

  /**
   * The constant EXPERIENCES_PACKAGE_SERVLET.
   */
  public static final String EXPERIENCES_PACKAGE_SERVLET = "Experiences Package servlet";

  /**
   * The constant CARDS_TEASER_TYPE.
   */
  public static final String CARDS_TEASER_TYPE =
      "sauditourism/components/content/c27-teaser-with-cards/v1/c27-teaser-with-cards";

  /**
   * The constant BIG_SLIDER.
   */
  public static final String BIG_SLIDER =
      "sauditourism/components/content/c25-big-slider/v1/c25-big-slider";

  /**
   * Path to dam/sauditourism/Json.
   */
  public static final String DAM_SAUDITOURISM_PATH_JSON = "/content/dam/sauditourism/"
                  + "interests/categories-interests.json";
  /**
  * Path to dam/sauditourism.
  */
  public static final String SAUDITOURISM_CITIES_PATH = "/content/sauditourism/%s/see-do/destinations/"
         + JcrConstants.JCR_CONTENT + "/root/responsivegrid/c28_destinations_and/cards";
  /**
   * constant of CITY_ID.
   */
  public static final String CITY_ID = "cityId";
  /**
   * constant of CITIES.
   */
  public static final String CITIES = "Cities";

  /** LOCALE to check. */
  public static final String EN = "en";
  /** REGION to check. */
  public static final String REGION = "region";
  /** CONTENT to check. */
  public static final String CONTENT_SAUDITOURISM = "/content/sauditourism";
  /** NAVIGATION_TITLE to check. */
  public static final String NAVIGATION_TITLE = "navigationTitle";
  /**
   * constant of city.
   */
  public static final String CITY = "city";
  /** DESTINATION_FEATURE_TAG to check. */
  public static final String DESTINATION_FEATURE_TAG = "destinationFeatureTags";
  /**
   * constant of null.
   */
  public static final String NULL = "null";
  /**
   * constant of description.
   */
  public static final String DESCRIPTION = "description";
  /**
  * constant of ctaType.
  */
  public static final String CTA_TYPE = "ctaType";
  /**
  * constant of hideFav.
  */
  public static final String HIDE_FAV = "hideFav";
  /**
  * constant of highlight.
  */
  public static final String HIGH_LIGHT = "highlight";
  /**
  * constant of icon.
  */
  public static final String ICON = "icon";
  /**
  * constant of IMAGE.
  */
  public static final String IMAGE = "image";
  /**
  * constant of icon.
  */
  public static final String TITLE = "title";
  /**
  * constant of imagePosition.
  */
  public static final String IMAGE_POSITION = "imagePosition";
  /**
  * constant of LINK_URL.
  */
  public static final String LINK_URL = "link/url";
  /**
  * constant of LINKURL.
  */
  public static final String LINKURL = "linkUrl";
  /**
  * constant of DESCRIPTIONS.
  */
  public static final String DESCRIPTIONS = "descriptions";
  /**
  * constant of URL.
  */
  public static final String URL = "url";
  /**
  * constant of link.
  */
  public static final String LINK = "link";
  /**
   * constant of regionId.
   */
  public static final String REGION_ID = "regionId";
  /**
  * constant of image/fileReference.
  */
  public static final String IMAGE_FILE_REFERENCE = "image/fileReference";
  /**
  * constant of fileReference.
  */
  public static final String FILE_REFERENCE = "fileReference";
  /**
  * constant of s7fileReference.
  */
  public static final String S7FILE_REFERENCE = "s7fileReference";
  /**
  * constant of image/mobileImageReference.
  */
  public static final String IMAGE_MOBILE_REFERENCE = "image/mobileImageReference";
  /**
  * constant of featuredTagLineFeaturedCopy.
  */
  public static final String FEATURED_IMAGE = "featuredTagLineFeaturedCopy";
  /**
  * constant of link/copy.
  */
  public static final String COPY_LINK = "link/copy";
  /**
  * constant of copy.
  */
  public static final String COPY = "copy";
  /**
  * constant of HYPHEN.
  */
  public static final String DASH = "-";
  /**
  * constant of UNDERSCORE.
  */
  public static final String UNDERSCORE = "_";
  /**
  * constant of SLASH.
  */
  public static final String SLASH = "/";
  /**
  * constant of NEW_LINE.
  */
  public static final String NEW_LINE = "\\r|\\n";
  /**
  * constant of LEFT_SQUARE_BRACKET.
  */
  public static final String LEFT_SQUARE_BRACKET = "[";
  /**
  * constant of RIGHT_SQUARE_BRACKET.
  */
  public static final String RIGHT_SQUARE_BRACKET = "]";
  /**
  * constant of OPEN_P_TAG.
  */
  public static final String OPEN_P_TAG = "<p>";
  /**
  * constant of CSV.
  */
  public static final String CSV = "csv";
  /**
  * constant of CLOSE_P_TAG.
  */
  public static final String CLOSE_P_TAG = "</p>";
  /**
  * constant of SPECIAL_CHAR.
  */
  public static final String SPECIAL_CHAR = "^\"|\"$";
  /**
  * constant of WHITE_SPACE.
  */
  public static final String WHITE_SPACE = "\\s+";
  /**
  * constant of LEBELS.
  */
  public static final String LEBELS = "labels";
  /** JCR_SQL to check. */
  public static final String JCR_SQL = "JCR-SQL2";
  /** constant of Comma. */
  public static final String COMMA = ",";
  /** constant of DEFAULT SEPARATOR. */
  public static final String DEFAULT_SEPARATOR = COMMA;
  /** constant of QUOTES. */
  public static final String DOUBLE_QUOTES = "\"";
  /** constant of EMBEDDED QUOTES. */
  public static final String EMBEDDED_DOUBLE_QUOTES = "\"\"";
  /** constant of New line Unix. */
  public static final String NEW_LINE_UNIX = "\n";
  /** constant of New line Windows. */
  public static final String NEW_LINE_WINDOWS = "\r\n";
  /**
  * constant of isFeatured.
  */
  public static final String ISFEATURED = "isFeatured";

  /**
   * DESTINATIONS Search Category.
   */
  public static final String DESTINATIONS = "destinations";

  /**
   * DESTINATIONS_TAG Search Category.
   */
  public static final String DESTINATIONS_TAG = "destinations/";

  /**
   * DESTINATIONS_DASH Search Category.
   */
  public static final String DESTINATIONS_DASH = "destinations-";

  /**
   * SAUDITOURISM_TAG Search Category.
   */
  public static final String SAUDITOURISM_TAG = "sauditourism/";

  /**
   * SAUDITOURISM Search Category.
   */
  public static final String SAUDITOURISM = "sauditourism";
  /**
   * CARD_ITEMS Search Category.
   */
  public static final String CARD_ITEMS = "card-items";
  /**
  * constant of type.
  */
  public static final String CITY_TYPE = "type";
  /**
  * constant of ENTITY.
  */
  public static final String ENTITY = "entity.";

  /**
  * constant of LINK_CITY_URL.
  */
  public static final String LINK_CITY_URL = "linkCityUrl";

  /**
  * constant of type.
  */
  public static final String TYPE = "@type";
  /**
  * constant of SPACE_WITH_COLON.
  */
  public static final String SPACE_WITH_COLON = "{\"%s\":%s}";
  /**
   * The constant CARDS_TEASER.
   */
  public static final String CARDS_TEASER =
      "sauditourism/components/content/c27-teaser-with-cards/v1";
  /**
  * constant of useActivityLink.
  */
  public static final String USE_ACTIVITY_LINK = "useActivityLink";

  /**
  * Path to dam/SAUDITOURISM_REGION_PATH.
  */
  public static final String SAUDITOURISM_REGION_PATH = "/content/sauditourism/%s/see-do/destinations/"
         + "jcr:content/root/responsivegrid/c27_teaser_with_card/cards";

  /**
   * Path to city components.
   */
  public static final String REGION_PATH_FORMAT = Constants.ROOT_APP_CONTENT_PATH + "/%s/regions";
}
