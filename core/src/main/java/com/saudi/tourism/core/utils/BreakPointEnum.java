package com.saudi.tourism.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum representing VSW breackpoints. */
@Getter
@AllArgsConstructor
public enum BreakPointEnum {
  /**
   * Desktop break point.
   */
  DESKTOP("1280"),
  /**
   * Mobile break point for 1023.
   */
  MOBILE_1023("1023"),

  /**
   * Mobile break point.
   */
  MOBILE("420");

  /** The value. */
  private String value;
}
