package com.saudi.tourism.core.cache;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Memory Holder class to store both a list and a map into the memory cache as one instance.
 *
 * @param <T> the type of elements to be stored in the holder (list and map)
 */
@Getter
public class MemHolder<T> {

  /**
   * list.
   */
  private final List<T> list;

  /**
   * map.
   */
  private final Map<String, T> map;

  /**
   * Default constructor with a list and a map.
   *
   * @param list list
   * @param map  map
   */
  public MemHolder(@NotNull final List<T> list, @NotNull final Map<String, T> map) {
    this.list = list;
    this.map = map;
  }
}
