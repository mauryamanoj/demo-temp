package com.sta.core;

/**
 * Common exception for project 'SaudiTourism'.
 */
public class MmCoreException extends Exception {

  /**
   * Constructor.
   */
  public MmCoreException() {
  }

  /**
   * Constructor.
   *
   * @param message message
   */
  public MmCoreException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param cause   cause
   */
  public MmCoreException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause cause
   */
  public MmCoreException(final Throwable cause) {
    super(cause);
  }
}
