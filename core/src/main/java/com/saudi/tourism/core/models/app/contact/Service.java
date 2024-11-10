package com.saudi.tourism.core.models.app.contact;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Service model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Service implements Serializable {

  /**
   * Name.
   */
  @ValueMapValue
  @Getter
  private String name;

  /**
   * Phone.
   */
  @ValueMapValue
  @Getter
  private String phone;

  /**
   * Category.
   */
  @ValueMapValue
  @Getter
  private String category;
}
