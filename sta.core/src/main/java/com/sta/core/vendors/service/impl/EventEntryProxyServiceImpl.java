package com.sta.core.vendors.service.impl;

import com.sta.core.vendors.PackageEntryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sta.core.vendors.service.EventEntryProxyService;
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

import static com.saudi.tourism.core.utils.VendorUtils.getVendorNameForEvents;

/**
 * Event entry proxy service.
 */
@Component(service = EventEntryProxyService.class)
@Designate(ocd = EventEntryProxyService.Configuration.class)
@Slf4j
public class EventEntryProxyServiceImpl implements EventEntryProxyService {

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
   * URI for get event entry request.
   */
  private String getEventUri;

  /**
   * URI for post event entry request.
   */
  private String postEventUri;

  /**
   * URI for put event entry request.
   */
  private String putEventUri;

  /**
   * URI for delete event entry request.
   */
  private String deleteEventUri;

  /**
   * URI for get event entry list request.
   */
  private String getEventsUri;

  /**
   * Invoked on configuration change.
   *
   * @param configuration service configuration.
   */
  @Activate @Modified public void init(Configuration configuration) {
    this.authenticationToken = configuration.encodedAuth();
    this.getEventUri = configuration.getEventUri();
    this.postEventUri = configuration.postEventUri();
    this.putEventUri = configuration.putEventUri();
    this.deleteEventUri = configuration.deleteEventUri();
    this.getEventsUri = configuration.getEventsUri();
  }

  /**
   * Get service user's resource resolver.
   *
   * @return resource resolver
   */
  private ResourceResolver getResourceResolver() {
    return PackageEntryUtils.getResourceResolver(userService);
  }

  /**
   * Forward create event entry request to author instance and expose it's result.
   *
   * @param request request
   * @return response message
   * @throws IOException io exception
   */
  public ResponseMessage createEvent(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForEvents(request.getUserPrincipal(), resourceResolver);
      String uri = String.format(postEventUri, dmc);
      return RestHelper.executeMethodPost(uri,
          IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8), authenticationToken,
          null);
    }
  }

  /**
   * Forward event entry request to author instance and expose it's result.
   *
   * @param request request
   * @return response message
   * @throws IOException io e
   */
  @Override public ResponseMessage getEvent(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForEvents(request.getUserPrincipal(), resourceResolver);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      String uri = String.format(this.getEventUri, dmc, name);

      return RestHelper.executeMethodGet(uri, authenticationToken, false);
    }
  }

  /**
   * Forward delete event entry request to author instance and expose it's result.
   *
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override public ResponseMessage deleteEvent(SlingHttpServletRequest request) throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForEvents(request.getUserPrincipal(), resourceResolver);
      String name = PackageEntryUtils.getMandatoryParameter(request, "name");
      String uri = String.format(deleteEventUri, dmc, name);
      LOGGER.info("uri: " + uri);
      return RestHelper.executeMethodDelete(uri, authenticationToken);
    }
  }

  /**
   * Forward update event entry request to author instance and expose it's result.
   *
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override public ResponseMessage createUpdateEvent(final SlingHttpServletRequest request)
      throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForEvents(request.getUserPrincipal(), resourceResolver);
      String name = request.getParameter("name");
      String uri = String.format(putEventUri, dmc, name);
      LOGGER.info("uri: " + uri);
      return RestHelper.executeMethodPut(uri, new InputStreamEntity(request.getInputStream()),
          authenticationToken, false);
    }
  }

  /**
   * Forward update event entry request to author instance and expose it's result.
   *
   * @param request original request
   * @return source system response
   * @throws IOException io e
   */
  @Override public ResponseMessage getEvents(final SlingHttpServletRequest request)
      throws IOException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String dmc = getVendorNameForEvents(request.getUserPrincipal(), resourceResolver);
      LOGGER.info("getEventsUri: " + getEventsUri);
      LOGGER.info("authenticationToken: " + authenticationToken);
      String uri = String.format(getEventsUri, dmc);
      LOGGER.info("getEventsUri uri: " + uri);
      return RestHelper.executeMethodGet(uri, authenticationToken, false);
    }
  }
}
