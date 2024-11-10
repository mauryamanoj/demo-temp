package com.saudi.tourism.core.services;

/**
 * Interface for Saudi Mode Configurations Service to expose only required methods.
 */
public interface SaudiModeConfig {

  /**
   * this method should return the publish or not.
   *
   * @return String publish or not.
   */
  String getPublish();

  /**
   * method returns the run mode dev,local,acc or prod.
   *
   * @return String mode.
   */
  String getMode();

}
