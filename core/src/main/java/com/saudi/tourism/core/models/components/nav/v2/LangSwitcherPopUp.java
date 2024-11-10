package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Model class of language switcher pop up data.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class LangSwitcherPopUp {

  /**
   * stay on page label.
   */
  @ValueMapValue
  @Expose
  private String ctaLabelOne;

  /**
   * Goto homepage label.
   */
  @ValueMapValue
  @Expose
  private String ctaLabelTwo;

  /**
   * current language message.
   */
  @ValueMapValue
  @Expose
  private String currentLangMsg;
  /**
   * Target language message.
   */
  @Expose
  private String targetLangMsg;
  /**
   * Target language message.
   */
  @Expose
  private Boolean pageAvailable;

}
