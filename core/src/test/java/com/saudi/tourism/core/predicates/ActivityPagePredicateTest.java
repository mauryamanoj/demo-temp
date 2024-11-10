package com.saudi.tourism.core.predicates;

import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Unit Test for ActivityPagePredicate.
 */
class ActivityPagePredicateTest {

  @Test
  void getSearchingResourceTypes() {
    assertArrayEquals(new String[] {Constants.RT_ACTIVITY},
        new ActivityPagePredicate().getSearchingResourceTypes());
  }
}
