package com.saudi.tourism.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * General Infos enum.
 */
@AllArgsConstructor
@Getter
public enum GeneralInfosIcons {
  /**
   * accessibility.
   */
  ACCESSIBILITY("accessibility"),
  /**
   * age.
   */
  AGE("age"),
  /**
   * duration.
   */
  DURATION("duration"),
  /**
   * group.
   */
  GROUP("group"),
  /**
   * language.
   */
  LANGUAGE("language"),
  /**
   * location.
   */
  LOCATION("location");

  /**
   * The value.
   */
  private String code;
}
