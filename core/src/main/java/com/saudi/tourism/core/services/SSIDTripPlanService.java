package com.saudi.tourism.core.services;

import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.models.components.tripplan.CreateTripPlanFilter;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlan;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * This defines all methods to execute business of Trip Plans.
 */
public interface SSIDTripPlanService {

  /**
   * Returns trip plan filters for the specified locale, cloned instance of the cached object,
   * without cities extended data.
   *
   * @param language the current language
   * @return the trip plan app filter object
   * @throws RepositoryException if can't get some JCR data from the query
   */
  TripPlanFilter getTripPlanFilter(String language) throws RepositoryException;

  /**
   * Returns all existing trip plans for the specified locale. Uses memory cache.
   *
   * @param language the current language
   * @return list of all trip plan objects
   * @throws RepositoryException if can't get some JCR data from the query
   */
  List<TripPlan> getTripPlanList(String language) throws RepositoryException;

  /**
   * Creates a new trip plan according to requested properties (locale, cities list etc.) and tries
   * to save it into login storage, if the auth0 token was provided.
   * If trip plan was saved, it will obtain id (GUID value), otherwise the id will remain default
   * value for unsaved trip plans.
   *
   * @param filter trip plan filter instncce that contains all filtering data
   * @param user   user id token for auth0 (if not provided, the trip plan won't be saved
   * @return most favorite trip plan for the specified filter
   * @throws RepositoryException if can't get some JCR data from the query
   * @throws SSIDFunctionnalException SSIDFunctionnalException
   * @throws MiddlewareException MiddlewareException
   */
  TripPlan createTripPlan(CreateTripPlanFilter filter, UserIDToken user)
      throws RepositoryException, SSIDFunctionnalException, MiddlewareException;

  /**
   * Updates start/end dates of the result trip plan and its days (and updates filter) for the
   * proper output.
   *
   * @param tripPlan trip plan instance to update
   * @param filter   the current search filter object
   */
  void adjustDates(TripPlan tripPlan, CreateTripPlanFilter filter);

  /**
   * This prepares fulfilled trip plan object from the provided shortened object gotten from
   * user login storage.
   *
   * @param storedTripPlan   the shortened trip plan object from the login storage user metadata
   * @param originalTripPlan the corresponding original trip plan object
   * @param language         language for producing some titles (holidays etc.)
   * @return new TripPlan instance with all the necessary data
   */
  TripPlan getPopulatedUserTripPlan(@NotNull TripPlan storedTripPlan,
      @Nullable TripPlan originalTripPlan, String language);

  /**
   * Get data from trip plan page by its path.
   *
   * @param path path to trip plan page
   * @return TripPlan instance
   * @throws RepositoryException if couldn't read data from jcr
   */
  TripPlan getTripPlanByPath(String path) throws RepositoryException;

  /**
   * Extracts list of trip plans from user storage's metadata JSONArray object.
   *
   * @param metadataTripsArray JSONArray of stored trip plans
   * @param language           the current locale
   * @return list of trip plans populated with the correct data
   */
  List<TripPlan> fromJson(@NotNull JSONArray metadataTripsArray, String language);

  /**
   *
   * @param jsonString Post request body data to create a new trip .
   * @param user user id token for SSID storage (if not provided, the trip plan won't be saved.
   * @return Trip plan Object .
   * @throws SSIDFunctionnalException SSIDFunctionnalException
   * @throws MiddlewareException MiddlewareException
   * @throws RepositoryException if can't get some JCR data from the query
   */
  List<TripPlan> createUpdateTripPlanNativeApp(TripPlan jsonString, UserIDToken user)
      throws RepositoryException, SSIDFunctionnalException, MiddlewareException;
}
