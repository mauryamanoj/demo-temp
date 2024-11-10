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
public class ContactItem implements Serializable {

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
  private String text;

  /**
   * Text.
   */
  @ValueMapValue
  @Getter
  private String value;

  /**
   * CTA.
   */
  @ValueMapValue
  @Getter
  private String ctaType;

}
