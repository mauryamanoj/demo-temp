package com.saudi.tourism.core.listeners;

import com.day.cq.wcm.api.LanguageManager;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.servlets.app.AppCityServlet;
import com.saudi.tourism.core.servlets.app.AppRegionServlet;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;

/**
 * Handle change event listener
 * <p>
 * ResourceChangeListener.PATHS: Filter the notifications by path in the content repository
 * ResourceChangeListener.CHANGES: Type of change you want to listen to.
 */
@Component(service = ResourceChangeListener.class,
           property = {ResourceChangeListener.PATHS + Constants.EQUAL + Constants.ROOT_CONTENT_PATH,
               ResourceChangeListener.CHANGES + Constants.EQUAL + Constants.CHANGED,
               ResourceChangeListener.CHANGES + Constants.EQUAL + Constants.ADDED,
               ResourceChangeListener.CHANGES + Constants.EQUAL + Constants.REMOVED})
@Slf4j
public class SaudiEventListener implements ResourceChangeListener {

  /**
   * The Language Manager.
   */
  @Reference
  private LanguageManager languageManager;

  /**
   * Resource type to cache key mapping.
   */
  private static final Map<String, String> RT_CACHE_KEY_MAP = ImmutableMap.<String, String>builder()
      // @formatter:off
      .put(Constants.HOTELS_RES_TYPE, Constants.HOTELS_CACHE_KEY)
      .put(Constants.PACKAGE_DETAIL_RES_TYPE, Constants.PACKAGES_CACHE_KEY)
      .put(Constants.EVENT_DETAIL_RES_TYPE, Constants.EVENTS_CACHE_KEY)
      .put(Constants.PLACEHOLDERS_RES_TYPE, Constants.PLACEHOLDERS_CACHE_KEY)
      .put(Constants.RT_TRIP_PLAN_PAGE, Constants.KEY_PREFIX_TRIP_PLANS)
      .put(Constants.RT_TRIP_DETAIL, Constants.KEY_PREFIX_TRIP_PLANS)
      .put(Constants.RT_TRIP_DAY, Constants.KEY_PREFIX_TRIP_PLANS)
      .put(Constants.RT_ADMIN_HOLIDAYS, Constants.KEY_PREFIX_ADMIN_HOLIDAYS)
      .put(Constants.RT_ACTIVITY, Constants.KEY_PREFIX_ACTIVITIES)
      .put(Constants.RT_CITY_COMPONENT, Constants.EXT_CITY_MEM_CACHE_KEY_PREFIX)
      .put(Constants.RT_APP_CITY_PAGE, AppCityServlet.MEM_CACHE_KEY_PREFIX)
      .put(Constants.RT_APP_REGION_PAGE, AppRegionServlet.MEM_CACHE_KEY_PREFIX)
      .put(Constants.GREEN_TAXI_RES_TYPE, Constants.GREEN_TAXI_CACHE_KEY)
      // @formatter:on
      .build();

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * The Cache.
   */
  @Reference
  private Cache cache;

