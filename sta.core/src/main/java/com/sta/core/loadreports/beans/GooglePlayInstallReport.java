package com.sta.core.loadreports.beans;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.sta.core.loadreports.LoadReportsUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Google Play install stat report.
 */
public class GooglePlayInstallReport extends Report {
  /**
   * Date Header.
   */
  private static final String CSV_HEADER_DATE = "Date";
  /**
   * Date Header.
   */
  private static final String CSV_HEADER_COUNTRY = "Country";
  /**
   * Installs Header.
   */
  private static final String CSV_HEADER_INSTALLS = "Daily User Installs";
  /**
   * header index Date.
   */
  private final int indexDate;
  /**
   * header index Country.
   */
  private final int indexCountry;
  /**
   * header index new installs.
   */
  private final int indexNewInstalls;

  /**
   * Constructor.
   *
   * @param indexDate         header index SKU
   * @param indexCountry       header index Units
   * @param indexNewInstalls  header index ProdTypeId
   */
  private GooglePlayInstallReport(final int indexDate, final int indexCountry,
      final int indexNewInstalls) {
    this.indexDate = LoadReportsUtils.requireNotNegative(indexDate, CSV_HEADER_DATE);
    this.indexCountry = LoadReportsUtils.requireNotNegative(indexCountry, CSV_HEADER_COUNTRY);
    this.indexNewInstalls = LoadReportsUtils.requireNotNegative(indexNewInstalls, CSV_HEADER_INSTALLS);
  }

  /**
   * Builder - init headers byb first line.
   *
   * @param headers - first line of report that contains headers
   * @return new object
   */
  private static GooglePlayInstallReport createReport(String[] headers) {
    int indexDate = ArrayUtils.indexOf(headers, CSV_HEADER_DATE);
    int indexCountry = ArrayUtils.indexOf(headers, CSV_HEADER_COUNTRY);
    int indexNewInstalls = ArrayUtils.indexOf(headers, CSV_HEADER_INSTALLS);
    return new GooglePlayInstallReport(indexDate, indexCountry, indexNewInstalls);
  }

  /**
   * Add row data.
   * @param line line
   * @param appName appName
   */
  public void addRowData(String[] line, final String appName) {
    int installs = Integer.parseInt(line[indexNewInstalls]);
    if (installs == 0) {
      return;
    }
    String countryCode = line[indexCountry];
    if (StringUtils.isBlank(countryCode)) {
      countryCode = LoadReportsUtils.UNKNOWN;
    }
    ReportRow row = ReportRow.builder()
        .date(LocalDate.parse(line[indexDate], DateTimeFormatter.ISO_DATE))
        .appName(appName).shop(ReportShop.GOOGLE_PLAY_MARKET).appVersion(LoadReportsUtils.UNKNOWN)
        .countryCode(countryCode).installCount(installs)
        .build();
    super.addRow(row);
  }

  /**
   * Parse report.
   * @param bytes binary presentation of report
   * @param startDate startDate
   * @param appName appName
   * @return array of reports per day
   * @throws IOException error
   */
  public static Map<LocalDate, GooglePlayInstallReport> parseReport(byte[] bytes,
      LocalDate startDate, String appName)
      throws IOException {
    Map<LocalDate, GooglePlayInstallReport> reportMap = new HashMap<>();
    String[] headers;
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_16))) {
      String line = br.readLine();
      headers = Objects.requireNonNull(line).split(LoadReportsUtils.COMMA);
      while ((line = br.readLine()) != null) {
        String[] values = line.split(LoadReportsUtils.COMMA);
        LocalDate dt = LocalDate.parse(values[0], DateTimeFormatter.ISO_DATE);
        if (dt.isBefore(startDate)) {
          continue;
        }
        GooglePlayInstallReport report = reportMap.computeIfAbsent(dt, d -> createReport(headers));
        report.addRowData(values, appName);
      }
    }
    return reportMap;
  }
}
