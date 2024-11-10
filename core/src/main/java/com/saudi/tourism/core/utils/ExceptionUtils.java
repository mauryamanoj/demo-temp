package com.saudi.tourism.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**
 * This class contains Exception utility methods.
 */
public final class ExceptionUtils {


  /**
   * private constructor because it's a utility class.
   */
  private ExceptionUtils() {

  }


  /**
   * @param statusCode statusCode.
   * @param prefixMessage prefixMessage.
   * @return status message.
   */
  public static String getMessageUsers(final String statusCode, final String prefixMessage) {

    switch (statusCode) {
      case StatusConstants.BAD_REQUEST_ERROR:
        return prefixMessage + " Bad request";
      case StatusConstants.UNAUTHORIZED_ERROR:
        return prefixMessage + " Invalid token state";
      case StatusConstants.INTERNAL_SERVER_ERROR:
        return prefixMessage + " Technical Error";
      default:
        return prefixMessage + " Generic Error";
    }
  }

  /**
   * @param e exception.
   * @return status code.
   */
  public static String getStatusUsers(Exception e) {
    try {
      if (StringUtils.isNotBlank(e.getMessage())) {
        JsonParser parser = new JsonParser();
        JsonObject errorObj = parser.parse(e.getMessage()).getAsJsonObject();
        // SSID format
        if (null != errorObj && null != errorObj.get("error")) {
          return String.valueOf(errorObj.get("error").getAsInt());
        }
        // Middleware format
        if (null != errorObj && null != errorObj.get("status")) {
          return String.valueOf(errorObj.get("status").getAsInt());
        }
      }
    } catch (Exception ex) {
      return Constants.INTERNAL_SERVER_ERROR;
    }
    return Constants.INTERNAL_SERVER_ERROR;
  }
}

