package com.saudi.tourism.core.utils;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.InMemoryCache;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Testing utilities
 */
public class Utils {

    /**
     * Set internal state on a private field.
     * @param target    target object to set the private field
     * @param field     name of the private field
     * @param value     value of the private field
     */
    public static void setInternalState(Object target, String field, Object value) {
        Class<?> c = target.getClass();
        try {
            Field f = getFieldFromHierarchy(c, field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to set internal state on a private field. Please report to mockito mailing list.", e);
        }
    }

    /**
     * Fake i18n resource bundle that returns key as a value.
     * @return i18nResourceBundle
     */
    public static ResourceBundle i18nBundle() {
        return new ResourceBundle() {
            @Override protected Object handleGetObject(@NotNull final String key) {
                return key;
            }

            @NotNull @Override public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }
        };
    }

    private static Field getFieldFromHierarchy(Class<?> clazz, String field) {
      Field f = getField(clazz, field);
      while (f == null && clazz != Object.class) {
        clazz = clazz.getSuperclass();
        f = getField(clazz, field);
      }
      if (f == null) {
        throw new RuntimeException(
            "You want me to get this field: '" + field + "' on this class: '" + clazz
                .getSimpleName()
                + "' but this field is not declared withing hierarchy of this class!");
      }
      return f;
    }

  private static Field getField(Class<?> clazz, String field) {
    try {
      return clazz.getDeclaredField(field);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  /**
   * Creates and registers mock for our memory cache.
   *
   * @param context aem context to register the the cache service, if it's null the service is
   *                not registered
   * @return created mocked cache instance
   */
  public static Cache mockMemCache(@Nullable AemContext context) {
    final Cache mockCache = mock(InMemoryCache.class);
    if (context != null) {
      context.registerInjectActivateService(mockCache);
    }
    return mockCache;
  }

  /**
   * Creates stub / spy for in-memory cache instance with default hash map implementation, for
   * checking if proper keys were added into the cache during service execution.
   *
   * @param context context to register as a service. If this parameter is null, the cache service
   *                is not registered, so you need to use
   *                {@link Utils#setInternalState(Object, String, Object)} to update in your class
   * @return created stub / spy cache instance
   */
  public static TestCacheImpl spyMemCache(@Nullable AemContext context) {
    // Ways to create: spy(new TestCacheImpl()), or mock(TestCacheImpl.class, CALLS_REAL_METHODS)
    // then init()
    final TestCacheImpl stubMemCache = spy(new TestCacheImpl());
    // Register as a service if context was provided
    if (context != null) {
      context.registerInjectActivateService(Cache.class, stubMemCache);
    }

    return stubMemCache;
  }

  /**
   * Creates mock of our UserService (resource resolver provider service), which returns provided
   * resource resolver when it is requested, or {@code null} if the resolver wasn't provided.
   *
   * @param resolver resource resolver
   * @return mocked UserService instance
   */
  public static UserService spyUserService(@Nullable ResourceResolver resolver) {
    final UserService resolverService = spy(UserService.class);
    doReturn(resolver).when(resolverService).getResourceResolver();
    return resolverService;
  }

  /**
   * Mocking query builder.
   *
   * @param resultResources list of resources to be returned
   * @return mocked QueryBuilder instance
   */
  public static QueryBuilder mockQueryBuilder(Resource... resultResources) {
    return mockQueryBuilder(Arrays.asList(resultResources));
  }

  /**
   * Mocking query builder.
   *
   * @param resultResourcesList list of resources to be returned
   * @return mocked QueryBuilder instance
   */
  public static QueryBuilder mockQueryBuilder(final List<Resource> resultResourcesList) {
    final QueryBuilder mockQueryBuilder = mock(QueryBuilder.class);
    final Query mockQuery = mock(Query.class);
    doReturn(mockQuery).when(mockQueryBuilder).createQuery(any(PredicateGroup.class), any());
    final SearchResult mockSearchResult = mock(SearchResult.class);
    doReturn(resultResourcesList.iterator()).when(mockSearchResult).getResources();
    doReturn(mockSearchResult).when(mockQuery).getResult();

    return mockQueryBuilder;
  }

  /**
   * Create resource that will return the specified object when calling {@code resource.adaptTo}.
   *
   * @param adapterTypeClass class for adaptTo method
   * @param adaptedObject    object that will be returned after adaptTo
   * @param <AdapterType>    AdapterType type
   * @return adaptable resource
   */
  public static <AdapterType> Resource mockAdaptableResource(
      final Class<AdapterType> adapterTypeClass, final AdapterType adaptedObject) {
    final Resource mockResource = mock(Resource.class);
    doReturn(adaptedObject).when(mockResource).adaptTo(eq(adapterTypeClass));
    return mockResource;
  }

  /**
   * Create resource that will return the specified object when calling {@code resource.adaptTo}.
   *
   * @param adaptedObject object that will be returned after adaptTo
   * @param <AdapterType> AdapterType type
   * @return adaptable resource
   */
  public static <AdapterType> Resource mockAdaptableResource(final AdapterType adaptedObject) {
    final Resource mockResource = mock(Resource.class);
    doReturn(adaptedObject).when(mockResource).adaptTo(eq(adaptedObject.getClass()));
    return mockResource;
  }

  // Mock a ContentFragment element with the given name and value.
  public static <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.lenient().when(element.getValue()).thenReturn(elementData);

    if(value instanceof Calendar) {
      Mockito.lenient().when(elementData.getValue(Calendar.class)).thenReturn((Calendar) value);
    } else {
      Mockito.lenient().when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
    }
  }
}
