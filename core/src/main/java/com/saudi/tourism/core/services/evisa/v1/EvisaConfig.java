package com.saudi.tourism.core.services.evisa.v1;

/**
 *Interface of Saudi Tourism Evisa Config.
 */
public interface EvisaConfig {
  /**
   * eVisa Config path.
   *
   * @return evisa Config Path
   */
  String getEVisaConfigPath();

  /**
   * eVisa Countries Groups config path.
   *
   * @return eVisa Countries Groups config path
   */
  String getEVisaCountriesGroupsConfigPath();
}
