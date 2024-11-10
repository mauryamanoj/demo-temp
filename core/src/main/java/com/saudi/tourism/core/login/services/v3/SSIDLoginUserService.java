package com.saudi.tourism.core.login.services.v3;

import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.models.common.ResponseMessage;

import java.io.IOException;
import java.util.Map;

/**
 * This contains all methods to get user data.
 */
public interface SSIDLoginUserService {

  /**
   * Get user data from auth0, not populated.
   *
   * @param userIDToken the user id token
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   * @throws MiddlewareException Middleware exception.
   */
  String getUserDetails(UserIDToken userIDToken, String type) throws IOException, MiddlewareException;

  /**
   * Get user data from auth0, populate output with full data and filter output list by property
   * values.
   *
   * @param userIDToken  the user id token
   * @param type         the type
   * @param filterParams properties map for filtering
   * @return Object currency data from api
   * @throws IOException IOException .
   * @throws MiddlewareException MiddlewareException .
   */
  String getUserDetailsFull(UserIDToken userIDToken, String type, Map<String, Object> filterParams)
      throws IOException, MiddlewareException;

  /**
   * update user data to auth0.
   *
   * @param body        the body
   * @param userIDToken the userId
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   * @throws MiddlewareException Middleware exception.
   * @throws SSIDFunctionnalException SSID exception.
   */
  String updateUserMetadata(String body, UserIDToken userIDToken, String type)
      throws IOException, SSIDFunctionnalException, MiddlewareException;

  /**
   * delete user fav/trip to auth0.
   *
   * @param body        the body
   * @param userIDToken the userId
   * @param type        the type
   * @return Object currency data from api
   * @throws IOException IOException .
   * @throws MiddlewareException Middleware exception .
   * @throws SSIDFunctionnalException SSID functionnal exception .
   *
   */
  String deleteFavTrip(String body, UserIDToken userIDToken, String type)
      throws IOException, MiddlewareException, SSIDFunctionnalException;

  /**
   * Save one trip plan to auth0 and returns its id (it is used in TripPlanService for storing
   * just created trip plans).
   *
   * @param tripPlanJson trip plan json to save
   * @param userIDToken  user id token
   * @return saved trip plan id
   * @throws IOException if couldn't save
   * @throws SSIDFunctionnalException SSID exception
   * @throws MiddlewareException Middleware exception
   */
  String saveTripPlan(String tripPlanJson, UserIDToken userIDToken)
      throws IOException, SSIDFunctionnalException, MiddlewareException;

  /**
   * Save one trip plan to Native App and returns its id (it is used in TripPlanService for storing
   * just created trip plans).
   *
   * @param tripPlanJson trip plan json to save
   * @param userIDToken  user id token
   * @return saved trip plan id
   * @throws IOException if couldn't save
   * @throws MiddlewareException MiddlewareException
   * @throws SSIDFunctionnalException SSIDFunctionnalException
   */
  ResponseMessage saveTripPlanNativeApp(String tripPlanJson, UserIDToken userIDToken)
      throws IOException, MiddlewareException, SSIDFunctionnalException;
}
