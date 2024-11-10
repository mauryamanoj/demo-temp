package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.MyTableLoginService;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * This class contains MyTable Login configurations.
 */
@Slf4j
@Component(immediate = true,
           service = MyTableLoginService.class)
@Designate(ocd = MyTableLoginServiceImpl.Configuration.class)
public class MyTableLoginServiceImpl implements MyTableLoginService {

  /**
   * Request string format.
   */
  private static final String REQUEST_FORMAT =
      "{\"client_id\": \"%s\",\"client_secret\": \"%s\"}";

  /**
   * Holds clientId.
   */
  private String loginUrl;

  /**
   * Holds clientId.
   */
  private String clientId;

  /**
   * Holds clientSecret.
   */
  private String clientSecret;

  @Override
  public String loginAndGetResponse() throws IOException {
    HttpEntity entity = new StringEntity(String.format(REQUEST_FORMAT, clientId, clientSecret),
        ContentType.APPLICATION_JSON);
    ResponseMessage response =
        RestHelper.executeMethodPost(loginUrl, entity, null, false);
    if (response.getStatusCode() == SC_OK) {
      return response.getMessage();
    }
    throw new IOException("Bad response from MyTable. Code: " + response.getStatusCode());
  }

  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param config Configuration
   */
  @Activate
  protected void activate(MyTableLoginServiceImpl.Configuration config) {
    LOGGER.debug("MyTable Login Configurations Activate/Modified");
    this.loginUrl = config.loginUrl();
    this.clientId = config.clientId();
    this.clientSecret = config.clientSecret();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "MyTable Login Configuration")
  @interface Configuration {

    /**
     * Retrieve the MyTable login URL.
     *
     * @return String login URL
     */
    @AttributeDefinition(name = "MyTable login URL",
                         type = AttributeType.STRING) String loginUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the MyTable clientId.
     *
     * @return String clientId
     */
    @AttributeDefinition(name = "MyTable clientId",
                         type = AttributeType.STRING) String clientId() default StringUtils.EMPTY;

    /**
     * Retrieve the MyTable clientSecret.
     *
     * @return String clientSecret
     */
    @AttributeDefinition(name = "MyTable clientSecret", type = AttributeType.STRING)
    String clientSecret() default StringUtils.EMPTY;
  }
}
