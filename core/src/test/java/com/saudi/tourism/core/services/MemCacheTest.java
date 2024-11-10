package com.saudi.tourism.core.services;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.InMemoryCache;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;

@ExtendWith(AemContextExtension.class)
class MemCacheTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  private QueryBuilder queryBuilder;

  private EventsRequestParams eventsRequestParams;
  private Query mockQuery;
  private Node mockNode;
  private SearchResult mockSearchResult;

  private Cache inMemoryCache;

  @BeforeEach public void setUp(AemContext context) {
    inMemoryCache = new InMemoryCache();

  }

  @Test void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException, RepositoryException {
    Assertions.assertEquals(null, inMemoryCache.get("test"));
  }

  @Test void testValue(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    inMemoryCache.add("test","value");
    Assertions.assertEquals("value", inMemoryCache.get("test"));
    Assertions.assertEquals(1, inMemoryCache.size());

  }

  @Test void testValueRemoved(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    inMemoryCache.add("test","value");
    Assertions.assertEquals("value", inMemoryCache.get("test"));
    inMemoryCache.remove("test");
    Assertions.assertEquals(null, inMemoryCache.get("test"));
  }

}
