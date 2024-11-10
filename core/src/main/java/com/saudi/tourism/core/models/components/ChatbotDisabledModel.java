package com.saudi.tourism.core.models.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Chatbot Disabled Pages Model.
 */
@Data
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ChatbotDisabledModel {

  /**
   * Disabled Page Path.
   */
  @ValueMapValue
  private String disablePagePath;

}
