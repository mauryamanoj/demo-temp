package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.saudi.tourism.core.services.RunModeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Set;

/**
 * The type RunModeService.
 */
@Component(service = RunModeService.class, immediate = true)
@Slf4j public class RunModeServiceImpl
    implements RunModeService {

  /**
   * Settings service.
   */
  @Reference
  private SlingSettingsService settingsService;

  @Override
  public boolean isPublishRunMode() {
    Set<String> runModes = settingsService.getRunModes();
    return runModes.contains(Externalizer.PUBLISH);
  }

}