  @Override
  public void onChange(@NotNull List<ResourceChange> list) {
    // Get ResourceResolver
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      for (ResourceChange change : list) {
        LOGGER.debug("Processing change event: {}", change);

        String path = change.getPath();
        String language = CommonUtils.getLanguageForPath(path);

        // Get resource by path
        Resource resource = resolver.getResource(path);

        if (resource == null) {
          // Resource is null, so we have only path (node was removed)
          flushCacheByPath(language, path, resolver);

        } else {
          // Resource is not null - flush by its resource type
          processExistingResource(resolver, path, language, resource);
        }
      }
    } catch (Exception e) {
      LOGGER.error("[onChange] Cache flushing error: {}", e.getMessage(), e);
    }
  }

  /**
   * Process exxisting resource.
   * @param resolver resolver
   * @param path resource path
   * @param language language
   * @param resource resource
   */
  private void processExistingResource(ResourceResolver resolver, String path, String language,
      Resource resource) {
    flushCacheByResourceType(language, resource.getResourceType(), resolver);

    if (path.contains(Constants.CONFIGS)) {
      flushHeaderCache(language);
    }
    if (path.contains(Constants.IMAGE_PROFILE_BASE_PATH)) {
      flushImageProfileMap();
    }

    // Admin options
    if (path.startsWith(StringUtils.replaceEach(Constants.ADMIN_OPTIONS_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language}))) {
      cache.remove(Constants.KEY_PREFIX_ADMIN_OPTIONS + language);
      flushCacheByResourceType(language, Constants.PACKAGE_DETAIL_RES_TYPE, resolver);
    }
    // Admin alerts
    if (path.startsWith(StringUtils.replaceEach(Constants.ADMIN_ALERT_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language}))) {
      cache.remove(Constants.KEY_PREFIX_ADMIN_ALERTS + language);
    }

    // Package settings
    if (path.startsWith(StringUtils.replaceEach(Constants.PACKAGE_SETTINGS_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language}))) {
      cache.remove(Constants.KEY_PREFIX_PACKAGE_SETTINGS + language);
    }
  }

  /**
   * Flush Dynamic Media Profile Cache.
   */
  private void flushImageProfileMap() {
    cache.remove(Constants.DYNAMIC_MEDIA_PROFILES);
  }

  /**
   * Flush cache by path if resource type can't be extracted (e.g. when resource is deleted).
   *
   * @param language language (language part from the path)
   * @param path     path to resource
   * @param resolver read only resource resolver for getting admin options
   */
  private void flushCacheByPath(final String language, final String path,
      final ResourceResolver resolver) {
    // Paths are checking using adminOptions because they can be changed in admin config page
    AdminPageOption adminOptions = AdminUtil.getAdminOptions(language, StringUtils.EMPTY);

    // StringUtils.contains is used because any path can be null in adminOptions
    if (StringUtils.contains(path, adminOptions.getEventsPath())) {
      flushCacheByResourceType(language, Constants.EVENT_DETAIL_RES_TYPE, resolver);

    } else if (StringUtils.contains(path, adminOptions.getPackagesPath())) {
      flushCacheByResourceType(language, Constants.PACKAGE_DETAIL_RES_TYPE, resolver);

    } else if (StringUtils.startsWith(path, Constants.GLOBAL_CONFIG_PATH)) {
      // This resource was inside global-configs node

      // Extended cities data
      if (path.startsWith(Constants.GLOBAL_CITIES_EXT_DATA_CONTENT)) {
        flushCacheByResourceType(language, Constants.RT_CITY_COMPONENT, resolver);
      }

    } else if (StringUtils.contains(path, Constants.CONFIGS)) {
      // This resource is inside Configs node

      if (StringUtils.contains(path, adminOptions.getHeaderPath())) {
        flushHeaderCache(language);
      }
      if (StringUtils.contains(path, Constants.HOLIDAYS_PARTIAL)) {
        flushCacheByResourceType(language, Constants.RT_ADMIN_HOLIDAYS, resolver);
      }

    } else if (StringUtils.contains(path, Constants.IMAGE_PROFILE_BASE_PATH)) {
      flushImageProfileMap();
    }

    // Process trip plans cache & activities cache
    if (StringUtils.startsWith(path, adminOptions.getTripPlansPath())) {
      // Clean trip plans cache if any trip plan page is deleted
      flushCacheByResourceType(language, Constants.RT_TRIP_PLAN_PAGE, resolver);
    } else if (StringUtils.startsWith(path, adminOptions.getActivitiesPath())) {
      // Clean activities & trip plan cache if any activity or trip plan resource is deleted
      flushCacheByResourceType(language, Constants.RT_ACTIVITY, resolver);
    }
  }

  /**
   * Handle change event.
   *
   * @param language     the language
   * @param resourceType the resource type
   * @param resolver     the resource resolver
   */
  private void flushCacheByResourceType(String language, @NotNull String resourceType,
      @NotNull final ResourceResolver resolver) {
    final String cacheKey = RT_CACHE_KEY_MAP.get(resourceType);

    if (StringUtils.isNotBlank(cacheKey)) {
      switch (resourceType) {
        case Constants.ACTIVITY:
          // Activity is stored in trip plan (not by reference), so if activity is changed, clean
          // activities cache along with trip plans cache
          cache.remove(Constants.KEY_PREFIX_TRIP_PLANS + language);
          cache.remove(cacheKey + language);
          break;
        case Constants.RT_CITY_COMPONENT:
          // If city extended data was edited - clean it in every language
          languageManager.getCqLanguages(resolver,
              Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
                  + Constants.DEFAULT_LOCALE)
              .forEach(lang -> cache.remove(cacheKey + lang.getLanguageCode()));
          break;
        case Constants.PLACEHOLDERS_RES_TYPE:
          // Only for placeholders (there's no language in key)
          cache.remove(cacheKey);
          break;
        case Constants.EVENT_DETAIL_RES_TYPE:
          // Events have two cache keys: default with language & with expired suffix
          cache.remove(cacheKey + language + "-expired");
          cache.remove(cacheKey + language);
          break;
        default:
          // Common notion - for all other caches
          cache.remove(cacheKey + language);
      }
    }
  }

  /**
   * Handle change event.
   *
   * @param language the language
   */
  private void flushHeaderCache(String language) {
    cache.remove(Constants.HEADER_KEY + Constants.MINUS + language);
  }
}
