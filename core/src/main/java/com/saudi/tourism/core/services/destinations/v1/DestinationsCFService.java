package com.saudi.tourism.core.services.destinations.v1;

import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;

import java.util.List;

/**
 * Destinations CF Service.
 */
public interface DestinationsCFService {
  /**
   * Fetch all Destination CF authored for a locale.
   *
   * @param locale Current locale
   * @return list ofDestination CF
   */
  List<DestinationCFModel> fetchAllDestination(String locale);
  /**
   * Fetch all Destination  authored for a locale.
   *
   * @param locale Current locale
   * @return list of Destination
   */
  List<Destination> returnAllDestination(String locale);


  /**
   * Fetch Destination by id for a locale.
   *
   * @param locale Current locale
   * @param destinationId destinationId
   * @return Destination
   */
  DestinationCFModel getDestinationById(String locale, String destinationId);
}
