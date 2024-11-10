package com.saudi.tourism.core.predicates;

import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.collections.Predicate;
import org.osgi.service.component.annotations.Component;

/**
 * Predicate that filters only trip packages for coral path fields & other search functionality.
 */
@Component(service = Predicate.class,
           property = {
               "predicate.name" + Constants.EQUAL + CardsGridComponentPredicate.PREDICATE_NAME})
public class CardsGridComponentPredicate extends AbstractResourceTypePredicate {

  /**
   * Resource types array to search for this predicate.
   */
  static final String[] RT_TO_SEARCH = {Constants.CARDS_GRID_RESOURCE_TYPE};

  /**
   * This predicate name.
   */
  static final String PREDICATE_NAME = "cards-grid-component";

  /**
   * Construct differently to search for components.
   */
  public CardsGridComponentPredicate() {
    setSearchComponents(true);
  }

  @Override String[] getSearchingResourceTypes() {
    return RT_TO_SEARCH;
  }
}


