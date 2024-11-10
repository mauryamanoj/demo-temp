package com.saudi.tourism.core.services;

import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripCustomResponse;
import org.json.JSONException;

import java.io.IOException;

/**
 * This contains all methods to get 4 road trip scenarios with  data needed for summer season component.
 */
public interface RoadTripScenariosService {

  /**
   * Get road trip scenarios data from API service.
   * @param limit number of road trips required
   * @return Object road trip scenarios data from api
   * @throws IOException IOException .
   */
  Object getRoadTripScenariosFromApi(Integer limit) throws IOException;
  /**
   * @param limit passing limit.
   * @param offset passing offset.
   * @param locale passing locale.
   * @param body passing body.
   * @return return object.
   * @throws IOException IOException.
   */
  RoadTripCustomResponse getRoadTripScenariosNativeApp(Integer limit, Integer offset, String locale,
                                                       String body) throws IOException;

  /**
   *
   * @param locale locale.
   * @return roadTrip Filter.
   * @throws IOException exception.
   */
  Object getRoadTripScenariosFilterNativeApp(String locale) throws IOException;

  /**
   *
   * @param locale locale.
   * @param scenarioId scenarioId.
   * @return Object .
   * @throws IOException IOException.
   */

  Object getRoadTripScenarioDetailByIdNativeApp(String locale, String scenarioId)throws IOException;

  /**
   *
   * @param scenarioId scenarioId.
   * @param language language.
   * @param pathStyle pathStyle.
   * @param pinStyle pinStyle.
   * @param dayParam dayParam.
   * @param size size.
   * @return Object.
   * @throws IOException IOException.
   */
  Object getRoadTripViewOnMapUrl(String scenarioId, String language, String pathStyle,
                                 String pinStyle, Integer dayParam, String size)throws IOException;

  /**
   *
   * @param userId userId.
   * @param locale locale.
   * @return Author Details Object.
   * @throws IOException IOException.
   * @throws JSONException jsonException.
   */
  Object getRoadTripAuthorDetails(String userId, String locale) throws IOException, JSONException;

}
