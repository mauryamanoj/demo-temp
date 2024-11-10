package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;

/**
 * The type User service.
 */
@Component(service = UserService.class, immediate = true)
@Slf4j public class UserServiceImpl
    implements UserService {

  /**
   * Inject resource.
   */
  @Reference private ResourceResolverFactory resourceFactory;

  @Override public ResourceResolver getResourceResolver() {
    ResourceResolver resourceResolver;

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put(ResourceResolverFactory.SUBSERVICE, Constants.READ_SERVICE);
    resourceResolver = getResourceResolver(paramMap);
    return resourceResolver;
  }

  @Override public ResourceResolver getWritableResourceResolver() {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put(ResourceResolverFactory.SUBSERVICE, Constants.WRITE_SERVICE);
    return getResourceResolver(userMap);
  }
  @Override public ResourceResolver getWorkflowResourceResolver() {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put(ResourceResolverFactory.SUBSERVICE, "workflow-service");
    return getResourceResolver(userMap);
  }


  @Override public ResourceResolver getResourceResolver(Map<String, Object> userMap) {
    ResourceResolver resolver = null;
    try {
      resolver = resourceFactory.getServiceResourceResolver(userMap);
    } catch (LoginException e) {
      LOGGER.error("Error in getResourceResolver() method. "
              + " Can't get resource resolver with subservice: {}.",
          userMap.get(ResourceResolverFactory.SUBSERVICE), e);
    }
    return resolver;
  }

}
