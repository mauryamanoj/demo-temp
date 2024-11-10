package com.saudi.tourism.core.services;

import java.io.IOException;

/**
 * Interface for MyTable Login Configurations Service to expose only login methods.
 */
public interface MyTableLoginService {

  /**
   * Login to MyTable and return the response.
   * @throws IOException error
   * @return response from MyTable
   */
  String loginAndGetResponse() throws IOException;
}
