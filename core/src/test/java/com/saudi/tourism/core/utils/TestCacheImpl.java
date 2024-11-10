package com.saudi.tourism.core.utils;

import com.saudi.tourism.core.cache.Cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Utility class for testing in-memory cache.
 */
public class TestCacheImpl implements Cache {

  private ConcurrentHashMap<String, Object> cache;

  public TestCacheImpl() {
    init();
  }

  public void init() {
    cache = new ConcurrentHashMap<>();
  }

  @Override
  public void add(final String key, final Object value, final long periodInMillis) {
    this.add(key, value);
  }

  @Override
  public void add(final String key, final Object value) {
    cache.put(key, value);
  }

  @Override
  public void remove(final String key) {
    cache.remove(key);
  }

  @Override
  public Object get(final String key) {
    return cache.get(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public long size() {
    return cache.size();
  }

  @Override
  public Object computeIfAbsent(final String key, final Function<String, ?> mappingFunction,
      final long periodInMillis) {
    return cache.computeIfAbsent(key, mappingFunction);
  }

  /**
   * Returns true if the cache has the object with specified key.
   *
   * @param key key to check
   */
  public boolean containsKey(String key) {
    return cache.containsKey(key);
  }
}
