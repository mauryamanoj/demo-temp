package com.saudi.tourism.core.models.components.events;

import lombok.Data;

/**
 * The Class EventsRequestParams.
 */
@Data
public class BaseRequestParams {

  /**
   * The language.
   */
  private String locale;

  /**
   * Flag to retrieve all the events.
   */
  private boolean all = false;

  /**
   * Flag to retrieve all expired events.
   */
  private boolean allExpired = false;

  /**
   * The limit.
   */
  private Integer limit = 0;

  /**
   * The offset.
   */
  private Integer offset = 0;

  /**
   * THe IS homepage.
   */
  private boolean homePage = false;

}
