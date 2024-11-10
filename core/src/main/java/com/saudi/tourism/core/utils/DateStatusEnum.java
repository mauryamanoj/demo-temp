package com.saudi.tourism.core.utils;

/**
 * This is to define Date Status Enum.
 */
public enum DateStatusEnum {

  /**
   * upcoming status.
   */
  UPCOMING("upcoming"),

  /**
   * ongoing status.
   */
  ONGOING("ongoing"),

  /**
   * The not found error.
   */
  EXPIRED("expired");



  /**
   * The value.
   */
  private final String value;

  /**
   * Instantiates a new status enum.
   *
   * @param value the value
   */
  DateStatusEnum(final String value) {
    this.value = value;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Gets the status.
   *
   * @return the name
   */
  public String getStatus() {
    return this.name();
  }

  /**
   * Get enum value by value.
   *
   * @param value name value
   * @return DateStatusEnum
   */
  public static DateStatusEnum getByValue(String value) {
    for (DateStatusEnum status : values()) {
      if (status.getValue().equals(value)) {
        return status;
      }
    }
    return null;
  }
}
