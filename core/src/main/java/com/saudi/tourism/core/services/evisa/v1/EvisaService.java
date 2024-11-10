package com.saudi.tourism.core.services.evisa.v1;

import com.saudi.tourism.core.models.common.CountriesGroupsListConfigModel;
import com.saudi.tourism.core.models.components.evisa.v1.EvisaConfigModel;
import com.saudi.tourism.core.models.mobile.components.EvisaMobileConfigModel;

import java.util.List;

/**
 * Evisa Service.
 */
public interface EvisaService {
  /**
   * Returns the filtered evisa config for a locale.
   *
   * @param locale Current locale.
   * @param visaGroup visaGroup to filter.
   * @param questionsOnly to filter only Questions
   * @param visaDetailsOnly to filter only visaDetails
   * @param responseCode to filter onlyQuestions
   * @param platform to filter platform
   *
   * @return eVisa filtered config for the locale.
   */
  EvisaConfigModel fetchFilteredEvisaConfig(String locale, String visaGroup, boolean questionsOnly,
                                            boolean visaDetailsOnly, String responseCode, String platform);

  /**
   * Returns the filtered evisa config for a locale for Mobile.
   *
   * @param locale          Current locale.
   * @param visaGroup       visaGroup to filter.
   * @param responseCode    to filter onlyQuestions
   * @return eVisa filtered config for the locale.
   */
  EvisaMobileConfigModel fetchMobileFilteredEvisaConfig(String locale, String visaGroup, String responseCode);

  /**
   * Returns the evisa config for a locale.
   *
   * @param locale Current locale.
   *
   * @return eVisa config for the locale.
   */
  EvisaConfigModel fetchEvisaConfig(String locale);

  /**
   * Fecthes the countries groups authored under /content/sauditourism/en/Configs/admin.
   * @param language language
   * @return List of countries groups
   */
  List<CountriesGroupsListConfigModel.CountryGroupModel> getCountriesGroups(String language);


}
