package com.saudi.tourism.core.models.components.events;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The type InfoItem.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class InfoItem implements Serializable {

  /**
   * The label.
   */
  @ValueMapValue
  private String label;

  /**
   * The value.
   */
  @ValueMapValue
  private String value;

}
