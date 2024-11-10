package com.saudi.tourism.core.utils;

/**
 * Keywords to be used in Profile/Login/Account.
 */
public final class ProfileConstants {

  /**
   * Connection Provider :: Twitter.
   */
  public static final String CP_TWITTER = "twitter";

  /**
   * Connection Provider :: Apple.
   */
  public static final String CP_APPLE = "apple";

  /**
   * Login User Metadata keyword.
   */
  public static final String USER_METADATA_KEY = "user_metadata";

  /**
   * Login  picture keyword.
   */
  public static final String PICTURE_KEY = "picture";
  /**
   * The constant ITEMS.
   */
  public static final String ITEMS = "items";

  /**
   * The constant PROFILE.
   */
  public static final String TRIP_COUNT = "tripCount";

  /**
   * The constant PROFILE.
   */
  public static final String FAVORITES_COUNT = "favoritesCount";

  /**
   * UNSUPPORTED TYPE error message.
   */
  public static final String UNSUPPORTED_TYPE_MESSAGE =
      "{\"error\":\"Only supported types: profile, favorites, trips\"}";

  /**
   * Login User Metadata keyword.
   */
  public static final String DEFAULT_FIRST_NAME_KEY = "given_name";

  /**
   * DEFAULT_LAST_NAME_KEY keyword.
   */
  public static final String DEFAULT_LAST_NAME_KEY = "family_name";

  /**
   * DEFAULT_EMAIL_KEY keyword.
   */
  public static final String DEFAULT_EMAIL_KEY = "email";

  /**
   * Metadata property FIRST_NAME keyword.
   */
  public static final String PN_FIRST_NAME = "firstName";

  /**
   * Metadata property LAST_NAME keyword.
   */
  public static final String PN_LAST_NAME = "lastName";

  /**
   * Default Constructor.
   */
  private ProfileConstants() {
    // do nothing
  }
}
