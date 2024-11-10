package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class EventListSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NoResultSlingModel implements Serializable {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String firstLine;
  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String icon;
  /**
   * The secondLine.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String secondLine;
  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String smallMessage;
}
