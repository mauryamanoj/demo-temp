package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * BridgeConfigModel.
 */

@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BridgeConfigModel {
  /**
   * siteWideCheck.
   */
  @Getter
  @Setter
  private boolean configCheckVal;
  /**
   * saudiTourismConfig.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfig;
  /**
   * Postconstruct.
   */
  @PostConstruct
  protected void init() {
    if (null != saudiTourismConfig && null != saudiTourismConfig.getBridgeEnable()
        && saudiTourismConfig.getBridgeEnable()) {
      configCheckVal = true;
    } else {
      configCheckVal = false;
    }
  }
}
