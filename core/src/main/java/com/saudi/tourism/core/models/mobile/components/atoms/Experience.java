package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Experience {

  /** Filter. */
  @ChildResource
  private Filter filter;

  /** Free Experience Only. */
  @ValueMapValue
  private Boolean freeOnly;

  /**
   * Destination CF Path.
   */
  @ValueMapValue
  private String destinationCFPath;

  /**
   * Destination for HY.
   */
  private String destination;

  @PostConstruct
  protected void init() {
    if (StringUtils.isNotBlank(destinationCFPath)) {
      destination = StringUtils.substringAfterLast(destinationCFPath, "/");
    }

    if (!freeOnly) {
      freeOnly = null;
    }
  }
}
