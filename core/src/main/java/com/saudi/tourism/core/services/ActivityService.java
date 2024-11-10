package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.models.components.tripplan.v1.SearchActivityFilter;

import java.util.List;

/**
 * All methods for activities service.
 */
public interface ActivityService {

  /**
   * Returns all existing activities for the specified locale. Uses memory cache.
   *
   * @param language the current language
   * @return list of all activity objects
   */
  List<Activity> getActivityList(String language);

  /**
   * Search all activities for the specified filter.
   *
   * @param filter activity filter instance that contains all filtering data
   * @return filtered list of activities
   */
  List<Activity> getActivityList(SearchActivityFilter filter);

  /**
   * Get one activity by path to page or activity id and the current language.
   *
   * @param path     path to the activity page or id to get the activity
   * @param language the current locale
   * @return activity instance
   */
  Activity getActivityByPath(String path, String language);


  /**
   * Get one activity by exact path to page.
   *
   * @param path absolute path to the activity page
   * @return activity instance
   */
  Activity getActivityByExactPath(String path);


  /**
   * Check if one Activity matches the desired filter.
   *
   * @param activity activity instance
   * @param filter   selected filters for activities
   * @return the boolean
   */
  boolean matchFilters(Activity activity, SearchActivityFilter filter);

}
