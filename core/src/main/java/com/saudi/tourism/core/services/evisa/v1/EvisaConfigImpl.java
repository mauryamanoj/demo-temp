package com.saudi.tourism.core.services.evisa.v1;

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
 * This class contains Saudi Tourism Evisa Config.
 */
@Slf4j
@Getter
@Component(immediate = true, service = EvisaConfig.class)
@Designate(ocd = EvisaConfigImpl.Configuration.class)
public class EvisaConfigImpl implements EvisaConfig {

  /**
   * eVisa Config path.
   */
  private String eVisaConfigPath;

  /**
   * eVisa Countries Groups config path.
   */
  private String eVisaCountriesGroupsConfigPath;



  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param eVisaConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration eVisaConfig) {
    LOGGER.debug("Saudi eVisa configuration Activate/Modified");
    this.eVisaConfigPath = eVisaConfig.eVisaConfigPath();
    this.eVisaCountriesGroupsConfigPath = eVisaConfig.eVisaCountriesGroupsConfigPath();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Evisa Configuration")
  @interface Configuration {

    /**
     * eVisa Config Path.
     *
     * Each locale will have its own evisa config page.
     * Should be something like
     * /content/sauditourism/{0}/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config.
     *
     * @return evisa config path
     */
    @AttributeDefinition(name = "Evisa Config Path", type = AttributeType.STRING)
    String eVisaConfigPath() default StringUtils.EMPTY;

    /**
     * eVisa Countries Groups config Path.
     *
     * @return eVisa Countries Groups config Path
     */
    @AttributeDefinition(name = "Evisa Countries Groups config Path", type = AttributeType.STRING)
    String eVisaCountriesGroupsConfigPath() default StringUtils.EMPTY;
  }
}
