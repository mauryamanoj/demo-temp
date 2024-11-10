package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.AreaDetailsModel;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;

/**
 * Area details Service.
 */
public interface AreaDetailsService {

  /**
   * Get Area Details.
   *
   * @param request    a {@link SlingHttpServletRequest}
   * @return a list of cards
   * @throws RepositoryException the repository exception
   */
  AreaDetailsModel getAreaDetails(SlingHttpServletRequest request) throws RepositoryException;
}
