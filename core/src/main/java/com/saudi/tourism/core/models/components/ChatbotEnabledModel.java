package com.saudi.tourism.core.models.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Chatbot Enabled Pages Model.
 */
@Data
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ChatbotEnabledModel {

  /**
   * Enabled Page Path.
   */
  @ValueMapValue
  private String enablePagePath;

}
