package com.saudi.tourism.core.services;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author vkaila.ext Service is used to fetch the experience funtionality from
 *         halayalla service
 */
public interface ExperienceService {

  /**
   * Get all experiences from API service.
   * @param queryString used to pass query strings
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getAllExperiences(Map<String, Object> queryString) throws IOException;

  /**
   * Get experience details from API service.
   * @param queryString  used to pass query strings
   * @param experienceId experienceId for which details are required
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getExperienceDetails(Map<String, Object> queryString, String experienceId)
      throws IOException;

  /**
   * Get experience details from Venue details API service.
   * @param locale  used to pass locale
   * @param experienceId experienceId for which details are required
   * @return JsonObject experience data from api
   * @throws IOException IOException .
   */
  JsonObject getVenueDetails(String locale, String experienceId)
      throws IOException;

  /**
   * Get experience Booking options from API service.
   * @param queryString  used to pass query strings
   * @param experienceId experienceId for which details are required
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getExperienceBookingOptions(Map<String, Object> queryString, String experienceId) throws IOException;

  /**
   * Get experience Suggestion from API service.
   * @param queryString  used to pass query strings
   * @param experienceId experienceId for which details are required
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getExperienceSuggestion(Map<String, Object> queryString, String experienceId)
      throws IOException;

  /**
   * Get experience Categories from API service.
   * @param queryString used to pass query strings
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getExperienceCategories(Map<String, Object> queryString) throws IOException;

  /**
   *
   * @param body body.
   * @return Object Multiple Ids api .
   * @throws IOException IOException.
   */
  Object getMultipleIds(String body) throws IOException;

  /**
   * Get experience details from API service.
   *
   * @param queryString used to pass query strings
   * @param experienceIds List of experience ids for which details are required
   * @return Object experiences data from api
   * @throws IOException IOException .
   */
  Object getExperiencesDetails(Map<String, Object> queryString, List<String> experienceIds)
      throws IOException;

  /**
   * Get experience list object from API service.
   * @param queryString  used to pass query strings
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getAllExperiencesV2(Map<String, Object> queryString) throws IOException;

}
