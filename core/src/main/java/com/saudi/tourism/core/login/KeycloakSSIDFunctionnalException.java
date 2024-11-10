package com.saudi.tourism.core.login;

/**
 * Function KeycloakSSIDFunctionnalException  for project 'SaudiTourism'.
 */
public class KeycloakSSIDFunctionnalException extends Exception {

  /**
   * code.
   */
  private final String code;

  /**
   * Constructor.
   *
   * @param code code
   */
  public KeycloakSSIDFunctionnalException(String code) {
    super();
    this.code = code;
  }

  /**
   * Constructor.
   *
   * @param code    code
   * @param message message
   */
  public KeycloakSSIDFunctionnalException(String message, String code) {
    super(message);
    this.code = code;
  }

  /**
   * Constructor.
   *
   * @param message message
   * @param code    code
   * @param cause   cause
   */
  public KeycloakSSIDFunctionnalException(String message, String code, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  /**
   * Constructor.
   *
   * @param code  code
   * @param cause cause
   */
  public KeycloakSSIDFunctionnalException(String code, final Throwable cause) {
    super(cause);
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }
}
