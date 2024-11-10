package com.saudi.tourism.core.beans.nativeapp;

import lombok.Getter;
import lombok.Setter;

/**
 * Data objects for calendar's app api.
 */
@Getter
@Setter
public class EventsDataObj {

  /**
   * start date.
   */
  private String startDate;
  /**
   * end date.
   */
  private String endDate;
  /**
   * title.
   */
  private String title;
  /**
   * s7featureEventImage.
   */
  private String s7featureEventImage;
  /**
   * featureEventImage.
   */
  private String featureEventImage;
  /**
   * city.
   */
  private String city;
  /**
   * favId.
   */
  private String favId;
  /**
   * link.
   */
  private String link;
  /**
   * id.
   */
  private String id;
  /**
   * appId.
   */
  private String appId;

  /**
   * featured.
   */
  private Boolean featured;

}
