package com.saudi.tourism.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum representing VSW crops. */
@Getter
@AllArgsConstructor
public enum CropEnum {
  /** crop-1920x768. */
  CROP_1920x768("crop-1920x768"),

  /** crop-1920x1080. */
  CROP_1920X1080("crop-1920x1080"),

  /** crop-1280x452. */
  CROP_1280x452("crop-1280x452"),

  /** crop-660x337. */
  CROP_660x337("crop-660x337"),

  /** crop-375x667. */
  CROP_375x667("crop-375x667"),


  /** crop-375x210. */
  CROP_375x210("crop-375x210"),

  /** crop-760x570. */
  CROP_760x570("crop-760x570"),

  /** crop-375x280. */
  CROP_375x280("crop-375x280"),

  /** crop-460x620. */
  CROP_460x620("crop-460x620"),

  /** crop-469x264. */
  CROP_469x264("crop-469x264"),

  /** crop-600x600. */
  CROP_600x600("crop-600x600"),

  /** crop-1160x650. */
  CROP_1160x650("crop-1160x650");

  /** The value. */
  private String value;
}
