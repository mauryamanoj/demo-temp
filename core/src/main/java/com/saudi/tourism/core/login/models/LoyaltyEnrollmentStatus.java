package com.saudi.tourism.core.login.models;

/**
 * LoyaltyEnrollmentStatus.
 */
public enum LoyaltyEnrollmentStatus {
  /**
   * Status returned if the user is already enrolled.
   */
  ALREADY_ENROLLED,
  /**
   * Status returned if SSID successfully enrolled the user.
   */
  SUCCESSFULLY_ENROLLED,
  /**
   * Status returned if SSID unsuccessfully enrolled the user (technical issue mainly).
   */
  UNSUCCESSFULLY_ENROLLED;
}
