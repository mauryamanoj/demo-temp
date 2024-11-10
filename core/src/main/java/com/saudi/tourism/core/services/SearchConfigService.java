package com.saudi.tourism.core.services;

import com.saudi.tourism.core.beans.SearchConfigResponse;

/**
 * ContactService.
 */
public interface SearchConfigService {

  /**
   * Method getConfig .
   *
   * @param locale localePath
   * @param source source
   * @return json
   */
  SearchConfigResponse getConfig(String locale, String source);
}
