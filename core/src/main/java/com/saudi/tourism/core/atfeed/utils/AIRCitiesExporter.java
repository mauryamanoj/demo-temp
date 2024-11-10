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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

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
import com.saudi.tourism.core.models.app.location.AppCity;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

/**
 * The Class CommonUtils.
 */
@Slf4j

public final class AIRCitiesExporter extends AbstractATFeedServlet {
/**
 * fOUR is a number .
 */
  private static final int FOUR = 4;
  /** Static category of HIKE. */
  public static final String HIKE = "Take a Hike in Aseer";
  /** Static category of description. */
  public static final String TRIPDETAILS = "jcr:content/jcr:description";

  /** Static category of featureImage. */
  public static final String TRIPTHUMBNAILURL = "jcr:content/featureImage";

  /** Static category of S7featureImage. */
  public static final String TRIPTHUMBNAILS7URL = "jcr:content/s7featureImage";
/**
    * Instantiates a new CsvExporter.
    */
  private AIRCitiesExporter() {
  }
   /**
   * Write CSV.
   * @param response the response
   * @param status   the status.
   * @param citiesInfo cities information.
   * @param object   the jsonArray
   * @param request SlingHTTPServletRequest
   * @param discoverRegionEntities discoverRegionEntities.
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void writeCSV(SlingHttpServletResponse response, int status, Object object,
       Map<String, AppCity> citiesInfo, SlingHttpServletRequest request, Map<String, Entity> discoverRegionEntities)
        throws IOException, JSONException {
    final GsonBuilder topBuilder = CommonUtils.createDefaultGsonBuilder();
    CommonUtils.registerCustomSerializers(topBuilder);
    Gson gson = topBuilder.create();
    citiesRegioncsvfile(response, status, gson.toJson(object), citiesInfo, request, discoverRegionEntities);
  }

   /**
   * Writes a CSV via response.
   * @param response   a {@link SlingHttpServletResponse}
   * @param status     a status of the error.
   * @param jsonString  a JSON string should be printed.
   * @param citiesInfo cities information.
   * @param request SlingHTTPServletRequest
   * @param discoverRegionEntities discoverRegionEntities.
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void citiesRegioncsvfile(SlingHttpServletResponse response, int status, final String jsonString,
      Map<String, AppCity> citiesInfo, SlingHttpServletRequest request, Map<String, Entity> discoverRegionEntities)
      throws IOException, JSONException {
    if (status > 0) {
      response.setStatus(status);
    }
    String csv = AIRCsvExporter.getCSVHead();
    try {
      if (StringUtils.isNotBlank(jsonString)) {
        csv = StringUtils.join(csv, ",entity.latitude,entity.longitude,entity.temperature,"
              + "entity.tripDetails,entity.tripThumbnailUrl,entity.tripThumbnailS7Url,"
              + "entity.summary,entity.title,entity.discoverThumbnailUrl,entity.discoverS7ThumbnailUrl");
        csv = StringUtils.join(csv, System.lineSeparator());

        String cityKey = StringUtils.EMPTY;
        JSONArray array = new JSONArray(jsonString);
        String key = StringUtils.EMPTY;
        Set<String> citiesAdded = new HashSet<String>();
        for (int i = 0; i < array.length(); i++) {
          JSONObject obj = array.getJSONObject(i);
          if (StringUtils.isNotBlank(obj.optString(AIRConstants.LINK_CITY_URL))) {
            cityKey = obj.getString(AIRConstants.LINK_CITY_URL).split(AIRConstants.SLASH)[FOUR];
          } else if (StringUtils.isNotBlank(obj.optString(AIRConstants.LINKURL))) {
            cityKey = obj.getString(AIRConstants.LINKURL).split(AIRConstants.SLASH)[FOUR];
          }
          csv = StringUtils.join(csv, cityKey + AIRConstants.DEFAULT_SEPARATOR); //entity.id
          Iterator<String> valueIterator = obj.keys();
          AppCity appCity = citiesInfo.get(cityKey);
          String name = StringUtils.EMPTY;
          name = obj.get(Constants.CONST_NAME).toString();
          csv = StringUtils.join(csv, name + AIRConstants.DEFAULT_SEPARATOR); //entity.name
          csv = StringUtils.join(csv, AIRConstants.CITIES + AIRConstants.DEFAULT_SEPARATOR); //entity.categoryId
          csv = StringUtils.join(csv, obj.get(AIRConstants.DESCRIPTION).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = getImageDetails(csv, obj, AIRConstants.FILE_REFERENCE, request); //entity.thumbnailUrl
          csv = StringUtils.join(csv, obj.get(AIRConstants.NAVIGATION_TITLE).toString()
                  + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.LINK_CITY_URL).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(AIRConstants.ICON).toString()
                    + AIRConstants.DEFAULT_SEPARATOR); //entity.inventory
          csv = StringUtils.join(csv, obj.get(AIRConstants.CITY_TYPE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = getImageDetails(csv, obj, AIRConstants.S7FILE_REFERENCE, request); //entity.thumbS7Url
          if (null != appCity && null != appCity.getBestFor() && appCity.getBestFor().length > 0) {
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(Arrays.toString(appCity.getBestFor()).substring(1,
            Arrays.toString(appCity.getBestFor()).length() - 1)) + AIRConstants.DEFAULT_SEPARATOR); //entity.tags
            Map<String, List<String>> data = getTagsInterestMapping();
            Set<String> interests = new HashSet<String>();
            for (String tag : appCity.getBestFor()) {
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
            csv = StringUtils.join(csv, Entity.interestToString(interestTags)
                     + AIRConstants.DEFAULT_SEPARATOR); //entity.interests
          } else if (obj.optString(AIRConstants.DESTINATION_FEATURE_TAG) != null) {
            JSONArray tagsArray = new JSONArray(obj.get(AIRConstants.DESTINATION_FEATURE_TAG).toString());
            String tags = StringUtils.EMPTY;
            for (int k = 0; k < tagsArray.length(); k++) {
              JSONObject destinationTags = tagsArray.getJSONObject(k);
              tags = tags + destinationTags.getString(AIRConstants.COPY) + AIRConstants.DEFAULT_SEPARATOR
                       + Constants.SPACE;
            }
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(tags.substring(0,
                 tags.length() - 2)) + AIRConstants.DEFAULT_SEPARATOR); //entity.tags
            Map<String, List<String>> data = getTagsInterestMapping();
            Set<String> interests = new HashSet<String>();
            for (Entry<String, List<String>> entry : data.entrySet()) {
              String[] tagsValue = tags.split(AIRConstants.DEFAULT_SEPARATOR + Constants.SPACE);
              for (String tag: tagsValue) {
                if (entry.getValue().contains(tag)) {
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
            csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.NULL
                  + AIRConstants.DEFAULT_SEPARATOR); //entity.tags,entity.interests
          }
          csv = StringUtils.join(csv, obj.get(Constants.LATITUDE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, obj.get(Constants.LONGITUDE).toString() + AIRConstants.DEFAULT_SEPARATOR);
          csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR); //entity.temperature
          String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
          Session session = request.getResourceResolver().adaptTo(Session.class);
          if (session.nodeExists(sourcePath)) {
            Node parentNode = session.getNode(sourcePath);
            if (parentNode.hasNodes()) {
              NodeIterator items = parentNode.getNodes();
              while (items.hasNext()) {
                Node item = items.nextNode();
                if (cityKey.equalsIgnoreCase(item.getName())) {
                  if (item.hasProperty(TRIPDETAILS)) {
                    String tripSummary = item.getProperty(TRIPDETAILS).getString().strip();
                    csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(tripSummary
                            .replaceAll(AIRConstants.NEW_LINE_WINDOWS, StringUtils.EMPTY))
                             + AIRConstants.DEFAULT_SEPARATOR); //entity.tripDetails
                  } else {
                    csv = StringUtils.join(csv, Constants.SPACE + AIRConstants.DEFAULT_SEPARATOR);
                  }
                  csv = StringUtils.join(csv, item.getProperty(TRIPTHUMBNAILURL)
                     .getString() + AIRConstants.DEFAULT_SEPARATOR); //entity.tripThumbnailUrl
                  csv = StringUtils.join(csv, item.getProperty(TRIPTHUMBNAILS7URL)
                     .getString() + AIRConstants.DEFAULT_SEPARATOR); //entity.tripThumbnailS7Url
                }
              }
            }
          }
          Entity entity =  discoverRegionEntities.get(cityKey);
          if (null != entity) {
            String details = entity.getMessage().strip();
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(details
                .replaceAll(AIRConstants.NEW_LINE_WINDOWS, StringUtils.EMPTY)) + AIRConstants.DEFAULT_SEPARATOR);
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(entity.getName().strip())
                      + AIRConstants.DEFAULT_SEPARATOR);
            csv = StringUtils.join(csv, entity.getThumbnailUrl() + AIRConstants.DEFAULT_SEPARATOR);
            csv = StringUtils.join(csv, entity.getThumbnailS7Url());
          }
          csv = StringUtils.join(csv, System.lineSeparator());
          citiesAdded.add(cityKey);
        }

        csv = populateDiscoverEntities(discoverRegionEntities, csv, citiesAdded);

      }
    } catch (Exception e) {
      csv = e.getMessage();
    }
    OutputStream outputStream = response.getOutputStream();
    response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
    response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.REGION_CITIES_RESPONSE_HEADER);
    outputStream.write(csv.getBytes(AIRConstants.UTF));
    outputStream.flush();
    outputStream.close();
  }

  /**
   * Writes a CSV via response.
   * @param discoverRegionEntities discoverRegionEntities.
   * @param csv get data.
   * @param citiesAdded cities information.
   * @return csv with exact data.
   */
  public static String populateDiscoverEntities(Map<String, Entity> discoverRegionEntities, String csv,
       Set<String> citiesAdded) {
    for (String discoverRegion : discoverRegionEntities.keySet()) {
      if (!citiesAdded.contains(discoverRegion)) {
        Entity entity = discoverRegionEntities.get(discoverRegion);
        csv = StringUtils.join(csv, entity.getId() + AIRConstants.DEFAULT_SEPARATOR);
        String name = entity.getId();
        String s1 = name.substring(0, 1).toUpperCase();
        String s2 = name.substring(1);
        String result = s1 + s2;
        csv = StringUtils.join(csv, result + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, AIRConstants.CITIES + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.NULL
              + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, entity.getThumbnailUrl() + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, result + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, entity.getCtaURL().replaceAll(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY)
                  + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.NULL
              + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, entity.getThumbnailS7Url() + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.NULL
               + AIRConstants.DEFAULT_SEPARATOR + StringUtils.EMPTY + AIRConstants.DEFAULT_SEPARATOR
               + StringUtils.EMPTY + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.NULL
               + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.DEFAULT_SEPARATOR
               + AIRConstants.DEFAULT_SEPARATOR + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(entity.getMessage().strip()))
                  + AIRConstants.DEFAULT_SEPARATOR;
        csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(entity.getName().strip())
                   + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, entity.getThumbnailUrl() + AIRConstants.DEFAULT_SEPARATOR);
        csv = StringUtils.join(csv, entity.getThumbnailS7Url());
        csv = StringUtils.join(csv, System.lineSeparator());
      }
    }
    return csv;
  }
  /**
   * @param csv  via response.
   * @param obj for edit
   * @param key for image
   * @param request SlingHTTPServletRequest
   * @return tag from bestfor
   * @throws JSONException Signals that an I/O exception has occurred.
   */
  private static String getImageDetails(String csv, JSONObject obj, String key, SlingHttpServletRequest request)
      throws JSONException {
    JSONObject imgObj = obj.getJSONObject(AIRConstants.IMAGE);
    csv = StringUtils.join(csv, setHost(imgObj.get(key).toString(), getHost(request)));
    csv = StringUtils.join(csv, AIRConstants.DEFAULT_SEPARATOR);
    return csv;
  }

}
