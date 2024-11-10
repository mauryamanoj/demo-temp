package com.saudi.tourism.core.services.seasons.v1;

import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;

import java.util.List;

/**
 * Seasons CF Service.
 */
public interface SeasonsCFService {
  /**
   * Fetch all Season CFs authored for a locale.
   *
   * @param locale Current locale
   * @return list of Season CF
   */
  List<SeasonCFModel> fetchAllSeasons(String locale);

  /**
   * Fetch Season by id for a locale.
   *
   * @param locale Current locale
   * @param seasonId seasonId
   * @return Season
   */
  SeasonCFModel getSeasonById(String locale, String seasonId);
}
