package com.saudi.tourism.core.models.components.tripplan.v1;

/**
 * Trip exception.
 */
public class TripException extends Exception {
  /**
   * Constructor.
   */
  public TripException() {
  }

  /**
   * Constructor.
   *
   * @param message message
   */
  public TripException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param cause   cause
   */
  public TripException(String message, Throwable cause) {
    super(message, cause);
  }
}
