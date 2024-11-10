package com.saudi.tourism.core.cache;

import java.util.function.Function;

/**
 * The interface Cache.
 */
public interface Cache {

  /**
   * adds the object value into cache with key as identifier for periodInMillis period.
   *
   * @param key            the key
   * @param value          the value
   * @param periodInMillis the period in millis
   */
  void add(String key, Object value, long periodInMillis);

  /**
   * adds the object value into cache with key as identifier for 1 day.
   *
   * @param key   the key
   * @param value the value
   */
  void add(String key, Object value);

  /**
   * removes an item from cache.
   *
   * @param key the key
   */
  void remove(String key);

  /**
   * return cache object for given identifier.
   *
   * @param key the key
   * @return object
   */
  Object get(String key);

  /**
   * resets the cache.
   */
  void clear();

  /**
   * returns the size of cache.
   *
   * @return long
   */
  long size();

  /**
   * compute and put value if present, return existing value otherwise.
   * @param key key
   * @param mappingFunction function
   * @param periodInMillis period in milliseconds
   * @return either existing or computed value
   */
  Object computeIfAbsent(String key, Function<String, ?> mappingFunction, long periodInMillis);
}
