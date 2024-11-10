package com.sta.core.loadreports.beans;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.sta.core.loadreports.LoadReportsUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * AppStoreSalesDailyReport parser.
 */
public final class AppStoreSalesDailyReport extends Report {

  /**
   * SKU Header.
   */
  private static final String CSV_HEADER_SKU = "SKU";

  /**
   * SKU Header.
   */
  private static final String CSV_HEADER_UNITS = "Units";

  /**
   * SKU Header.
   */
  private static final String CSV_HEADER_PRODTYPE_ID = "Product Type Identifier";

  /**
   * Country Code Header.
   */
  private static final String CSV_HEADER_COUNTRY_CODE = "Country Code";

  /**
   * Begin date Header.
   */
  private static final String CSV_HEADER_BEGIN_DATE = "Begin Date";

  /**
   * Version Header.
   */
  private static final String CSV_HEADER_VERSION = "Version";

  /**
   * header index SKU.
   */
  private int indexSku;
  /**
   * header index Units.
   */
  private final int indexUnits;
  /**
   * header index ProdTypeId.
   */
  private final int indexProdTypeId;
  /**
   * header index Version.
   */
  private final int indexVersion;
  /**
   * header index CountryCode.
   */
  private final int indexCountryCode;
  /**
   * header index BeginDate.
   */
  private final int indexBeginDate;

  /**
   * Constructor.
   *
   * @param indexSku         header index SKU
   * @param indexUnits       header index Units
   * @param indexProdTypeId  header index ProdTypeId
   * @param indexCountryCode header index CountryCode
   * @param indexBeginDate   header index BeginDate
   * @param indexVersion     header index Version
   */
  private AppStoreSalesDailyReport(final int indexSku, final int indexUnits,
      final int indexProdTypeId, final int indexCountryCode,
      int indexBeginDate, int indexVersion) {
    this.indexSku = LoadReportsUtils.requireNotNegative(indexSku, CSV_HEADER_SKU);
    this.indexUnits = LoadReportsUtils.requireNotNegative(indexUnits, CSV_HEADER_UNITS);
    this.indexProdTypeId = LoadReportsUtils.requireNotNegative(indexProdTypeId, CSV_HEADER_PRODTYPE_ID);
    this.indexCountryCode = LoadReportsUtils.requireNotNegative(indexCountryCode, CSV_HEADER_COUNTRY_CODE);
    this.indexBeginDate = LoadReportsUtils.requireNotNegative(indexBeginDate, CSV_HEADER_BEGIN_DATE);
    this.indexVersion = LoadReportsUtils.requireNotNegative(indexVersion, CSV_HEADER_VERSION);
  }

  /**
   * Builder - init headers byb first line.
   *
   * @param headers - first line of report that contains headers
   * @return new object
   */
  private static AppStoreSalesDailyReport createReport(String[] headers) {
    int indexSku = ArrayUtils.indexOf(headers, CSV_HEADER_SKU);
    int indexUnits = ArrayUtils.indexOf(headers, CSV_HEADER_UNITS);
    int indexProdTypeId = ArrayUtils.indexOf(headers, CSV_HEADER_PRODTYPE_ID);
    int indexCountryCode = ArrayUtils.indexOf(headers, CSV_HEADER_COUNTRY_CODE);
    int indexBeginDate = ArrayUtils.indexOf(headers, CSV_HEADER_BEGIN_DATE);
    int indexVersion = ArrayUtils.indexOf(headers, CSV_HEADER_VERSION);
    return new AppStoreSalesDailyReport(indexSku, indexUnits, indexProdTypeId,
        indexCountryCode, indexBeginDate, indexVersion);
  }

  /**
   * Add row data.
   * @param line line
   * @param appName - appName
   */
  public void addRowData(String[] line, String appName) {
    String prodTypeId = line[indexProdTypeId];
    if (StringUtils.isBlank(prodTypeId)) {
      return;
    }

    int units = Integer.parseInt(line[indexUnits]);
    int installs = 0;
    if (prodTypeId.startsWith(LoadReportsUtils.STRING_ONE)) {
      installs = units;
    } else {
      return;
    }
    String countryCode = line[indexCountryCode];
    if (StringUtils.isBlank(countryCode)) {
      countryCode = LoadReportsUtils.UNKNOWN;
    }
    String version = line[indexVersion];
    if (StringUtils.isBlank(version)) {
      version = LoadReportsUtils.UNKNOWN;
    }

    ReportRow row = ReportRow.builder()
        .date(LocalDate.parse(line[indexBeginDate],
            DateTimeFormatter.ofPattern(DATE_FORMAT_MM_DD_YYYY)))
        .appName(appName).shop(ReportShop.APPLE_APP_STORE).appVersion(version)
        .countryCode(countryCode).installCount(installs).build();

    super.addRow(row);
  }

  /**
   * Parse report.
   *
   * @param bytes binary presentation of report
   * @param sku sku
   * @param appName - appName
   * @return list of rows
   * @throws IOException error
   */
  public static AppStoreSalesDailyReport parseReport(byte[] bytes, final String sku, String appName)
      throws IOException {
    AppStoreSalesDailyReport report = null;
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(bytes)))) {
      String line = br.readLine();
      if (Objects.nonNull(line)) {
        String[] values = line.split(LoadReportsUtils.TAB);
        report = AppStoreSalesDailyReport.createReport(values);
      }
      Objects.requireNonNull(report);
      while ((line = br.readLine()) != null) {
        String[] values = line.split(LoadReportsUtils.TAB);
        if (!StringUtils.equals(sku, values[report.indexSku])) {
          continue;
        }
        report.addRowData(values, appName);
      }
    }
    return report;
  }
}
