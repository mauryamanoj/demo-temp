package com.saudi.tourism.core.login.models;

import lombok.Data;

/**
 * The type Address.
 */
@Data
public class Address {
  /**
   * The Zip code.
   */
  private String zipCode;

  /**
   * The Formatted address.
   */
  private String formattedAddress;

  /**
   * The Country code.
   */
  private String countryCode;
}
