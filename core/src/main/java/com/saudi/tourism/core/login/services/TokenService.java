package com.saudi.tourism.core.login.services;

import java.io.IOException;

/**
 * This contains all methods to get currency data.
 */
public interface TokenService {

  /**
   * Get currency data from API service.
   *
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  String getMachineToMachineToken() throws IOException;
}
