package com.saudi.tourism.core.models.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Model class of heading related data.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@NoArgsConstructor
public class Heading {

  /**
   * Constructor.
   * @param text text
   */
  public Heading(final String text) {
    this.text = text;
  }

  /**
  * Variable heading text.
  */
  @Getter
  @ValueMapValue
  @Expose
  private String text;

  /**
   * Variable analytics text.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String analyticsText;

  /**
  * Variable heading weight.
  */
  @Getter
  @ValueMapValue
  @Expose
  private String weight;

  /**
   * Get weight without h prefix. Required for heading-1..5 classes.
   * @return decimal weight
   */
  public String getDecimalWeight() {
    if (this.weight == null) {
      return StringUtils.EMPTY;
    }

    return this.weight.replaceFirst("^h", StringUtils.EMPTY);
  }
}
