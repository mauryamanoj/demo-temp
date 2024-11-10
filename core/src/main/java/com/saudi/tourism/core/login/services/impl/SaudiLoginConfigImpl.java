package com.saudi.tourism.core.login.services.impl;

import com.saudi.tourism.core.login.services.SaudiLoginConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


/**
 * This class contains Saudi Tourism Login configurations.
 */
@Slf4j
@Component(immediate = true,
           service = SaudiLoginConfig.class)
@Designate(ocd = SaudiLoginConfigImpl.Configuration.class)
public class SaudiLoginConfigImpl implements SaudiLoginConfig {

  /**
   * Holds map API key.
   */
  @Getter
  private String clientId;

  /**
   * Mailchimp endpoint URL.
   */
  @Getter
  private String clientSecret;

  /**
   * audience.
   */
  @Getter
  private String audience;
  /**
   * Auth0 Domain.
   */
  @Getter
  private String auth0Domain;
  /**
   * Auth0 Domain.
   */
  @Getter
  private String auth0BaseUrl;

  /**
   * auth0OauthTokenEndpoint.
   */
  @Getter
  private String auth0OauthTokenEndpoint;
  /**
   * auth0GetUserEndpoint.
   */
  @Getter
  private String auth0GetUserEndpoint;

  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param saudiLoginConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration saudiLoginConfig) {
    LOGGER.debug("Saudi Configurations Activate/Modified");

    this.clientId = saudiLoginConfig.clientId();
    this.clientSecret = saudiLoginConfig.clientSecret();
    this.audience = saudiLoginConfig.audience();
    this.auth0Domain = saudiLoginConfig.auth0Domain();
    this.auth0BaseUrl = saudiLoginConfig.auth0BaseUrl();
    this.auth0OauthTokenEndpoint = saudiLoginConfig.auth0OauthTokenEndpoint();
    this.auth0GetUserEndpoint = saudiLoginConfig.auth0GetUserEndpoint();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Login Configuration") @interface Configuration {

    /**
     * Retrieve the MapBox API key.
     *
     * @return String MapBox API key
     */
    @AttributeDefinition(name = "Auth0 clientId", type = AttributeType.STRING)
    String clientId() default StringUtils.EMPTY;

    /**
     * Retrieve the Auth0 clientSecret.
     *
     * @return String clientSecret
     */
    @AttributeDefinition(name = "Auth0 clientSecret", type = AttributeType.STRING)
    String clientSecret() default StringUtils.EMPTY;

    /**
     * Retrieve the Mailchimp Field.
     *
     * @return String Mailchimp Field
     */
    @AttributeDefinition(name = "Auth0 Api audience", type = AttributeType.STRING)
    String audience() default StringUtils.EMPTY;
    /**
     * Retrieve the Auth0 Domain.
     *
     * @return String auth0Domain Field
     */
    @AttributeDefinition(name = "Auth0 Api Domain", type = AttributeType.STRING)
    String auth0Domain() default StringUtils.EMPTY;
    /**
     * Retrieve the Auth0 Base URL.
     *
     * @return String auth0Domain Field
     */
    @AttributeDefinition(name = "Auth0 Api Base URL", type = AttributeType.STRING)
    String auth0BaseUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the auth0OauthTokenEndpoint.
     *
     * @return String auth0OauthTokenEndpoint.
     */
    @AttributeDefinition(name = "Auth0 Oauth Token Endpoint", type = AttributeType.STRING)
    String auth0OauthTokenEndpoint() default StringUtils.EMPTY;
    /**
     * Retrieve the Weather Data External App URL .
     *
     * @return String Weather Data External App URL .
     */
    @AttributeDefinition(name = "Auth0 GetUser Endpoint", type = AttributeType.STRING)
    String auth0GetUserEndpoint() default StringUtils.EMPTY;

  }
}

