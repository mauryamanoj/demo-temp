package com.saudi.tourism.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class is used as a utitlity for data based operations.
 */
public final class DataUtils {

  /**
   * private constructor.
   */
  private DataUtils() {
    // private constructor.
  }

  /**
   * This method is used to filter required currency data.
   *
   * @param fullCurrencyData String
   * @param currency         the currency
   * @return JsonObject .
   */
  public static JsonObject filterRequiredCurrencyData(String fullCurrencyData,
      final String currency) {
    JsonParser parser = new JsonParser();
    JsonObject requiredCurrencyData = new JsonObject();
    JsonElement jsonTree = parser.parse(fullCurrencyData);
    if (Objects.nonNull(jsonTree) && jsonTree.isJsonObject()) {
      JsonObject jsonObject = jsonTree.getAsJsonObject();
      if (currency.contains(",")) {
        requiredCurrencyData.add(Constants.CONST_STR_CURRENCIES,
            jsonObject.get(Constants.CONST_QUOTES));
      } else {
        String source = jsonObject.get(Constants.CONST_SOURCE).getAsString();
        requiredCurrencyData.add(Constants.LABEL, parser.parse(currency));
        JsonElement jsonElement = parser.parse(String.format("%.2f", jsonObject
            .get(Constants.CONST_QUOTES).getAsJsonObject().get(source + currency).getAsDouble()));
        requiredCurrencyData.add(Constants.CONST_STR_CURRENCY, jsonElement);
      }
    }
    return requiredCurrencyData;
  }

  /**
   * This method is used to get json data via Scanner.
   *
   * @param dataStream InputStream
   * @return String .
   */
  public static String getJsonData(InputStream dataStream) {
    Scanner sc = new Scanner(dataStream);
    StringBuilder stringBuilder = new StringBuilder();
    while (sc.hasNext()) {
      stringBuilder.append(sc.nextLine());
    }
    sc.close();
    return stringBuilder.toString();
  }

  /**
   * This method is used to get input stream out of from the string.
   *
   * @param inputString String
   * @return InputStream .
   */
  public static InputStream getDataValues(String inputString) {
    return new ByteArrayInputStream(inputString.getBytes());

  }

  /**
   * This method is used to prpare required road trips data.
   *
   * @param loadedRoadTripScenarios String
   * @param limit                   the number of road trips
   * @return List<String> scenario Ids to be used for loading data of road trips .
   * @param city .
   */
  public static List<String> filterRequiredRoadTripScenariosApiUrl(String loadedRoadTripScenarios,
      int limit, String city) {
    JsonParser parser = new JsonParser();
    List<String> scenarioIds = new ArrayList<String>();
    JsonElement jsonTree = parser.parse(loadedRoadTripScenarios);
    if (Objects.nonNull(jsonTree) && jsonTree.isJsonObject()) {
      JsonObject jsonObject = jsonTree.getAsJsonObject();

      int count = jsonObject.get("count").getAsInt();

      JsonArray scenarios = jsonObject.get("scenarios").getAsJsonArray();

      if (count < limit) {
        limit = scenarios.size();
      }

      if (limit <= 0) {
        limit = count;
      }

      for (JsonElement scenario : scenarios) {

        if (StringUtils.isNoneBlank(city)) {
          if (null != scenario
              && scenario.getAsJsonObject().get("start_city_name").getAsJsonObject().get("en")
              .getAsJsonObject().get("text").getAsString().toLowerCase().equals(city) && scenarioIds.size() < limit) {
            scenarioIds.add(scenario.getAsJsonObject().get("scenario_id").getAsString());
          }
        } else {
          if (scenarioIds.size() < limit) {
            scenarioIds.add(scenario.getAsJsonObject().get("scenario_id").getAsString());
          }
        }
      }
    }
    return scenarioIds;

  }

}
