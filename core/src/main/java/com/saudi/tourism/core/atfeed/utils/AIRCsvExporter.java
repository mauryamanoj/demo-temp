package com.saudi.tourism.core.atfeed.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

/**
 * The Class CommonUtils.
 */
@Slf4j
public final class AIRCsvExporter {
  /**
  * constant of CATEGORY_SLUG.
  */
  public static final String CATEGORY_SLUG = "category_slug";
  /**
  * constant of CATEGORY_NAME.
  */
  public static final String CATEGORY_NAME = "category_name";
  /**
  * constant of DISCOUNTED_PRICE.
  */
  public static final String DISCOUNTED_PRICE = "discounted_price";
  /**
  * constant of BACKGROUND_IMAGE.
  */
  public static final String BACKGROUND_IMAGE = "background_image";
  /**
  * constant of SUB_CATEGORY.
  */
  public static final String SUB_CATEGORY = "sub_category";
  /**
  * constant of IS_DMC.
  */
  public static final String IS_DMC = "is_dmc";
  /**
  * constant of SECONDARY_DESTINATIONS.
  */
  public static final String SECONDARY_DESTINATIONS = "secondary_destinations";
  /**
  * constant of DURATION.
  */
  public static final String DURATION = "duration";
  /**
  * constant of WAIVER_REQUIRED_INPUT.
  */
  public static final String WAIVER_REQUIRED_INPUT = "waivers_required_input";
  /**
  * constant of SLUG.
  */
  public static final String SLUG = "slug";
  /**
  * constant of MIN_CAPACITY.
  */
  public static final String MIN_CAPACITY = "min_capacity";
  /**
  * constant of BOOK_ONLINE.
  */
  public static final String BOOK_ONLINE = "book_online";
  /**
  * constant of BACKGROUND_IMAGES.
  */
  public static final String BACKGROUND_IMAGES = "background_images";
  /**
  * constant of MAX_CAPACITY.
  */
  public static final String MAX_CAPACITY = "max_capacity";
  /**
  * constant of WAIVERS_VIEW.
  */
  public static final String WAIVERS_VIEW = "waivers_view";
  /**
  * constant of HAS_WAIVERS_BOOKING.
  */
  public static final String HAS_WAIVERS_BOOKING = "has_waivers_booking";
  /**
  * constant of LIST_PRICE.
  */
  public static final String LIST_PRICE = "list_price";
  /**
  * constant of DURATION_UNIT.
  */
  public static final String DURATION_UNIT = "duration_unit";
  /**
  * constant of SECONDARY_DESTINATION.
  */
  public static final String SECONDARY_DESTINATION = "secondary_destination";
  /**
  * constant of PRICE_TYPE.
  */
  public static final String PRICE_TYPE = "price_type";
  /**
  * constant of CITY_ID.
  */
  public static final String CITY_ID = "city_id";
  /**
  * constant of CONTACT_NAME.
  */
  public static final String CONTACT_NAME = "contact_name";
  /**
  * constant of ID.
  */
  public static final String ID = "id";
  /**
   * constant of REC_ENTITY_ID.
   */
  public static final String REC_ENTITY_ID = "## RECSentity.id";
  /**
  * constant of DATA.
  */
  public static final String DATA = "data";
    /**
    * Instantiates a new CsvExporter.
    */
  private AIRCsvExporter() {
  }
   /**
   * Write CSV.
   * @param response the response
   * @param status   the status
   * @param object   the jsonArray.
   * @param resolverFactory Reading Resolver Factory.
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void writeCSV(SlingHttpServletResponse response, int status, Object object,
      ResourceResolverFactory resolverFactory)throws IOException, JSONException {
    final GsonBuilder topBuilder = CommonUtils.createDefaultGsonBuilder();
    CommonUtils.registerCustomSerializers(topBuilder);
    Gson gson = topBuilder.create();
    writeCSVFile(response, status, gson.toJson(object), resolverFactory);
  }

   /**
   * Writes a CSV via response.
   * @param response   a {@link SlingHttpServletResponse}
   * @param status     a status of the error.
   * @param jsonString  a JSON string should be printed.
   * @param resolverFactory Reading Resolver Factory.
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JSONException Signals that an JSON exception has occurred.
   */
  public static void writeCSVFile(SlingHttpServletResponse response, int status,
      final String jsonString, ResourceResolverFactory resolverFactory) throws IOException, JSONException {
    if (status > 0) {
      response.setStatus(status);
    }
    JSONObject jsonObj = new JSONObject(jsonString);
    String csv = getCSVHead();
    if (jsonObj.has(DATA)) {
      JSONArray array = jsonObj.getJSONArray(DATA);
      Iterator it2 = array.getJSONObject(0).keys();
      String[] keysToBeSkipped = {CATEGORY_SLUG, CATEGORY_NAME, DISCOUNTED_PRICE,
          BACKGROUND_IMAGE, ID, SUB_CATEGORY, IS_DMC, SECONDARY_DESTINATIONS,
          DURATION, WAIVER_REQUIRED_INPUT, SLUG, MIN_CAPACITY, BOOK_ONLINE,
          BACKGROUND_IMAGES, MAX_CAPACITY, WAIVERS_VIEW, HAS_WAIVERS_BOOKING,
          LIST_PRICE, DURATION_UNIT, SECONDARY_DESTINATION, PRICE_TYPE, CITY_ID,
          CONTACT_NAME};
      String csvHeader = csv.split(REC_ENTITY_ID)[1];
      while (it2.hasNext()) {
        String newColName = it2.next().toString();
        if (!csvHeader.contains(AIRConstants.DEFAULT_SEPARATOR + AIRConstants.ENTITY + newColName
            + AIRConstants.DEFAULT_SEPARATOR) && !Arrays.asList(keysToBeSkipped).contains(newColName)) {
          csv = StringUtils.join(csv, AIRConstants.DEFAULT_SEPARATOR + AIRConstants.ENTITY);
          csv = StringUtils.join(csv, newColName);
        }
      }
      csv = StringUtils.join(csv, System.lineSeparator());
      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
//        csv = StringUtils.join(csv, ComponentUtils.generateId(AIRConstants.CARD_ITEMS,
//                  obj.getString(Constants.CONST_NAME)) + AIRConstants.DEFAULT_SEPARATOR); //id
        csv = StringUtils.join(csv, obj.getString(ID) + AIRConstants.DEFAULT_SEPARATOR); //id
        csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(obj.getString(Constants.CONST_NAME))
                  + AIRConstants.DEFAULT_SEPARATOR); //name
        csv = StringUtils.join(csv, Constants.PACKAGES + AIRConstants.DEFAULT_SEPARATOR); //category
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR); //message
        csv = StringUtils.join(csv, obj.getString(BACKGROUND_IMAGE) + AIRConstants.DEFAULT_SEPARATOR); //thumbnailUrl
        csv = StringUtils.join(csv, obj.getInt(DISCOUNTED_PRICE) + AIRConstants.DEFAULT_SEPARATOR); //value
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR); //PageUrl
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR); //inventory
        csv = StringUtils.join(csv, AIRConstants.NULL + AIRConstants.DEFAULT_SEPARATOR); //margin
        csv = StringUtils.join(csv, obj.getString(BACKGROUND_IMAGE) + AIRConstants.DEFAULT_SEPARATOR); //thumbnailS7Url
        csv = StringUtils.join(csv, obj.getString(CATEGORY_SLUG) + AIRConstants.DEFAULT_SEPARATOR); //tags
        csv = StringUtils.join(csv, AIRConstants.LEFT_SQUARE_BRACKET + AIRConstants.DOUBLE_QUOTES
                  + obj.getString(CATEGORY_NAME) + AIRConstants.DOUBLE_QUOTES + AIRConstants.RIGHT_SQUARE_BRACKET);
        Iterator valueIterator = obj.keys();
        String key = StringUtils.EMPTY;
        while (valueIterator.hasNext()) {
          key = valueIterator.next().toString(); //check key is skipped or not
          if (key.equals(ID) || key.equals(Constants.CONST_NAME) || key.equals(BACKGROUND_IMAGE)
              || key.equals(CATEGORY_NAME) || key.equals(DISCOUNTED_PRICE)
              || key.equals(CATEGORY_SLUG) || key.equals(SUB_CATEGORY) || key.equals(IS_DMC)
              || key.equals(SECONDARY_DESTINATIONS) || key.equals(DURATION) || key.equals(WAIVER_REQUIRED_INPUT)
              || key.equals(SLUG) || key.equals(MIN_CAPACITY) || key.equals(BOOK_ONLINE)
              || key.equals(BACKGROUND_IMAGES) || key.equals(MAX_CAPACITY) || key.equals(WAIVERS_VIEW)
              || key.equals(HAS_WAIVERS_BOOKING) || key.equals(LIST_PRICE) || key.equals(DURATION_UNIT)
              || key.equals(SECONDARY_DESTINATION) || key.equals(PRICE_TYPE) || key.equals(CITY_ID)
              || key.equals(CONTACT_NAME)) {
            continue;
          }
          csv = StringUtils.join(csv, AIRConstants.DEFAULT_SEPARATOR);
          if (null != obj.optJSONArray(key)) {
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(obj.getJSONArray(key).toString()));
          } else {
            csv = StringUtils.join(csv, obj.get(key).toString());
          }
          if (key.equals(CATEGORY_SLUG) && null != obj.get(key).toString()) {
            String catergoriesString = obj.get(key).toString();
            csv = StringUtils.join(csv, AIRConstants.DEFAULT_SEPARATOR);
            csv = StringUtils.join(csv, StringEscapeUtils.escapeCsv(catergoriesString));
          }
        }
        csv = StringUtils.join(csv, System.lineSeparator());
      }
    }
    OutputStream outputStream = response.getOutputStream();
    response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
    response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.PACKAGE_RESPONSE_HEADER);
    outputStream.write(csv.getBytes(AIRConstants.UTF));
    outputStream.flush();
    outputStream.close();
  }

  /**
   *
   * @return Header of CSV of AT feeds
   */
  public static @NotNull String getCSVHead() {
    String csvHead = StringUtils.EMPTY;
    csvHead = StringUtils.join(csvHead, "## RECSRecommendations Upload File");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead =  StringUtils.join(csvHead, "## RECS''## RECS'' indicates a Recommendations pre-process header. "
      + "Please do not remove these lines.");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead = StringUtils.join(csvHead, "## RECS");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead = StringUtils.join(csvHead, "## RECSUse this file to upload product display information"
      + " to Recommendations.Each product has its own row. Each line must contain 19"
      + " values and if not all are filled"
      + " a space should be left.");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead = StringUtils.join(csvHead, "## RECSThe last 100 columns (entity.custom1 - entity.custom100) are custom."
      + " The name 'customN' can be replaced with a custom name such as 'onSale' or 'brand'.");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead = StringUtils.join(csvHead, "## RECSIf the products already exist in Recommendations then changes "
      + "uploaded here will override the data in Recommendations. Any new attributes entered here will be "
      + "added to the product''s entry in Recommendations.");
    csvHead = StringUtils.join(csvHead, System.lineSeparator());
    csvHead = StringUtils.join(csvHead, "## RECSentity.id,entity.name,entity.categoryId,entity.message,"
          + "entity.thumbnailUrl,entity.value,"
    + "entity.pageUrl,entity.inventory,entity.margin,entity.thumbnailS7Url,entity.tags,entity.interests");
    return csvHead;
  }

}
