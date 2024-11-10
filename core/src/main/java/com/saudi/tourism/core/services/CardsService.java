package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;

/**
 * This defines all methods to execute for cards.
 */
public interface CardsService {

  /**
   * Get list of filtered cards.
   *
   * @param request    a {@link SlingHttpServletRequest}
   * @param queryParam the query parameter
   * @param version the version
   * @return a list of cards
   * @throws RepositoryException the repository exception
   */
  CardsListModel getFilteredCards(SlingHttpServletRequest request,
                                  CardsRequestParams queryParam,
                                  String version) throws RepositoryException;
}
