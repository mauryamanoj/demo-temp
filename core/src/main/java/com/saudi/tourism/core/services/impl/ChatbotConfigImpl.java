package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.services.ChatbotConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


/**
 * This class contains Saudi Chatbot configurations.
 */
@Slf4j
@Component(immediate = true,
           service = ChatbotConfig.class)
@Designate(ocd = ChatbotConfigImpl.Configuration.class)
@Getter
public class ChatbotConfigImpl implements ChatbotConfig {

  /**
   * Chatbot Enable/Disable Sitewide.
   */
  private Boolean chatbotEnableDisableSitewide;
  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param saudiChatbotConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration saudiChatbotConfig) {
    LOGGER.debug("Saudi Chatbot Configurations Enabled/Disabled sitewide");

    this.chatbotEnableDisableSitewide = saudiChatbotConfig.chatbotEnableDisableSitewide();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Tourism Chatbot Configuration") @interface Configuration {

    /**
     *
     * @return Boolean chatbot.
     */
    @AttributeDefinition(name = "Chatbot (Sitewide)", type = AttributeType.BOOLEAN)
    boolean chatbotEnableDisableSitewide() default false;

  }
}

