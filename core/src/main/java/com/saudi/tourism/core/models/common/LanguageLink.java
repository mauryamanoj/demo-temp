package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Model class of link related data.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
public class LanguageLink extends Link implements Serializable {

  /**
   * Variable current Lang Message.
   */
  @ValueMapValue
  @Expose
  private String currentLangMsg;

  /**
   * Variable of cta label.
   */
  @ValueMapValue
  @Expose
  private String ctaLabelTwo;


}
