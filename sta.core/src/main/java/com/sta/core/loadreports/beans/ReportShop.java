package com.sta.core.loadreports.beans;

import com.sta.core.loadreports.LoadReportsUtils;
import lombok.Getter;

/**
 * Enum for claassify the mobile app shops.
 */
public enum ReportShop {
  /**
   * Apple App Store.
   */
  APPLE_APP_STORE("Apple App Store", LoadReportsUtils.STATREPORT_APPSTORE_PATH, "apple"),
  /**
   * Google Play Market.
   */
  GOOGLE_PLAY_MARKET("Google Play", LoadReportsUtils.STATREPORT_GOOGLEPLAY_PATH, "google"),
  /**
   * Huawei AppGallery.
   */
  HUAWEI_APP_GALLERY("Huawei AppGallery", LoadReportsUtils.STATREPORT_HUAWEI_PATH, "huawei");

  /**
   * name.
   */
  @Getter
  private final String name;
  /**
   * path.
   */
  @Getter
  private final String path;
  /**
   * short Name.
   */
  @Getter
  private final String source;

  /**
   * @param name - the name of shop.
   */
  ReportShop(final String name, final String path, final String source) {
    this.name = name;
    this.path = path;
    this.source = source;
  }

  /**
   * Get enum value by name.
   *
   * @param name name value
   * @return ReportShop
   */
  public static ReportShop getByName(String name) {
    for (ReportShop shop : values()) {
      if (shop.name.equals(name)) {
        return shop;
      }
    }
    return null;
  }
}
