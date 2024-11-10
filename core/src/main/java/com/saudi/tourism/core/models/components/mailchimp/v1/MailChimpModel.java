package com.saudi.tourism.core.models.components.mailchimp.v1;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Mailchimp.
 *
 * @author jlentink
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class MailChimpModel {

  /**
   * Saudi Tourism Configurations.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * Mailchimp URL.
   */
  @Getter
  private String mailChimpURL;

  /**
   * Mailchimp Field.
   */
  @Getter
  private String mailChimpField;

  /**
   * Retrieve settings from config.
   */
  @PostConstruct protected void setKey() {
    try {
      mailChimpURL = saudiTourismConfig.getMailChimpURL();
      mailChimpField = saudiTourismConfig.getMailChimpField();
    } catch (Exception e) {
      LOGGER.debug("Could not get OSGI object.");
    }
  }
}
