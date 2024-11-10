package com.saudi.tourism.core.cache;

import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InMemoryCacheTest {

  InMemoryCache cache = new InMemoryCache();

  @Test
  @DisplayName("Test computeIfAbsent method when value is present in the cache")
  void testComputeIfAbsentWhenPresent() {
    Function function = mock(Function.class);
    cache.add(Constants.CODE, Constants.VALUE);
    String value =
        (String) cache.computeIfAbsent(Constants.CODE, function, NumberConstants.TIMEOUT);
    assertEquals(Constants.VALUE, value);
    verify(function, never()).apply(any());
  }

  @Test
  @DisplayName("Test computeIfAbsent method when value is not present in the cache")
  void testComputeIfAbsentWhenIsNotPresent() {
    Function function = mock(Function.class);
    when(function.apply(Constants.CODE)).thenReturn(Constants.VALUE);
    String value =
        (String) cache.computeIfAbsent(Constants.CODE, function, NumberConstants.TIMEOUT);
    assertEquals(Constants.VALUE, value);
    verify(function, times(1)).apply(Constants.CODE);
  }

}
