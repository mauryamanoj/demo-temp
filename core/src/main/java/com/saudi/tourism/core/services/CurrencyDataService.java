package com.saudi.tourism.core.services;

import java.io.IOException;

/**
 * This contains all methods to get currency data.
 */
public interface CurrencyDataService {

  /**
   * Get currency data from API service.
   *
   * @param currency the currency
   * @return Object currency data from api
   * @throws IOException IOException .
   */
  Object getCurrencyDataFromApi(String currency) throws IOException;
}
