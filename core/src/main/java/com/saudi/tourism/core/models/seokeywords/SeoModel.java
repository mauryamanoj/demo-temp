package com.saudi.tourism.core.models.seokeywords;

import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class SeoModel.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SeoModel implements Serializable {

  /**
   * The keyword.
   */
  @Getter
  @ValueMapValue
  private String keyword;
}

