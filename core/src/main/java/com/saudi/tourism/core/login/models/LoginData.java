package com.saudi.tourism.core.login.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginData {

  /**
   * The loginUrl.
   */
  private final String loginUrl;

  /**
   * The codeVerifier.
   */
  private final String codeVerifier;

}
