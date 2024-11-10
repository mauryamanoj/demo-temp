package com.saudi.tourism.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

/**
 * The interface PlaceholderService.
 */
public interface PlaceholderService {

  /**
   * Filter translation for each key by language and return key-translation map.
   *
   * @param language the value of language
   * @param resourceResolver the value of resourceResolver
   * @return the java.util.Map<java.lang.String,java.lang.String>
   */
  Map<String, String> getKeyTranslationMapByLanguage(String language,
          ResourceResolver resourceResolver);

}
