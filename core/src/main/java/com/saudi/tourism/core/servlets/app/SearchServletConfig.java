package com.saudi.tourism.core.servlets.app;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Application Search Servlet Configuration.
 */
@ObjectClassDefinition(name = "Application Search Servlet Config")
public @interface SearchServletConfig {

  /**
   * Search Paths.
   * @return search paths
   */
  @AttributeDefinition(name = "Search paths",
                       description = "Paths to be searched in. Use {locale} token",
                       type = AttributeType.STRING)
  String[] includeSearchPaths() default {""};

}
