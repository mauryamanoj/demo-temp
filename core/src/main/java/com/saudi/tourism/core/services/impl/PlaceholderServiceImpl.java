package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.PlaceholderKey;
import com.saudi.tourism.core.models.components.PlaceholderModel;
import com.saudi.tourism.core.models.components.PlaceholderTranslation;
import com.saudi.tourism.core.services.PlaceholderService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saudi.tourism.core.utils.SpecialCharConstants.FORWARD_SLASH;

/**
 * The Class PlaceholderServiceImpl.
 */
@Component(immediate = true, service = PlaceholderService.class)
public class PlaceholderServiceImpl implements PlaceholderService {

  /**
   * The constant PLACEHOLDER_ADMIN_BASE_PATH.
   */
  private static final String PLACEHOLDER_ADMIN_BASE_PATH
          = Constants.ROOT_CONTENT_PATH + FORWARD_SLASH + "global-configs/Configs/admin/"
          + JcrConstants.JCR_CONTENT + "/placeholder";

  /**
   * The Cache.
   */
  @Reference
  private Cache cache;

  /**
   * Filter translation for each key by language and return key-translation map.
   *
   * @param language the value of language
   * @param resourceResolver the value of resourceResolver
   * @return the java.util.Map<java.lang.String,java.lang.String>
   */
  @Override
  public Map<String, String> getKeyTranslationMapByLanguage(String language,
          ResourceResolver resourceResolver) {

    Map<String, String> placeholderMap = new HashMap<>();

    PlaceholderModel cachedPlaceholderModel = (PlaceholderModel) cache.get(
            Constants.PLACEHOLDERS_CACHE_KEY);

    if (cachedPlaceholderModel == null) {

      cachedPlaceholderModel = resourceResolver
              .resolve(PLACEHOLDER_ADMIN_BASE_PATH)
              .adaptTo(PlaceholderModel.class);

      cache.add(Constants.PLACEHOLDERS_CACHE_KEY, cachedPlaceholderModel);

    }

    List<PlaceholderKey> placeholderKeys = cachedPlaceholderModel.getKeys();

    if (placeholderKeys != null) {
      placeholderKeys.forEach(key -> {

        String translation = getTranslationByLanguage(
                key.getTranslations(), language);

        if (StringUtils.isNotBlank(translation)) {
          placeholderMap.put(
                  key.getText(),
                  translation);
        }

      });
    }

    return placeholderMap;
  }

  /**
   * Get translation by language.If none found, get default language translation
   * ("en"). If none found, return empty.
   *
   * @param translations the value of translations
   * @param language the value of language
   * @return the java.lang.String
   */
  private String getTranslationByLanguage(
          List<PlaceholderTranslation> translations,
          String language) {

    return translations.stream()
            .filter(translation -> translation.getLanguage().equals(
            language))
            .map(PlaceholderTranslation::getTranslation)
            .findFirst()
            .orElse(getDefaultTranslation(translations));
  }

  /**
   * Get default translation ("en").If none found, return empty.
   *
   * @param translations the value of translations
   * @return the java.lang.String
   */
  private String getDefaultTranslation(
          List<PlaceholderTranslation> translations) {
    return translations.stream()
            .map(PlaceholderTranslation::getTranslation)
            .filter(Constants.DEFAULT_LOCALE::equals)
            .findFirst()
            .orElse(StringUtils.EMPTY);
  }
}
