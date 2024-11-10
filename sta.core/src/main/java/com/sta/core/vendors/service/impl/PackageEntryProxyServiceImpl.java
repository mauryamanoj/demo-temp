package com.sta.core.vendors.service.impl;

import com.sta.core.vendors.PackageEntryUtils;
import com.sta.core.vendors.service.PackageEntryProxyService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.RestHelper;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import static com.saudi.tourism.core.utils.VendorUtils.getVendorNameForPackages;

/**
 * Package entry proxy service.
 */
@Component(service = PackageEntryProxyService.class)
@Designate(ocd = PackageEntryProxyService.Configuration.class)
@Slf4j
public class PackageEntryProxyServiceImpl implements PackageEntryProxyService {

  /**
   * User service.
   */
  @Reference
  private UserService userService;

  /**
   * Authentication token.
   */
  private String authenticationToken;

  /**
   * URI for get package entry request.
   */
  private String getPackageUri;

  /**
   * URI for post package entry request.
   */
  private String postPackageUri;

  /**
   * URI for put package entry request.
   */
  private String putPackageUri;

  /**
   * URI for delete package entry request.
   */
  private String deletePackageUri;

  /**
   * URI for get package entry list request.
   */
  private String getPackagesUri;

  /**
   * Invoked on configuration change.
   * @param configuration service configuration.
   */
  @Activate
  @Modified
  public void init(Configuration configuration) {
    this.authenticationToken = configuration.encodedAuth();
    this.getPackageUri = configuration.getPackageUri();
    this.postPackageUri = configuration.postPackageUri();
    this.putPackageUri = configuration.putPackageUri();
    this.deletePackageUri = configuration.deletePackageUri();
    this.getPackagesUri = configuration.getPackagesUri();
  }

  /**
   * Get service user's resource resolver.
   * @return resource resolver
   */
  private ResourceResolver getResourceResolver() {
    return PackageEntryUtils.getResourceResolver(userService);
  }

  /**
   * Forward create package entry request to author instance and expose it's result.
   * @param request request
   * @throws IOException io exception
   * @return response message
   */
  public ResponseMessage createPackage(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForPackages(request.getUserPrincipal(), resourceResolver);
      String uri = String.format(postPackageUri, dmc);
      return RestHelper.executeMethodPost(uri, IOUtils.toString(request.getInputStream(),
          StandardCharsets.UTF_8), authenticationToken, null);
    }
  }

  /**
   * Forward package entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io e
   */
  @Override
  public ResponseMessage getPackage(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForPackages(request.getUserPrincipal(), resourceResolver);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      String uri = String.format(this.getPackageUri, dmc, name);

      return RestHelper.executeMethodGet(uri, authenticationToken, false);
    }
  }

  /**
   * Forward delete package entry request to author instance and expose it's result.
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override
  public ResponseMessage deletePackage(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForPackages(request.getUserPrincipal(), resourceResolver);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      String uri = String.format(deletePackageUri, dmc, name);
      LOGGER.info("uri: " + uri);
      return RestHelper.executeMethodDelete(uri, authenticationToken);
    }
  }

  /**
   * Forward update package entry request to author instance and expose it's result.
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override public ResponseMessage createupdatePackage(final SlingHttpServletRequest request)
      throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForPackages(request.getUserPrincipal(), resourceResolver);
      String name = request.getParameter("name");
      String uri = String.format(putPackageUri, dmc, name);
      LOGGER.info("uri: " + uri);
      return RestHelper.executeMethodPut(uri, new InputStreamEntity(request.getInputStream()),
          authenticationToken, false);
    }
  }

  /**
   * Forward update package entry request to author instance and expose it's result.
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override public ResponseMessage getPackages(final SlingHttpServletRequest request)
      throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForPackages(request.getUserPrincipal(), resourceResolver);
      String uri = String.format(getPackagesUri, dmc);
      return RestHelper.executeMethodGet(uri, authenticationToken, false);
    }
  }
}
