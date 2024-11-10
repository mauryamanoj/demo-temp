package com.saudi.tourism.core.login;

/**
 * Thrown to indicate that request misses mandatory parameter.
 */
public class SsidException extends RuntimeException {

  /**
   * Constructs an <code>MissingRequestParameterException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public SsidException(String s) {
    super(s);
  }

}
