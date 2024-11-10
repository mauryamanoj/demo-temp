
package com.saudi.tourism.core.login.services;

import com.saudi.tourism.core.login.models.UserIDToken;

import java.io.IOException;
import java.util.Map;

/**
 * This contains all methods to get user data.
 */
public interface LoginUserService {

  /**
   * Get user data from auth0, not populated.
   *
   * @param userIDToken the user id token
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  String getUserDetails(UserIDToken userIDToken, String type) throws IOException;

  /**
   * Get user data from auth0, populate output with full data and filter output list by property
   * values.
   *
   * @param userIDToken  the user id token
   * @param type         the type
   * @param filterParams properties map for filtering
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  String getUserDetailsFull(UserIDToken userIDToken, String type, Map<String, Object> filterParams)
        throws IOException;

  /**
   * update user data to auth0.
   *
   * @param body        the body
   * @param userIDToken the userId
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  String updateUserMetadata(String body, UserIDToken userIDToken, String type) throws IOException;

  /**
   * delete user fav/trip to auth0.
   *
   * @param body        the body
   * @param userIDToken the userId
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  String deleteFavTrip(String body, UserIDToken userIDToken, String type) throws IOException;

  /**
   * Save one trip plan to auth0 and returns its id (it is used in TripPlanService for storing
   * just created trip plans).
   *
   * @param tripPlanJson trip plan json to save
   * @param userIDToken  user id token
   * @return saved trip plan id
   * @throws IOException if couldn't save
   */
  String saveTripPlan(String tripPlanJson, UserIDToken userIDToken) throws IOException;
}
