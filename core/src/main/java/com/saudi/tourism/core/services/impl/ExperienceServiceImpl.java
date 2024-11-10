package com.saudi.tourism.core.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Currency Data Service.
 */
@Slf4j
@Component(service = ExperienceService.class, immediate = true)
public class ExperienceServiceImpl implements ExperienceService {
  /**
   * for handling json format in response.
   */
  private JsonParser parser = new JsonParser();

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  @Override
  public Object getAllExperiences(Map<String, Object> queryStrings) throws IOException {
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(
        getHalayallaEndPointUrl() + "experiences", saudiTourismConfig.getHalayallaToken(),
        queryStrings);
    return getFilteredDataOfExperiences(responseMessage, queryStrings);
  }

  /**
   * Returns boolean to filter package.
   *
   * @param queryStrings     queries.
   * @param isDiscountFilter is Discount.
   * @param isPriceFilter    is Price Filter.
   * @param listPrice        list Price.
   * @param discountPrice    discount price.
   * @return true/ false.
   */
  private boolean isFilterPackage(Map<String, Object> queryStrings, boolean isDiscountFilter,
                                  boolean isPriceFilter, JsonElement listPrice, JsonElement discountPrice) {
    return (!isDiscountFilter || (listPrice.getAsInt()
        > discountPrice.getAsInt()))
        && (!isPriceFilter || (((null == queryStrings.get("minPrice"))
        || (Integer.parseInt(queryStrings.get("minPrice").toString())
        < discountPrice.getAsInt()))
        && ((null == queryStrings.get("maxPrice"))
        || (Integer.parseInt(queryStrings.get("maxPrice").toString())
        > discountPrice.getAsInt()))));
  }

  @Override
  public Object getExperienceDetails(Map<String, Object> queryString, String experienceId)
      throws IOException {
    StringBuilder sb = new StringBuilder(getHalayallaEndPointUrl());
    sb.append("experiences");
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append(experienceId);
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(sb.toString(),
        saudiTourismConfig.getHalayallaToken(), queryString);

    JsonElement json = parser.parse(responseMessage.getMessage());
    return json;

  }

  @Override
  public JsonObject getVenueDetails(@NonNull final String locale, @NonNull final String experienceId)
      throws IOException {
    StringBuilder sb = new StringBuilder(saudiTourismConfig.getHalayallaEndPointUrl());
    sb.append("/api/book/venue/detail");

    final JsonObject body = new JsonObject();
    body.addProperty("venue_id", experienceId);
    body.addProperty("lang", locale);

    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    headerMap.put("token", saudiTourismConfig.getHalayallaToken());
    final ResponseMessage responseMessage =
        RestHelper.executeMethodPost(sb.toString(), body.toString(), null, headerMap);

    JsonElement json = parser.parse(responseMessage.getMessage());

    JsonObject experienceJson = json.getAsJsonObject();

    if (experienceJson.has("status") && experienceJson.get("status").getAsString().equals("Success")) {
      return experienceJson;
    } else {
      return null;
    }
  }

  @Override
  public Object getExperienceBookingOptions(Map<String, Object> queryString, String experienceId)
      throws IOException {
    StringBuilder sb = new StringBuilder(getHalayallaEndPointUrl());
    sb.append("experiences");
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append(experienceId);
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append("booking-options");
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(sb.toString(),
        saudiTourismConfig.getHalayallaToken(), queryString);

    JsonElement json = parser.parse(responseMessage.getMessage());
    return json;
  }

  @Override
  public Object getExperienceSuggestion(Map<String, Object> queryString, String experienceId)
      throws IOException {
    StringBuilder sb = new StringBuilder(getHalayallaEndPointUrl());
    sb.append("experiences");
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append(experienceId);
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append("suggestions");
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(sb.toString(),
        saudiTourismConfig.getHalayallaToken(), queryString);

    JsonElement json = parser.parse(responseMessage.getMessage());
    return json;
  }

  @Override
  public Object getExperienceCategories(Map<String, Object> queryString) throws IOException {
    StringBuilder sb = new StringBuilder(getHalayallaEndPointUrl());
    sb.append("experiences");
    sb.append(SpecialCharConstants.FORWARD_SLASH);
    sb.append("categories");
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(sb.toString(),
        saudiTourismConfig.getHalayallaToken(), queryString);

    JsonElement json = parser.parse(responseMessage.getMessage());
    return json;
  }

