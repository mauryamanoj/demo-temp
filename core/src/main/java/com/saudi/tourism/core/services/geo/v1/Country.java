package com.saudi.tourism.core.services.geo.v1;

import lombok.Builder;
import lombok.Value;

/**
 * Class representing a country.
 * As configured under /content/sauditourism/<locale>//country-list/jcr:content/root/responsivegrid/countrylist.
 */
@Value
@Builder
public class Country {
  /**
   * Country Name.
   */
  private String countryName;

  /**
   * The path to the flag icon.
   */
  private String flag;

  /**
   * Visa Group of the country.
   * Eg : gcc, eligibleplus, ...
   */
  private String visaGroup;
}
