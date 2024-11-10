package com.sta.core.scene7.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.sta.core.scene7.DMAssetUploadConfig;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Dynamic Media Asset upload config service implementation.
 */
@Component(immediate = true, service = DMAssetUploadConfig.class)
@Designate(ocd = DMAssetUploadConfigImpl.Configuration.class)
@Slf4j
public class DMAssetUploadConfigImpl implements DMAssetUploadConfig {

  /**
   * Company Name.
   */
  @Getter
  private String ugcAssetUploadUrl;

  /**
   * user.
   */
  @Getter
  private String user;
  /**
   * password.
   */
  @Getter
  private String password;
  /**
   * Company Name.
   */
  @Getter
  private String companyName;

  /**
   * DM Secret Key.
   */
  @Getter
  private String dmSecretKey;

  /**
   * DM Secret Key.
   */
  @Getter
  private String tokenExpires;

  /**
   * Activate configuration.
   * @param configuration configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration configuration) {
    LOGGER.debug("DMAssetUpload Configurations Activate/Modified");
    user = configuration.user();
    password = configuration.password();
    companyName = configuration.companyName();
    dmSecretKey = configuration.dmSecretKey();
    tokenExpires = configuration.tokenExpires();
    ugcAssetUploadUrl = configuration.ugcAssetUploadUrl();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Tourism Forms - DM Asset Upload Configuration")
  @interface Configuration {

    /**
     * Dynamic Media server Asset upload url.
     *
     * @return String asset upload base url.
     */
    @AttributeDefinition(name = "UGC DM Asset Upload URL", type = AttributeType.STRING)
    String ugcAssetUploadUrl() default StringUtils.EMPTY;

    /**
     * Company Name as per configured for Dynamic Media server.
     *
     * @return String Company Name.
     */
    @AttributeDefinition(name = "Company Name", type = AttributeType.STRING)
    String companyName() default StringUtils.EMPTY;

    /**
     * user of Dynamic Media server.
     *
     * @return String user.
     */
    @AttributeDefinition(name = "user", type = AttributeType.STRING)
    String user() default StringUtils.EMPTY;

    /**
     * password for Dynamic Media server.
     *
     * @return String password.
     */
    @AttributeDefinition(name = "password", type = AttributeType.STRING)
    String password() default StringUtils.EMPTY;

    /**
     * Dynamic Media server Secret key.
     *
     * @return String Shared Secret Key.
     */
    @AttributeDefinition(name = "DM Secret Key", type = AttributeType.STRING)
    String dmSecretKey() default StringUtils.EMPTY;

    /**
     * Dynamic Media token expire time in seconds.
     * Default : 300 seconds
     *
     * @return String Shared Secret Key.
     */
    @AttributeDefinition(name = "Token Expiration Time(in seconds)", type = AttributeType.STRING)
    String tokenExpires() default StringUtils.EMPTY;
  }

}
