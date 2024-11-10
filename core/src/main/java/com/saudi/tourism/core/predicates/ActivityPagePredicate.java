package com.saudi.tourism.core.predicates;

import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.osgi.service.component.annotations.Component;

/**
 * Predicate that filters only activities for coral pathfields.
 */
@Component(service = Predicate.class,
           property = {"predicate.name" + Constants.EQUAL + ActivityPagePredicate.PREDICATE_NAME})
public class ActivityPagePredicate extends AbstractResourceTypePredicate {

  /**
   * Name for this predicate.
   */
  static final String PREDICATE_NAME = Constants.ACTIVITY;

  /**
   * The predicate searches activity pages.
   *
   * @return list of resource types to search
   * @see Constants#RT_ACTIVITY
   */
  @Override
  String[] getSearchingResourceTypes() {
    return new String[] {Constants.RT_ACTIVITY};
  }

  @Override
  protected String[] getSkippedResourceTypes() {
    // Do not skip /Configs as activities are placed in configs
    return ArrayUtils.toArray();
  }
}


