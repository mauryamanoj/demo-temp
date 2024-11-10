package com.sta.core.vendors.models;

import lombok.Data;

/**
 * The type Work flow info model.
 */
@Data
public class WorkFlowInfoModel {

  /**
   * The Path.
   */
  private String path;
  /**
   * The Message.
   */
  private String message;
  /**
   * The page updated.
   */
  private boolean updated;
}