  @Override
  public Object getMultipleIds(String body) throws IOException {
    String endPoint = saudiTourismConfig.getHalayallaEndPointUrl() + "/api/vs/" + "experiences/by-ids";
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    headerMap.put("token", saudiTourismConfig.getHalayallaToken());
    ResponseMessage responseMessage = RestHelper.executeMethodPost(
        endPoint, body, null, headerMap);
    JsonElement json = parser.parse(responseMessage.getMessage());
    JsonObject experienceJson = json.getAsJsonObject();
    if ((experienceJson.has("status") && (experienceJson.get("status").
        equals("failed") || experienceJson.get("status").equals("error")))) {
      throw new RuntimeException(responseMessage.getMessage());
    }
    experienceJson.remove("status");
    return experienceJson.toString();
  }

  @Override
  public Object getExperiencesDetails(
      @NonNull Map<String, Object> queryString, @NonNull List<String> experienceIds)
      throws IOException {
    final String endPoint = saudiTourismConfig.getHalayallaEndPointUrl() + "/api/vs/" + "experiences/by-ids";
    final Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    headerMap.put("token", saudiTourismConfig.getHalayallaToken());
    final JsonObject body = new JsonObject();
    final JsonArray experienceIdsArray = new JsonArray();
    experienceIds.forEach(experienceId -> experienceIdsArray.add(experienceId));
    body.add("experience_ids", experienceIdsArray);

    // The token is passed as a header
    // 'RestHelper.executeMethodPost' is not handling the token as
    // 'RestHelper.executeMethodGetWithQueryParameters'
    // 'RestHelper.executeMethodPost' passes the token as an 'Authorization' header
    // 'RestHelper.executeMethodGetWithQueryParameters' passes the token as a 'token' header
    final ResponseMessage responseMessage =
        RestHelper.executeMethodPost(endPoint, body.toString(), null, headerMap);

    final JsonElement json = parser.parse(responseMessage.getMessage());
    final JsonObject experienceJson = json.getAsJsonObject();
    if ((experienceJson.has("status")
        && (experienceJson.get("status").equals("failed")
        || experienceJson.get("status").equals("error")))) {
      throw new RuntimeException(responseMessage.getMessage());
    }

    return experienceJson;
  }

  /**
   * This method is used to construct the URL.
   *
   * @return String .
   */
  private String getHalayallaEndPointUrl() {

    return String.format(saudiTourismConfig.getHalayallaEndPointUrl()) + "/api/vs/";
  }

  /**
   * @param responseMessage responseMessage.
   * @param queryStrings    queryStrings.
   * @return JsonElement JsonElement.
   * @throws IOException IOException.
   */
  public JsonElement getFilteredDataOfExperiences(ResponseMessage responseMessage,
                                                  Map<String, Object> queryStrings) throws IOException {
    JsonElement json = parser.parse(responseMessage.getMessage());
    boolean isDiscountFilter = (queryStrings.containsKey("discounted")
        && (queryStrings.get("discounted").equals("true")));
    boolean isPriceFilter = (queryStrings.containsKey("minPrice") || queryStrings.containsKey("maxPrice"));
    if ((null != json) && (isDiscountFilter || isPriceFilter)) {
      JsonObject responseObj = (JsonObject) json;
      JsonArray data = responseObj.getAsJsonArray("data");
      JsonArray filterData = new JsonArray();
      data.forEach(item -> {
        JsonObject itemObj = (JsonObject) item;
        final JsonElement listPrice = itemObj.get("list_price");
        JsonElement discountPrice = itemObj.get("discounted_price");
        if (null == discountPrice) {
          discountPrice = listPrice;
        }
        if (null != listPrice) {
          if (isFilterPackage(queryStrings, isDiscountFilter,
              isPriceFilter, listPrice, discountPrice)) {
            filterData.add(item);
          }
        } else {
          filterData.add(item);
          LOGGER.error("Error in Halayalla package listPrice: {} or discountPrice:{}",
              listPrice, discountPrice);
        }
      });
      responseObj.remove("data");
      responseObj.add("data", filterData);
      responseObj.remove("total");
      responseObj.addProperty("total", filterData.size());
      if (null != responseObj.get("per_page")) {
        int perPage = responseObj.get("per_page").getAsInt();
        responseObj.remove("last_page");
        responseObj.addProperty("last_page", ((filterData.size() - 1) / perPage) + 1);
      }
    }
    return json;
  }

  /**
   * This API is V2 version of Experiences Service .
   *
   * @param queryStrings used to pass query strings.
   * @return
   * @throws IOException IOException.
   */
  @Override
  public Object getAllExperiencesV2(Map<String, Object> queryStrings) throws IOException {

    String domainUrl = saudiTourismConfig.getHalayallaEndPointUrlV2() + "listing";
    String token = saudiTourismConfig.getHalayallaTokenV2();
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(
        domainUrl, token, queryStrings);
    return getFilteredDataOfExperiences(responseMessage, queryStrings);
  }
}
