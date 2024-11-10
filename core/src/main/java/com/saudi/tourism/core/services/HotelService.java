package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.hotels.HotelDetail;
import com.saudi.tourism.core.models.components.hotels.HotelsListModel;
import com.saudi.tourism.core.models.components.hotels.HotelsRequestParams;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * This defines all methods to execute business of Packages.
 */
public interface HotelService {

  /**
   * Get list of filtered hotels.
   *
   * @param request    a {@link SlingHttpServletRequest}
   * @param queryParam the query parameter
   * @return a list of feature hotels
   * @throws RepositoryException the repository exception
   */
  HotelsListModel getFilteredPackages(SlingHttpServletRequest request,
                                      HotelsRequestParams queryParam) throws RepositoryException;

  /**
   * Get related of hotels paths. Based in getRelatedPackages but with a different return
   *
   * @param request       a {@link SlingHttpServletRequest}
   * @param language      the language
   * @param isAllExpired  Boolean
   * @param hotelDetail the hotel detail
   * @return a list of hotel app filters items
   * @throws RepositoryException repository exception
   */
  List<String> getRelatedPackagesPaths(SlingHttpServletRequest request, String language,
      Boolean isAllExpired, HotelDetail hotelDetail) throws RepositoryException;
}
