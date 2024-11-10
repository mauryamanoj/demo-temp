package com.saudi.tourism.core.models.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Chatbot Config Model.
 */
@Data
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ChatbotConfigModel {
  /**
   * chatbotTitle.
   */
  @ValueMapValue
  private String chatbotTitle;
  /**
   * Enabled or Disabled Type value.
   */
  @ValueMapValue
  private String enableOrDisable;
  /**
   * List of pages for disabling chatbot.
   */
  @ChildResource
  private List<ChatbotDisabledModel> disabledPages;
  /**
   * List of pages for enabling chatbot.
   */
  @ChildResource
  private List<ChatbotEnabledModel> enabledPages;

}
