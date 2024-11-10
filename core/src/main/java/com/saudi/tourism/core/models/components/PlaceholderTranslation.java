package com.saudi.tourism.core.models.components;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Class PlaceholderTranslation.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PlaceholderTranslation {

  /**
   * The Language.
   */
  @ValueMapValue
  @Getter
  private String language;

  /**
   * The Translation.
   */
  @ValueMapValue
  @Getter
  private String translation;

}
