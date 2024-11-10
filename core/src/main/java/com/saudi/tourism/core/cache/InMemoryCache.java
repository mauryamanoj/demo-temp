package com.saudi.tourism.core.cache;

import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The type In memory cache.
 */
@Component(name = "InMemoryCache",
           service = Cache.class,
           immediate = true)
@Slf4j
public class InMemoryCache implements Cache {

  /**
   * cleans up the cache every 5 seconds, if any object is expired.
   */
  private static final long CLEAN_UP_PERIOD_IN_SEC = 5;

  /**
   * default cache period of 1 day.
   */
  private static final long CLEAN_UP_PERIOD_DAY = 86400;


  /**
   * The Cache.
   */
  private final ConcurrentHashMap<String, SoftReference<CacheObject>> cache =
      new ConcurrentHashMap<>();

  /**
   * Cleaner thread.
   */
  private Thread cleanerThread;

  /**
   * creates a InMemory Cache.
   */
  public InMemoryCache() {
    cleanerThread = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * Constants.NUMBER_OF_SECONDS);
          cache.entrySet().removeIf(
              entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
                  .map(CacheObject::isExpired).orElse(false));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    cleanerThread.setDaemon(true);
    cleanerThread.start();
  }

  /**
   * Cleans up the service upon deactivation.
   */
  @Deactivate
  protected void deactivate() {
    cleanerThread.interrupt();
  }

  @Override public void add(String key, Object value, long periodInMillis) {
    if (key == null) {
      return;
    }
    if (value == null) {
      cache.remove(key);
    } else {
      long expiryTime = System.currentTimeMillis() + periodInMillis;
      cache.put(key, new SoftReference<>(new CacheObject(value, expiryTime)));
    }
  }

  @Override public void add(String key, Object value) {
    add(key, value, CLEAN_UP_PERIOD_DAY * Constants.NUMBER_OF_SECONDS);
  }

  @Override public void remove(String key) {
    LOGGER.debug("Removing cached object by key: {}", key);
    cache.remove(key);
  }

  @Override public Object get(String key) {
    return Optional.ofNullable(cache.get(key)).map(SoftReference::get)
        .filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
  }

  @Override public void clear() {
    LOGGER.debug("Clearing in-memory cache");
    cache.clear();
  }

  @Override public long size() {
    return cache.entrySet().stream().filter(
        entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
            .map(cacheObject -> !cacheObject.isExpired()).orElse(false)).count();
  }

  @Override
  public Object computeIfAbsent(final String key, final Function<String, ?> mappingFunction,
      long periodInMillis) {
    Object value = get(key);
    if (value == null) {
      value = Optional.ofNullable(mappingFunction.apply(key)).orElse(null);
      if (value != null) {
        add(key, value, periodInMillis);
      }
    }
    return value;
  }

  /**
   * CacheObject which holds value and expiry time.
   */
  private static final class CacheObject {
    /**
     * Instantiates a new Cache object.
     *
     * @param value      the value
     * @param expiryTime the expiry time
     */
    private CacheObject(Object value, long expiryTime) {
      this.value = value;
      this.expiryTime = expiryTime;
    }

    /**
     * cache object value.
     */
    @Getter
    @Setter
    private Object value;

    /**
     * expiry time for the cache object.
     */
    private long expiryTime;

    /**
     * Is expired boolean.
     *
     * @return the boolean
     */
    boolean isExpired() {
      return System.currentTimeMillis() > expiryTime;
    }

  }
}
