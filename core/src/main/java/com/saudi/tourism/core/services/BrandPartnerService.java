package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.brands.BrandDetail;
import com.saudi.tourism.core.models.components.brands.BrandListModel;
import com.saudi.tourism.core.models.components.brands.BrandRequestParams;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;

/**
 * This defines all methods to execute business of Brands .
 */
public interface BrandPartnerService {

  /**
   * Get list of filtered events.
   * @param request request.
   * @param queryParam the query parameter
   * @return a list of brand partners
   * @throws RepositoryException the repository exception
   */
  BrandListModel getFilteredBrands(SlingHttpServletRequest request,
                                   BrandRequestParams queryParam) throws RepositoryException;

  /**
   * Get related of hotels paths. Based in getRelatedPackages but with a different return
   *
   * @param request       a {@link SlingHttpServletRequest}
   * @param queryParam query param.
   * @param brandDetail the hotel detail
   * @return a list of hotel app filters items
   * @throws RepositoryException repository exception
   */
  List<String> getRelatedPackagesPaths(SlingHttpServletRequest request, BrandRequestParams queryParam,
                                       BrandDetail brandDetail) throws RepositoryException;

  /**
   *
   * @param request request.
   * @param queryParam queryParam.
   * @return brandlist map.
   * @throws RepositoryException exception.
   */
  Map<String, BrandListModel> getHomePageItems(SlingHttpServletRequest request,
                                               BrandRequestParams queryParam) throws RepositoryException;

}
