package com.saudi.tourism.core.services.geo.v1;

import java.util.List;

/** Countries Service. */
public interface CountriesService {
  /**
   * Fetch list of countries of the world.
   * Authored under /content/sauditourism/<locale>//country-list/jcr:content/root/responsivegrid/countrylist
   * @param locale Current Locale
   * @return List of countries
   */
  List<Country> fetchListOfCountries(String locale);
}
