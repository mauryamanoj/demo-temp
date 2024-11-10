package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * This defines all methods to execute business of Packages.
 */
public interface PackageService {

  /**
   * Get list of filtered packages.
   *
   * @param queryParam the query parameter
   * @return a list of feature packages
   * @throws RepositoryException the repository exception
   */
  PackagesListModel getFilteredPackages(PackagesRequestParams queryParam)
      throws RepositoryException;

  /**
   * Get related of packages paths. Based in getRelatedPackages but with a different return
   *
   * @param language      the language
   * @param isAllExpired  Boolean
   * @param packageDetail the package detail
   * @return a list of package app filters items
   * @throws RepositoryException repository exception
   */
  List<String> getRelatedPackagesPaths(String language,
      Boolean isAllExpired, PackageDetail packageDetail) throws RepositoryException;

  /**
   * Get related of packages paths. Based in getRelatedPackages but with a different return
   *
   * @param request       a {@link SlingHttpServletRequest}
   * @param language      the language
   * @param isAllExpired  Boolean
   * @param packageDetail the package detail
   * @return a list of package app filters items
   * @throws RepositoryException repository exception
   */
  List<PackageDetail> getRelatedPackages(SlingHttpServletRequest request, String language,
      Boolean isAllExpired, PackageDetail packageDetail) throws RepositoryException;
}
