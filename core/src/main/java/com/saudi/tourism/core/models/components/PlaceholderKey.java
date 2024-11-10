package com.saudi.tourism.core.models.components;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;

/**
 * The Class PlaceholderKey.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PlaceholderKey {

  /**
   * The Text.
   */
  @ValueMapValue(name = PN_TITLE)
  @Getter
  private String text;

  /**
   * The Translations.
   */
  @ChildResource
  @Getter
  private List<PlaceholderTranslation> translations;

}
