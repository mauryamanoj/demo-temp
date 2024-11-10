package com.saudi.tourism.core.login;

/**
 * Function MiddlewareException  for project 'SaudiTourism'.
 */
public class MiddlewareException extends Exception {

  /**
   * Constructor.
   */
  public MiddlewareException() {
  }

  /**
   * Constructor.
   *
   * @param message message
   */
  public MiddlewareException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param cause   cause
   */
  public MiddlewareException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause cause
   */
  public MiddlewareException(final Throwable cause) {
    super(cause);
  }
}
