package com.saudi.tourism.core.atfeed.servlets;

import static com.saudi.tourism.core.utils.Constants.HTTPS_SCHEME;
// TODO Implement Abstract Servlet for feeds

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;

import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.Constants;

/**
 * Abstract Feed servlet.
 */
@Slf4j
public class AbstractATFeedServlet extends SlingAllMethodsServlet {


  /**
   * Format the entity data.
   * @param field entity in string
   * @param quote quotation for fields
   * @return Formatting version
   */
  private String formatCsvField(final String field, final boolean quote) {

    String result = field;

    if (result.contains(AIRConstants.COMMA)
        || result.contains(AIRConstants.DOUBLE_QUOTES)
        || result.contains(AIRConstants.NEW_LINE_UNIX)
        || result.contains(AIRConstants.NEW_LINE_WINDOWS)) {

      // if field contains double quotes, replace it with two double quotes \"\"
      //result = result.replace(COMMA, EMBEDDED_DOUBLE_QUOTES);

      // must wrap by or enclosed with double quotes
      //result = DOUBLE_QUOTES + result + DOUBLE_QUOTES;
      return result;

    } else {
      // should all fields enclosed in double quotes
      if (quote) {
        result = AIRConstants.DOUBLE_QUOTES + result + AIRConstants.DOUBLE_QUOTES;
      }
    }

    return result;

  }

  /**
   * Get host to add for file reference.
   * @param request SlingHTTPServletRequest
   * @return Protocol and Host of request
   */
  @NotNull
public static String getHost(SlingHttpServletRequest request) {
    StringBuffer url = request.getRequestURL();
    String uri = request.getRequestURI();
    String ctx = request.getContextPath();
    String base = url.substring(0, url.length() - uri.length() + ctx.length());
    if (base.indexOf(Constants.HTTPS_SCHEME) == -1) {
      base = base.replaceFirst("http", Constants.HTTPS_SCHEME);
    }
    return base;
  }


  /**
   *  Search card rental folder.
   * @param locale Locale
   * @return Return full nested folder path
   */
  @NotNull
  String getHomeLocation(String locale) {
    return Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale
      + Constants.FORWARD_SLASH_CHARACTER;
  }

  /**
   *  Locale from Request.
   * @param request SlingHttpServletRequest
   * @return Return locale
   */
  @NotNull
  String getLocale(SlingHttpServletRequest request) {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)) {
      locale = request.getRequestParameter(Constants.LOCALE).toString();
    }
    return locale;
  }

  /**
   * Search discover regions folder.
   *
   * @param locale       Local
   * @param nestedFolder NestedFolder
   * @return Return full nested folder path
   */
  @NotNull
  public String getSearchLocation(String locale, String nestedFolder) {
    return Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale + Constants.FORWARD_SLASH_CHARACTER
        + nestedFolder;
  }

  /**
   * Set host if missing.
   *
   * @param attribute Attribute name
   * @param host      Host
   * @return Attribute with host
   */
  @NotNull
public static String setHost(String attribute, String host) {
    if (attribute.startsWith(HTTPS_SCHEME)) {
      return attribute;
    }
    return host + attribute;
  }

  /**
   * Get output stream for feeds.
   * @param response SlingResponse
   * @param csv CSV file
   * @throws IOException IOException
   */
  protected void getOutputStream(SlingHttpServletResponse response, String csv) throws IOException {
    OutputStream outputStream = response.getOutputStream();
    response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
    response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.FEEDS_RESPONSE_HEADER);
    outputStream.write(csv.getBytes("UTF-8"));
    outputStream.flush();
    outputStream.close();
  }


  /**
   *  Mapping between tags and user interests.
   * @return Mapping
   */
  protected static Map<String, List<String>> getTagsInterestMapping() {
    Map<String, List<String>> data = new HashMap<String, List<String>>();
    data.put("int-sunnsea", Arrays.asList("Beach", "Diving", "Sea", "Fishing", "Water Activities"));
    data.put("int-adventure", Arrays.asList("Camping", "Exploring", "Hike", "Mountains", "Nature", "Sand Skiing",
        "Swimming"));
    data.put("int-wellness", Arrays.asList("Relaxing", "Honeymoon"));
    data.put("int-arts", Arrays.asList("Art", "Culture", "Heritage", "History", "Themed attractions"));
    data.put("int-fandd", Arrays.asList("Food"));
    data.put("int-entertain", Arrays.asList("Art", "Private Jet", "Sand Skiing", "Water Activities"));
    data.put("int-sports", Arrays.asList("Diving", "Sand Skiing", "Water Activities"));
    data.put("int-cruise", Arrays.asList("Sea"));
    data.put("int-shopping", Arrays.asList("Urban", "Shopping"));
    return data;
  }

}
