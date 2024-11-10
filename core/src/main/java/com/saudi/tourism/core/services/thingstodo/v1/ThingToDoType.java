package com.saudi.tourism.core.services.thingstodo.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Thing ToDo types enum.
 */
@AllArgsConstructor
@Getter
public enum ThingToDoType {
  /**
   * Attraction type.
   */
  ATTRACTION("attraction"),
  /**
   * Activity type.
   */
  ACTIVITY("activity"),
  /**
   * Tour type.
   */
  TOUR("tour"),
  /**
   * Event type.
   */
  EVENT("event"),
  /**
   * POI value.
   */
  POI("poi");

  /**
   * The value.
   */
  private String code;
}
