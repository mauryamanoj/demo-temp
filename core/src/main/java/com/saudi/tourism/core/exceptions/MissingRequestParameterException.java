package com.saudi.tourism.core.exceptions;

/**
 * Thrown to indicate that request misses mandatory parameter.
 */
public class MissingRequestParameterException extends RuntimeException {

  /**
   * Constructs an <code>MissingRequestParameterException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public MissingRequestParameterException(String s) {
    super(s);
  }

}
