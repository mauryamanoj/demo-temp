package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Data
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ButtonComponentStyle {
  /**
   * componentUIType.
   */
  @ValueMapValue
  private String componentUIType;

  /**
   * Default constructor.
   */
  public ButtonComponentStyle() {
  }

  /**
   * Constructor with parameter.
   * @param componentUIType componentUIType
   */
  public ButtonComponentStyle(String componentUIType) {
    this.componentUIType = componentUIType;
  }
}
