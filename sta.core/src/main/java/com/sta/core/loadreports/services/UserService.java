package com.sta.core.loadreports.services;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Common service provide utilities for accessing the JCR and other methods,
 * All service should extends this for accessibility convenient.
 */
public interface UserService {
  /**
   * Get Resource resolver from subservice.
   *
   * @param userMap the user map
   * @return resource resolver
   */
  ResourceResolver getResourceResolver(Map<String, Object> userMap);

  /**
   * Get writeservice resource resolver object.
   *
   * @return resource resolver object or null if cannot create
   */
  ResourceResolver getWritableResourceResolver();

}
