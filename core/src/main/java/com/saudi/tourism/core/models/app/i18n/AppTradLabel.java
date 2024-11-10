package com.saudi.tourism.core.models.app.i18n;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * App trad label model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AppTradLabel implements Serializable {

  /**
   * keyAppTrad .
   */
  @Getter
  @Setter
  @ValueMapValue
  private String valueAppTrad;

  /**
   * valueAppTrad .
   */
  @Getter
  @Setter
  @ValueMapValue
  private String keyAppTrad;
}
