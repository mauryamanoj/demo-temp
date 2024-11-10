package com.saudi.tourism.core.services;

import com.saudi.tourism.core.services.impl.UserServiceImpl;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class UserServiceTest {

  UserService userService;
  ResourceResolverFactory resourceFactory;

  @BeforeEach private void setUp(AemContext context) {
    userService = new UserServiceImpl();
    resourceFactory = mock(ResourceResolverFactory.class);
    Utils.setInternalState(userService, "resourceFactory", resourceFactory);
  }

  @Test public void getResourceResolver(AemContext context) throws LoginException {
    when(resourceFactory.getServiceResourceResolver(any())).thenReturn(context.resourceResolver());
    Assertions.assertNotNull(userService.getResourceResolver());
    Assertions.assertNotNull(userService.getWritableResourceResolver());
  }

  @Test public void getException(AemContext context) throws LoginException {
    when(resourceFactory.getServiceResourceResolver(any())).thenThrow(new LoginException());
    Assertions.assertNull(userService.getResourceResolver());
  }
}
