package com.sta.core.vendors.service;

import java.io.IOException;

import com.saudi.tourism.core.models.common.ResponseMessage;

import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * .
 */
public interface PackageEntryProxyService {

  /**
   * Forward get package entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io e
   */
  ResponseMessage getPackage(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward add package entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  ResponseMessage createPackage(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward delete package entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  ResponseMessage deletePackage(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward update package entry request to author instance and expose it's result.
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  ResponseMessage createupdatePackage(SlingHttpServletRequest request) throws IOException;

  /**
   * Forward update package entry request to author instance and expose it's result.
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  ResponseMessage getPackages(SlingHttpServletRequest request) throws IOException;

  /**
   * Service configuration.
   */
  @ObjectClassDefinition(name = "Saudi PackageEntryProxyService Configuration") @interface Configuration {

    /**
     * URI for get package entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Author instance user",
                         type = AttributeType.STRING)
    String encodedAuth();

    /**
     * URI for get package entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Get package entry API uri",
                         defaultValue = "/bin/api/v1/entry/package?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String getPackageUri();

    /**
     * URI for post package entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Post package entry API uri",
                         defaultValue = "/bin/api/v1/entry/package?dmc=%s",
                         type = AttributeType.STRING)
    String postPackageUri();

    /**
     * URI for post package entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Put package entry API uri",
                         defaultValue = "/bin/api/v1/entry/package?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String putPackageUri();

    /**
     * URI for delete package entry request.
     * @return URI
     */
    @AttributeDefinition(name = "Put package entry API uri",
                         defaultValue = "/bin/api/v1/entry/package?dmc=%s&name=%s",
                         type = AttributeType.STRING)
    String deletePackageUri();

    /**
     * URI for get package entries list request.
     * @return URI
     */
    @AttributeDefinition(name = "Get package entry list API uri",
                         defaultValue = "/bin/api/v1/entry/package/list?dmc=%s",
                         type = AttributeType.STRING)
    String getPackagesUri();
  }

}
