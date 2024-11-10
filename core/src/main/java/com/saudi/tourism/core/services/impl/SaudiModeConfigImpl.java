package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.services.SaudiModeConfig;
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
 * This class contains Saudi Mode project configurations.
 */
@Slf4j
@Component(immediate = true,
           service = SaudiModeConfig.class)
@Designate(ocd = SaudiModeConfigImpl.Configuration.class)
public class SaudiModeConfigImpl implements SaudiModeConfig {

  /**
   * Holds map API key.
   */
  @Getter
  private String publish;

  /**
   * mode.
   */
  @Getter
  private String mode;

  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param saudiModeConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration saudiModeConfig) {
    LOGGER.debug("Saudi Mode Configurations Activate/Modified");

    this.publish = saudiModeConfig.publish();
    this.mode = saudiModeConfig.mode();
  }


  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Mode Configuration") @interface Configuration {

    /**
     * Retrieve the MapBox API key.
     *
     * @return String MapBox API key
     */
    @AttributeDefinition(name = "Is Publish or not", type = AttributeType.STRING)
    String publish() default StringUtils.EMPTY;

    /**
     * Get the environment mode.
     *
     * @return String mode.
     */
    @AttributeDefinition(name = "Instance run Mode",
                         description = "Instance run Mode",
                         type = AttributeType.STRING)
    String mode() default StringUtils.EMPTY;

  }
}

