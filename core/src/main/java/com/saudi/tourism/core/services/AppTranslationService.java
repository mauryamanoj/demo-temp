package com.saudi.tourism.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

/**
 * The service AppTranslationService.
 */
public interface AppTranslationService {

  /**
   * Loop all i 18 keys.
   *
   * @param target   target
   * @param resolver resolver
   * @param locale   locale
   * @return map map
   */
  Map<String, String> loopAllI18keys(ResourceResolver resolver, String target,
                                     String locale);

}
