package com.saudi.tourism.core.login.models;

import lombok.Builder;
import lombok.Getter;

/**
 * Loyalty enrollment response.
 */
@Getter
@Builder
public class LoyaltyEnrollmentResponse {
  /**
   * Enrollment status.
   */
  private LoyaltyEnrollmentStatus status;

  /**
   * Human-readable Message.
   */
  private String message;
}
