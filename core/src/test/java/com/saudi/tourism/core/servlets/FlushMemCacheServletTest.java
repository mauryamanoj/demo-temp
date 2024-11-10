package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.FlushMemCacheServlet;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;

@ExtendWith(AemContextExtension.class) public class FlushMemCacheServletTest {

  private FlushMemCacheServlet flushMemCacheServlet;

  private Cache memCache;

  @BeforeEach public void setUp(AemContext context) {
    memCache = Mockito.mock(Cache.class);
    flushMemCacheServlet = new FlushMemCacheServlet();
    Utils.setInternalState(flushMemCacheServlet, "memCache", memCache);

  }

  @Test public void test(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {

    flushMemCacheServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

}
