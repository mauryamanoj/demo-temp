package com.saudi.tourism.core.models.app.contact;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import java.io.Serializable;

/**
 * Telephone Provider Details model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TelephoneProviderDetail implements Serializable {

  /**
   * Name.
   */
  @ValueMapValue
  @Getter
  private String name;

  /**
   * Icon.
   */
  @ValueMapValue
  @Getter
  private String icon;

  /**
   * link.
   */
  @ValueMapValue
  @Getter
  private String link;
}