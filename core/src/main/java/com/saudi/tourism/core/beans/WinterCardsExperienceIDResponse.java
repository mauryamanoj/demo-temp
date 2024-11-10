package com.saudi.tourism.core.beans;

import lombok.Getter;

/**
 * Bean for the API response.
 */
@Getter
public class WinterCardsExperienceIDResponse {

 /**
 * Object data.
 */
  private Data data;

  /**
   * Class Data.
   */
  @Getter
  public class Data {

  /**
   * Variable description.
   */
    private String description;

  }
}
