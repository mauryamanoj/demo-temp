package com.sta.core.scene7;

/**
 * Dynamic Media Asset Upload Config.
 */
public interface DMAssetUploadConfig {

  /**
   * Get Dynamic Media Server UGC upload platform url.
   * @return company name.
   */
  String getUgcAssetUploadUrl();
  /**
   * Get Dynamic Media user.
   * @return company name.
   */
  String getUser();
  /**
   * Get Dynamic Media password.
   * @return password.
   */
  String getPassword();

  /**
   * Get Company Name for Dynamic Media Server.
   * @return company name.
   */
  String getCompanyName();

  /**
   * Get Company Name for Dynamic Media Server.
   * @return company name.
   */
  String getDmSecretKey();

  /**
   * Token Expires time in seconds.
   * @return token expiry time
   */
  String getTokenExpires();
}
