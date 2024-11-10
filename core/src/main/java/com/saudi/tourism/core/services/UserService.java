package com.saudi.tourism.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

/**
 * Common service provide utilities for accessing the JCR and other methods,
 * All service should extends this for accessibility convenient.
 */
public interface UserService {

  /**
   * Get readService Resource resolver.
   *
   * @return resource resolver
   */
  ResourceResolver getResourceResolver();

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
  /**
   * Get writeservice resource resolver object.
   *
   * @return resource resolver object or null if cannot create
   */
  ResourceResolver getWorkflowResourceResolver();
}
