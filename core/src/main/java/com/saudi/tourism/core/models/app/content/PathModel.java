package com.saudi.tourism.core.models.app.content;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Path model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@ToString
public class PathModel implements Serializable {

  /**
   * Path.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String path;
}
