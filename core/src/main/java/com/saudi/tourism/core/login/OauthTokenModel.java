package com.saudi.tourism.core.login;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Oauth token model.
 */
@Data
@Builder
public class OauthTokenModel implements Serializable {

  /**
   * The Client id.
   */
  @SerializedName("client_id")
  private String clientId;
  /**
   * The Client secret.
   */
  @SerializedName("client_secret")
  private String clientSecret;
  /**
   * The Audience.
   */
  private String audience;
  /**
   * The Grant type.
   */
  @SerializedName("grant_type")
  private String grantType;
}
