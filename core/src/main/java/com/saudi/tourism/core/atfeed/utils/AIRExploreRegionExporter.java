package com.saudi.tourism.core.atfeed.utils;



import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.servlets.AbstractATFeedServlet;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

/**
 * The Class CommonUtils.
 */
@Slf4j

public final class AIRExploreRegionExporter extends AbstractATFeedServlet {
/**
 * fOUR is a number .
 */
  private static final int FOUR = 4;
  /**
  * constant of IMAGE_DESKTOP_LINK.
  */
  public static final String IMAGE_DESKTOP_LINK = "imageDesktopLink";
  /**
  * constant of IMAGE_MOBILE_LINK.
  */
  public static final String IMAGE_MOBILE_LINK = "imageMobileLink";
  /**
  * constant of EXPLORE.
  */
  public static final String EXPLORE = "Explore";

/**
    * Instantiates a new CsvExporter.
    */
  private AIRExploreRegionExporter() {
  }
   /**
   * Write CSV.
   * @param response the response
   * @param status   the status.
   * @param object   the jsonArray
   * @param regionsInfo information for region.
   * @param request SlingHTTPServletRequest
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void writeCSV(SlingHttpServletResponse response, int status, Object object,
      Map<String, AppRegion>
      regionsInfo, SlingHttpServletRequest request)throws IOException, JSONException {
    final GsonBuilder topBuilder = CommonUtils.createDefaultGsonBuilder();
    CommonUtils.registerCustomSerializers(topBuilder);
    Gson gson = topBuilder.create();
    citiesRegioncsvfile(response, status, gson.toJson(object), regionsInfo, request);
  }

   /**
   * Writes a CSV via response.
   * @param response   a {@link SlingHttpServletResponse}
   * @param status     a status of the error.
   * @param jsonString  a JSON string should be printed.
   * @param regionsInfo information for region.
   * @param request SlingHTTPServletRequest
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void citiesRegioncsvfile(SlingHttpServletResponse response, int status, final String jsonString,
      Map<String, AppRegion>
      regionsInfo, SlingHttpServletRequest request)throws IOException, JSONException {
    if (status > 0) {
      response.setStatus(status);
    }
    String csv = AIRCsvExporter.getCSVHead();
    try {
      if (StringUtils.isNotBlank(jsonString)) {
        csv = StringUtils.join(csv, ",entity.latitude,entity.longitude,entity.temperature,"
              + "entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url");
        csv = StringUtils.join(csv, System.lineSeparator());

        String cityKey = StringUtils.EMPTY;
        JSONArray array = new JSONArray(jsonString);
        String key = StringUtils.EMPTY;
        for (int i = 0; i < array.length(); i++) {
          JSONObject obj = array.getJSONObject(i);
          if (StringUtils.isNotBlank(obj.optString(AIRConstants.LINK_CITY_URL))) {
            cityKey = obj.getString(AIRConstants.LINK_CITY_URL).split(AIRConstants.SLASH)[FOUR];
          } else if (StringUtils.isNotBlank(obj.optString(AIRConstants.LINKURL))) {
            cityKey = obj.getString(AIRConstants.LINKURL).split(AIRConstants.SLASH)[FOUR];
          }
          csv = StringUtils.join(csv, cityKey + AIRConstants.DEFAULT_SEPARATOR);
          Iterator<String> valueIterator = obj.keys();
          AppRegion appRegion = getRegion(regionsInfo, cityKey);
          csv = StringUtils.join(csv, obj.get(Constants.CONST_NAME).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, EXPLORE + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.DESCRIPTION).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, setHost(obj.get(IMAGE_DESKTOP_LINK).toString(), getHost(request))
                     + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(Constants.CONST_NAME).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.LINKURL).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.ICON).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.CITY_TYPE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, setHost(obj.get(IMAGE_DESKTOP_LINK).toString(), getHost(request))
                        + AIRConstants.DEFAULT_SEPARATOR);
          if (null != appRegion && null != appRegion.getBestFor() && appRegion.getBestFor().length > 0) {
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(Arrays.toString(appRegion.getBestFor())
            .substring(1, Arrays.toString(appRegion.getBestFor()).length() - 1)) + AIRConstants.DEFAULT_SEPARATOR);
            Map<String, List<String>> data = getTagsInterestMapping();
            Set<String> interests = new HashSet<String>();
            for (String tag : appRegion.getBestFor()) {
              for (Entry<String, List<String>> entry : data.entrySet()) {
                if (entry.getValue().contains(tag.trim())) {
                  interests.add(entry.getKey());
                }
              }
            }
            List<String> interestTags = new ArrayList<String>();
            if (interests.size() > 0) {
              interestTags.addAll(interests);
            }
            csv = StringUtils.join(csv, Entity.interestToString(interestTags) + AIRConstants.DEFAULT_SEPARATOR);
          } else {
            csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR
                    + AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR);
          }
          csv = StringUtils.join(csv, obj.get(Constants.LATITUDE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(Constants.LONGITUDE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, setHost(obj.get(IMAGE_MOBILE_LINK).toString(), getHost(request))
                   + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, setHost(obj.get(IMAGE_MOBILE_LINK).toString(), getHost(request))
                   + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, System.lineSeparator());
        }
      }
    } catch (Exception e) {
      csv = e.getMessage();
    }
    OutputStream outputStream = response.getOutputStream();
    response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
    response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.REGION_RESPONSE_HEADER);
    outputStream.write(csv.getBytes(AIRConstants.UTF));
    outputStream.flush();
    outputStream.close();
  }

  /**
  * Returns Region based on Identifier.
  * @param regionsInfo All Regions
  * @param cityKey Region Code
  * @return Matching Region
  */
  private static AppRegion getRegion(Map<String, AppRegion> regionsInfo,
      String cityKey) {
    for (AppRegion appRegion : regionsInfo.values()) {
      if (StringUtils.contains(appRegion.getId(), cityKey.split("-")[0])) {
        return appRegion;
      }
    }
    return null;
  }
}
