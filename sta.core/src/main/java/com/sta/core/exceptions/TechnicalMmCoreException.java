package com.sta.core.exceptions;

/**
 * Technical exception for project 'SaudiTourism'.
 */
public class TechnicalMmCoreException extends Exception {

  /**
   * Constructor.
   */
  public TechnicalMmCoreException() {
  }

  /**
   * Constructor.
   *
   * @param message message
   */
  public TechnicalMmCoreException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param cause   cause
   */
  public TechnicalMmCoreException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause cause
   */
  public TechnicalMmCoreException(final Throwable cause) {
    super(cause);
  }
}
