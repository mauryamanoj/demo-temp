package com.saudi.tourism.core.models.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Analytics Link Model. can be used to set properties for analytics link model.
 */
@Data
@AllArgsConstructor
public class AnalyticsLinkModel {

  /**
   * Analytics Event Title.
   */
  private String event;

  /**
   * Analytics Link URL.
   */
  private String linkURL;

  /**
   * Analytics Link Title.
   */
  private String linkTitle;

  /**
   * Vendor Name for package.
   */
  private String vendorName;

  /**
   * Package name.
   */
  private String packageName;
}
