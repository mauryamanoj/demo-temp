package com.saudi.tourism.core.utils;

/**
 * This defines of Message Type.
 */
public enum MessageType {

  /**
   * The error.
   */
  ERROR("error"),
  /**
   * The warning.
   */
  WARNING("warning"),
  /**
   * The success.
   */
  SUCCESS("success");

  /**
   * The type.
   */
  private String type;

  /**
   * Instantiates a new message type.
   *
   * @param type the type
   */
  MessageType(String type) {
    this.type = type;
  }

  /**
   * Gets the String of type.
   *
   * @return the type
   */
  public String getType() {
    return this.type;
  }
}
