package com.sta.core.loadreports.services.impl;

import com.sta.core.loadreports.LoadReportsUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import com.sta.core.loadreports.services.UserService;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The type User service.
 */
@Component(service = UserService.class,
           immediate = true)
@Slf4j
public class UserServiceImpl implements UserService {

  /**
   * Inject resource.
   */
  @Reference
  private ResourceResolverFactory resourceFactory;

  @Override public ResourceResolver getWritableResourceResolver() {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put(ResourceResolverFactory.SUBSERVICE, LoadReportsUtils.WRITE_SERVICE);
    return getResourceResolver(userMap);
  }

  @Override public ResourceResolver getResourceResolver(Map<String, Object> userMap) {
    ResourceResolver resolver = null;
    try {
      resolver = resourceFactory.getServiceResourceResolver(userMap);
    } catch (LoginException e) {
      LOGGER.error("Error in getResourceResolver() method. "
              + " Can't get resource resolver with subservice: {}. Detailed error {}",
          userMap.get(ResourceResolverFactory.SUBSERVICE), e);
    }
    return resolver;
  }

}
