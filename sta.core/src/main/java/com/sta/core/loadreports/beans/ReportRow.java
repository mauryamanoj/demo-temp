package com.sta.core.loadreports.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Row of report.
 */
@Getter
@Setter
@Builder
public class ReportRow {
  /**
   * date.
   */
  private LocalDate date;
  /**
   * shop.
   */
  private ReportShop shop;
  /**
   * appName.
   */
  private String appName;
  /**
   * appVersion.
   */
  private String appVersion;
  /**
   * countryCode.
   */
  private String countryCode;
  /**
   * installCount.
   */
  private int installCount;

  public ReportRow copy() {
    return ReportRow.builder().date(date).shop(shop)
        .appName(appName).appVersion(appVersion).countryCode(countryCode)
        .installCount(installCount).build();
  }

  /**
   * Find first equals row in the list.
   *
   * @param list list
   * @param row  row
   * @return Optional of row
   */
  public static Optional<ReportRow> findFirstEqualsInList(List<ReportRow> list, ReportRow row) {
    return list.stream().filter(r -> r.getDate().equals(row.getDate()))
        .filter(r -> r.getAppName().equals(row.getAppName()))
        .filter(r -> r.getAppVersion().equals(row.getAppVersion()))
        .filter(r -> r.getCountryCode().equals(row.getCountryCode()))
        .filter(r -> r.getShop().equals(row.getShop())).findFirst();
  }
}
