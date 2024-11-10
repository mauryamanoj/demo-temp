package com.saudi.tourism.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum representing Image Relative Widths. */
@Getter
@AllArgsConstructor
public enum ImageRelativeWidthEnum {
  /** 100vw. */
  RELATIVE_WIDTH_100VW("100vw");

  /** The value. */
  private String value;
}
