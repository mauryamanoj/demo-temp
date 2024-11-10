package com.saudi.tourism.core.utils;

/**
 * This is to define Status Enum.
 */
public enum StatusEnum {

  /**
   * The success.
   */
  SUCCESS(200),
  /**
   * The No Content.
   */
  NO_CONTENT(204),

  /**
   * The bad request.
   */
  BAD_REQUEST(400),

  /**
   * The not found error.
   */
  NOT_FOUND(404),

  /**
   * Forbidden.
   */
  FORBIDDEN_AUTH_ERROR(401),
  /**
   * The internal server error.
   */
  INTERNAL_SERVER_ERROR(500);

  /**
   * The value.
   */
  private Integer value;

  /**
   * Instantiates a new status enum.
   *
   * @param value the value
   */
  StatusEnum(Integer value) {
    this.value = value;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public Integer getValue() {
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
}
