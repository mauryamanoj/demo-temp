package com.saudi.tourism.core.services;

/**
 * Common service provide utilities for accessing Run modes,
 * All service should extends this for accessibility convenient.
 */
public interface RunModeService {

  /**
   * check if runmode publish is set.
   *
   * @return true if publish runmode.
   */
  boolean isPublishRunMode();
}
