package com.saudi.tourism.core.models.components.packages;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The phone field input model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
public class PhoneInputModel extends InputModel {

  /**
   * Message about an invalid phone.
   */
  @ValueMapValue
  @Getter
  private String invalidPhoneCopy;

  /**
   * Placeholder for the country field.
   */
  @ValueMapValue
  @Getter
  private String countryCodePlaceholder;

}
