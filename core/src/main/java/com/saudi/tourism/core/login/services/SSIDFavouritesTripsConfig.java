package com.saudi.tourism.core.login.services;

/**
 * Interface for Saudi SSID Configurations Service to expose only required
 * methods.
 */

public interface SSIDFavouritesTripsConfig {

  /**
   * Gets the middleware domain.
   *
   * @return the middleware domain
   */
  String getMiddlewareDomain();

  /**
   * Gets the fetch favorites trips endpoint.
   *
   * @return the fetch favorites trips endpoint
   */
  String getFetchFavoritesTripsEndpoint();

  /**
   * Gets the modify favorites trips endpoint.
   *
   * @return the modify favorites trips endpoint
   */
  String getModifyFavoritesTripsEndpoint();

  /**
   * Gets the fetch Native App User Details endpoint.
   *
   * @return the fetch Native App User Details endpoint
   */
  String getNativeAppUserDetailsEndpoint();

  /**
   * Gets the get Entertainer locations endpoint.
   *
   * @return the get Entertainer locations endpoint
   */
  String getEntertainerLocationsEndpoint();

}
