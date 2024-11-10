package com.saudi.tourism.core.utils;

import javax.servlet.http.Cookie;

/**
 * Cookie Helper class.
 */
public final class CookieHelper {

  /**
   * domain.
   */
  public static final String DOMAIN = "Domain";

  /**
   * path.
   */
  public static final String PATH = "Path";

  /**
   * sameSite.
   */
  public static final String SAME_SITE = "SameSite";

  /**
   * Secure attr.
   */
  public static final String SECURE = "Secure";

  /**
   * Http only attr.
   */
  public static final String HTTP_ONLY = "HttpOnly";

  /**
   * max age attr.
   */
  public static final String MAX_AGE = "Max-Age";

  /**
   * comment attr.
   */
  public static final String COMMENT = "Comment";

  /**
   * set cookie constant.
   */
  public static final String SET_COOKIE = "Set-Cookie";

  /**
   * Seperator.
   */
  public static final String SEPERATOR = "; ";

  /**
   * assign constant.
   */
  public static final String ASSIGN = "=";

  //hide default constructor

  /**
   * constructor.
   */
  private CookieHelper() { }

  /**
   * creats the cookie header.
   *
   * @param cookieName name.
   * @param cookieValue value.
   * @param domain domain.
   * @param path path.
   * @param sameSite attr.
   * @param isSecure attr.
   * @param maxAge attr.
   * @return cookie.
   */
  public static String createSetCookieHeader(String cookieName, String cookieValue, String domain,
                                             String path, SameSite sameSite, boolean isSecure,
                                             Integer maxAge) {

    StringBuilder cookieString = new StringBuilder();

    if (cookieName == null || cookieName.trim().isEmpty() || cookieValue == null || cookieValue.trim().isEmpty()) {
      throw new IllegalArgumentException("Cookie name or value can not be empty (" + cookieName + "=" + cookieValue);
    }
    cookieString.append(cookieName).append(ASSIGN).append(cookieValue).append(SEPERATOR);

    if (path != null && !path.trim().isEmpty()) {
      cookieString.append(PATH).append(ASSIGN).append(path).append(SEPERATOR);
    }

    if (domain != null && !domain.trim().isEmpty()) {
      cookieString.append(DOMAIN).append(ASSIGN).append(domain).append(SEPERATOR);
    }

    if (sameSite != null) {
      cookieString.append(SAME_SITE).append(ASSIGN).append(sameSite.getValue()).append(SEPERATOR);
    }

    if (isSecure) {
      cookieString.append(SECURE + SEPERATOR);
    }
    if (maxAge != null) {
      cookieString.append(MAX_AGE).append(ASSIGN).append(maxAge).append(SEPERATOR);
    }
    return cookieString.toString().trim();
  }

  /**
   * Creates a string with key value pairs used in a HttpCookie with ; delimiter.
   *
   * @param cookie       to use. Must not be null.
   * @param sameSite     attribute of the cookie. Can be null. In this case browsers treat it as Lax.
   * @return a string with key value pairs used in a HttpCookie with ; delimiter
   */
  public static String createSetCookieHeader(Cookie cookie, SameSite sameSite) {

    if (cookie == null) {
      throw new IllegalArgumentException("The cookie parameter must not be null!");
    }

    return createSetCookieHeader(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
          sameSite, cookie.getSecure(), cookie.getMaxAge());
  }
}
