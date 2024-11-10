package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.listeners.SaudiEventListener;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class SaudiEventListenerTest {

  private SaudiEventListener saudiEventListener;
  private UserService userService;

  private Cache cache;

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @BeforeEach public void setUp(AemContext context) {
    cache = Mockito.mock(Cache.class);
    saudiEventListener = new SaudiEventListener();
    Utils.setInternalState(saudiEventListener, "cache", cache);

    userService = mock(UserService.class);
    when(userService.getResourceResolver()).thenReturn(context.resourceResolver());
    Utils.setInternalState(saudiEventListener, "userService", userService);
  }

  @Test public void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    saudiEventListener.onChange(null);
    Assertions.assertNotNull(context.response());
  }

  @Test public void testEventsChanges(AemContext context)
      throws IllegalArgumentException {
    List<ResourceChange> changes = new ArrayList<>();
    changes.add(new ResourceChange(ResourceChange.ChangeType.CHANGED, "/content/sauditourism/en/events", false));
    saudiEventListener.onChange(changes);
    Assertions.assertNotNull(context.response());
  }

  @Test public void testChanges(AemContext context)
      throws IllegalArgumentException {
    context.load().json("/components/header-config-model/content.json", CONTENT_PATH);

    List<ResourceChange> changes = new ArrayList<>();
    changes.add(new ResourceChange(ResourceChange.ChangeType.CHANGED, CONTENT_PATH + "/Configs"
        + "/navigation-menu-configs", false));
    saudiEventListener.onChange(changes);
    Assertions.assertNotNull(context.response());
  }

}
