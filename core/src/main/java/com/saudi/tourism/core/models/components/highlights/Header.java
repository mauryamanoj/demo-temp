package com.saudi.tourism.core.models.components.highlights;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 *  header model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class Header {

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * with Controls flag.
   */
  @ValueMapValue
  @Expose
  private Boolean withControls;

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private Boolean hideUnderline;

}
