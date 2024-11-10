package com.saudi.tourism.core.models.app.contact;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Contact model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Contact implements Serializable {

  /**
   * Header.
   */
  @ValueMapValue
  @Getter
  private String header;

  /**
   * Phone.
   */
  @ValueMapValue
  @Getter
  private String phone;

  /**
   * Text.
   */
  @ValueMapValue
  @Getter
  private String text;

  /**
   * CTA.
   */
  @ValueMapValue
  @Getter
  private String cta;

}
