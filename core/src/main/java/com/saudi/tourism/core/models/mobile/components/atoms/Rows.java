package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Rows model.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Rows {

  /**
   * key.
   */
  @ValueMapValue
  private String key;

  /**
   * value.
   */
  @ValueMapValue
  private String value;
}
