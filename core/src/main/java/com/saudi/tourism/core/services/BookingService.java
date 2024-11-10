package com.saudi.tourism.core.services;

import java.io.IOException;
import java.util.Map;

/**
 * @author vkaila.ext
 * Service is used to fetch the bookings from halayalla service
 */
public interface BookingService {

  /**
   * Get all bookings from API service.
   * @param queryStrings used to pass query strings
   * @return Object experience data from api
   * @throws IOException IOException .
   */
  Object getAllBookings(Map<String, Object> queryStrings) throws IOException;

}
