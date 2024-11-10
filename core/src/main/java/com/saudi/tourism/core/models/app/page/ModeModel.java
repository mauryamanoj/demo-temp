package com.saudi.tourism.core.models.app.page;

import com.saudi.tourism.core.services.SaudiModeConfig;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

/**
 * Component mode model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ModeModel {

  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  @Getter
  private SaudiModeConfig saudiModeConfig;

}
