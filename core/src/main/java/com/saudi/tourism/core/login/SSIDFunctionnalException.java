package com.saudi.tourism.core.login;

/**
 * Function SSIDFunctionnalException  for project 'SaudiTourism'.
 */
public class SSIDFunctionnalException extends Exception {

  /**
   * Constructor.
   */
  public SSIDFunctionnalException() {
  }

  /**
   * Constructor.
   *
   * @param message message
   */
  public SSIDFunctionnalException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param cause   cause
   */
  public SSIDFunctionnalException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param cause cause
   */
  public SSIDFunctionnalException(final Throwable cause) {
    super(cause);
  }
}
