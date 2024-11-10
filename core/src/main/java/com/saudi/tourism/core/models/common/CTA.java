package com.saudi.tourism.core.models.common;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Model class of CTA.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class CTA implements Serializable {

  /**
   * CTA Text.
   */
  @ValueMapValue
  private String ctaText;

  /**
   * CTA URL.
   */
  @ValueMapValue
  private String ctaUrl;
}
