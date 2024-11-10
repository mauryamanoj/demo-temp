package com.saudi.tourism.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum representing ornaments. */
@Getter
@AllArgsConstructor
public enum OrnamentEnum {
  /**
   * Ornament : 03A.
   */
  ORNAMENT_03A("03A"),

  /**
   *  Ornament : 07A.
   */
  ORNAMENT_07A("07A");

  /** The value. */
  private String value;
}
