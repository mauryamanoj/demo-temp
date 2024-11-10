package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.app.entertainer.AppGame;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * The service Gameservice.
 */
public interface GameService {

  /**
   * Get all games .
   * @param locale used
   * @param resolver Resource resolver
   * @param filterStatus status to filter
   * @return Games returned from aem
   * */
  List<AppGame> getAllGames(ResourceResolver resolver, String locale, String filterStatus);

}
