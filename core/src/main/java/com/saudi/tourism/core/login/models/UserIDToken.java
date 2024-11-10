package com.saudi.tourism.core.login.models;

import lombok.Data;

/**
 * The type User IDToken.
 */
@Data
public class UserIDToken {
  /**
   * The Token.
   */
  private String token;

  /**
   * The User ID.
   */
  private String user;

  /**
   * The User locale.
   */
  private String locale;

}
