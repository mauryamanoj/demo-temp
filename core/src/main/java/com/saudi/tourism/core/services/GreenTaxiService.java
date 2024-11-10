package com.saudi.tourism.core.services;


import com.saudi.tourism.core.models.components.greenTaxi.GreenTaxiModel;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;

/**
 * This defines all methods to execute business of green taxi.
 */
public interface GreenTaxiService {

  /**
   * Get list of green taxi cards data.
   *
   * @param request    a {@link SlingHttpServletRequest}
   * @param locale the query parameter locale.
   * @return a list of feature hotels
   * @throws RepositoryException the repository exception
   */
  GreenTaxiModel getData(SlingHttpServletRequest request,
                         String locale) throws RepositoryException;

}
