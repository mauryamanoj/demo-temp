package com.saudi.tourism.core.services.impl;

import java.util.Iterator;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.day.crx.JcrConstants;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.MemHolder;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.models.components.tripplan.v1.SearchActivityFilter;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.JcrQueryUtils;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.services.impl.ActivityServiceImpl.SERVICE_DESCRIPTION;

/**
 * Service for requesting activities.
 */
@Component(service = ActivityService.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class ActivityServiceImpl implements ActivityService {

  /**
   * This Service description for OSGi.
   */
  static final String SERVICE_DESCRIPTION = "Activity Service";

  /**
   * The cache for Activities requests.
   */
  @Reference
  private Cache memCache;

  /**
   * The service for obtaining resource resolver.
   */
  @Reference
  private UserService resolverService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  @Generated
  @Override
  public List<Activity> getActivityList(final String language) {
    return getFromCacheOrQueryActivities(language).getList();
  }

  @Override
  public List<Activity> getActivityList(final SearchActivityFilter filter) {
    final List<Activity> allActivities = getActivityList(filter.getLocale());

    final List<Activity> resultList = new LinkedList<>();
    allActivities.stream().filter(activity -> matchFilters(activity, filter))
        .collect(Collectors.toCollection(() -> resultList));

    return resultList;
  }

  @Generated
  @Override
  public Activity getActivityByPath(final String path, final String language) {
    return getActivityByExactPath(CommonUtils.toAbsolutePath(path, language));
  }

  @Override
  public Activity getActivityByExactPath(String path) {
    // Path must be without jcr:content
    path =
        StringUtils.removeEnd(path, Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);

    if (StringUtils.isBlank(path)) {
      LOGGER.warn("Activity path is empty.");
      return null;
    }

    final ActivityHolder holder =
        getFromCacheOrQueryActivities(CommonUtils.getLanguageForPath(path));

    final Activity result = holder.getMap().get(path);
    if (result == null) {
      LOGGER.error("Couldn't find Activity by path {}", path);
    }

    return result;
  }

  /**
   * Checks cache, and if the activities holder is in cache, returns
   * it, otherwise requests a new one using QueryBuilder ({@link #queryActivities(String)}), and
   * stores it into the mem cache.
   *
   * @param language the current locale
   * @return ActivityHolder instance
   */
  @NotNull ActivityServiceImpl.ActivityHolder getFromCacheOrQueryActivities(final String language) {
    final String memCacheKey = Constants.KEY_PREFIX_ACTIVITIES + language;

    ActivityHolder holder =
        (ActivityHolder) memCache.get(memCacheKey);

    if (holder == null) {
      // Get trip plans list using query if couldn't find it in the cache
      holder = queryActivities(language);

      if (CollectionUtils.isNotEmpty(holder.getList())) {
        memCache.add(memCacheKey, holder);
      }
    }

    return holder;
  }

  @Override
  public boolean matchFilters(@NotNull final Activity activity,
      @NotNull final SearchActivityFilter filter) {

    // Check path param
    final String filterActivityPath = filter.getPath();
    if (StringUtils.isNotBlank(filterActivityPath) && !StringUtils
        .startsWith(activity.getPath(), filterActivityPath)) {
      return false;
    }

    // Check city param
    final List<String> filterCity = filter.getCity();
    if (CollectionUtils.isNotEmpty(filterCity)) {
      final String activityCityId = activity.getCityId();
      return StringUtils.isNotBlank(activityCityId) && filterCity
          .contains(AppUtils.stringToID(activityCityId));
    }

    // Check interest param
    final List<String> filterInterests = filter.getInterest();
    if (CollectionUtils.isNotEmpty(filterInterests)) {
      final String[] interests = activity.getInterests();
      return interests != null && Stream.of(interests).anyMatch(filterInterests::contains);
    }

    // Check partner param
    final List<String> filterPartners = filter.getPartner();
    if (CollectionUtils.isNotEmpty(filterPartners)) {
      final String[] partners = activity.getTravelPartner();
      return partners != null && Stream.of(partners).anyMatch(filterPartners::contains);
    }

    return true;
  }

  /**
   * Returns path to the root Activity page (it's always language root for now).
   *
   * @param language language to get proper path
   * @return path to all activities root page
   */
  @Generated
  @NotNull
  public String getPathToActivities(final String language) {
    return CommonUtils.getResourcePath(language, Constants.CONFIG_URL)
        + Constants.ACTIVITIES_PARTIAL;
  }

  /**
   * Queries all existing Activity pages from JCR and produces a list of Activity objects.
   *
   * @param language language for querying
   * @return a list of activities
   */
  @NotNull ActivityHolder queryActivities(final String language) {
    final List<Activity> resultList = new LinkedList<>();
    final Map<String, Activity> resultMap = new HashMap<>();

    try (ResourceResolver resolver = resolverService.getResourceResolver()) {
      final String path = getPathToActivities(language);

      final Map<String, String> queryMap = getQueryMapToSearchActivityPages(path);

      // Ascending sort of the list by path as we use binary search for it
      queryMap.put(Predicate.ORDER_BY, PathPredicateEvaluator.PATH);

      final Query query = queryBuilder
          .createQuery(PredicateGroup.create(queryMap), resolver.adaptTo(Session.class));

      final SearchResult searchResult = query.getResult();

      try {
        CommonUtils.iteratorToStream(searchResult.getResources())
            .forEach(activityPageContentResource -> {
              final Activity activity = activityPageContentResource.adaptTo(Activity.class);
              if (activity == null) {
                LOGGER.error("Adapting activity page {} error, activity will be ignored.",
                    activityPageContentResource.getPath());
              } else {
                resultList.add(activity);
                resultMap.put(activity.getPath(), activity);
              }
            });
      } finally {
        // As explained in https://kiransg.com/2022/03/26/unclosed-resource-resolver-complete-guide-aem/
        // Whenever using AEM queries, there is a ResourceResolver that we should close.
        final Iterator<Resource> resources = searchResult.getResources();
        if (resources.hasNext()) {
          final ResourceResolver resourceResolver = resources.next().getResourceResolver();
          if (resourceResolver != null && resourceResolver.isLive()) {
            resourceResolver.close();
          }
        }
      }

      return new ActivityHolder(ImmutableList.copyOf(resultList),
          ImmutableMap.copyOf(resultMap));
    }
  }


  /**
   * Activities memory holder to be stored into the cache.
   */
  static final class ActivityHolder extends MemHolder<Activity> {

    /**
     * Default constructor with a list and a map.
     *
     * @param list list
     * @param map  map
     */
    ActivityHolder(final List<Activity> list, final Map<String, Activity> map) {
      super(list, map);
    }
  }

  /**
   * Returns query map for searching activities pages. Wrapping of utils method to be able to
   * mock it during unit testing.
   *
   * @param path path to all activities parent page
   * @return prepared map for query builder
   */
  @Generated
  @NotNull Map<String, String> getQueryMapToSearchActivityPages(final String path) {
    return JcrQueryUtils.getQueryMapToSearchPages(path, Constants.RT_ACTIVITY);
  }
}
