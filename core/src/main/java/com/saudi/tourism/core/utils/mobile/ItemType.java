package com.saudi.tourism.core.utils.mobile;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different types of items.
 */
@AllArgsConstructor
@Getter
public enum ItemType {
  /**
   * Represents an attraction.
   */
  ATTRACTION("attraction"),

  /**
   * Represents an activity.
   */
  ACTIVITY("activity"),

  /**
   * Represents a tour.
   */
  TOUR("tour"),

  /**
   * Represents a point of interest.
   */
  POI("poi"),

  /**
   * Represents an event.
   */
  EVENT("event"),

  /**
   * Represents a story.
   */
  STORY("story"),

  /**
   * Represents an experience.
   */
  EXPERIENCE("experience");

  /**
   * The code associated with the item type.
   */
  private final String code;
}
