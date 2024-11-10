package com.saudi.tourism.core.services;

import com.saudi.tourism.core.beans.PackageFormParams;
import com.saudi.tourism.core.models.common.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;

/**
 * Mailchimp service for managing subscriptions.
 */
public interface MailchimpService {

  /**
   * Send a POST request to configured Mailchimp URL to add a new subscription using
   * PackageFormParams instance.
   *
   * @param formParams email of the user to add a subscription
   * @param vendor the vendor
   * @return response message from the Mailchimp service in case of error or our success response
   * if everything was successful
   * @throws IOException if can't connect
   */
  ResponseMessage addSubscription(PackageFormParams formParams, String vendor)
      throws IOException;

  /**
   * Configuration for one service instance.
   */
  @ObjectClassDefinition(description = MailChimpServiceConfig.DESCRIPTION,
                         name = MailChimpServiceConfig.NAME)
  @interface MailChimpServiceConfig {

    /**
     * Service name in OSGi Console.
     */
    String NAME = "Saudi Tourism Mailchimp Service Configuration";

    /**
     * Service name in OSGi Console.
     */
    String DESCRIPTION = "Service to manage Mailchimp subscriptions";

    /**
     * Mailchimp service url.
     *
     * @return the url
     */
    @AttributeDefinition(name = "URL",
                         defaultValue = StringUtils.EMPTY,
                         description = "Mailchimp service URL, the list id and member hash will be"
                             + " added automatically. Example: https://us10.api.mailchimp.com/3.0/")
    String url() default StringUtils.EMPTY;

    /**
     * Subscribers list id for the Mailchimp service.
     *
     * @return list id
     */
    @AttributeDefinition(name = "List Id",
                         defaultValue = StringUtils.EMPTY,
                         description = "Mailchimp subscriptions list id. Example: 9c0a110ff6")
    String listId() default StringUtils.EMPTY;

    /**
     * Mailchimp service authorization password.
     *
     * @return password
     */
    @AttributeDefinition(name = "API Key",
                         description = "Mailchimp service API key",
                         defaultValue = StringUtils.EMPTY,
                         type = AttributeType.PASSWORD)
    String apiKey() default StringUtils.EMPTY;

    /**
     * Mailchimp nofity email.
     *
     * @return password
     */
    @AttributeDefinition(name = "Notify Email",
                         description = "Notify Email",
                         defaultValue = StringUtils.EMPTY,
                         type = AttributeType.STRING)
    String notifyEmail() default StringUtils.EMPTY;
  }
}
