package com.saudi.tourism.core.predicates;

import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Unit Test for CardsGridComponentPredicate.
 */
class CardsGridComponentPredicateTest {

  @Test
  void getSearchingResourceTypes() {
    assertArrayEquals(new String[] {Constants.CARDS_GRID_RESOURCE_TYPE},
        new CardsGridComponentPredicate().getSearchingResourceTypes());
  }
}
