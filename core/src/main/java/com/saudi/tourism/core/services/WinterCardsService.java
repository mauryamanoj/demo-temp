package com.saudi.tourism.core.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service for Winter Cards .
 */
public interface WinterCardsService {

  /**
   * getExperienceCategories for Winter Cards .
   * @param queryString used to pass query strings
   * @return String experience data from api
   * @throws IOException IOException .
   */
  String getExperienceCategories(Map<String, Object> queryString) throws IOException;

  /**
   * getExperience for Winter Cards .
   * @param queryString used to pass query strings
   * @return String experience data from api
   * @throws IOException IOException .
   */
  String getExperience(Map<String, Object> queryString) throws IOException;

  /**
   * getRoadTripScenariosFromApi for Winter Cards .
   * @param limit used to pass query strings
   * @param locale Locale String
   * @return String experience data from api
   * @throws IOException IOException .
   * @param city .
   */
  List getRoadTripScenariosFromApi(Integer limit, String locale, String city) throws IOException;

  /**
   * getExperienceDetails for Winter Cards .
   * @param queryString used to pass query strings
   * @param experienceId used to pass the id
   * @return String experience data from api
   * @throws IOException IOException .
   */
  String getExperienceDetails(Map<String, Object> queryString, String experienceId) throws IOException;
}
