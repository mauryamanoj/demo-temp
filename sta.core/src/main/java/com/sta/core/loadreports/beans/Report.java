package com.sta.core.loadreports.beans;

import com.sta.core.loadreports.LoadReportsUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Rendition;
import com.day.cq.dam.api.DamConstants;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Report class.
 */
@Slf4j
public class Report {

  /**
   * Date format for export report.
   */
  public static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
  /**
   * Date format for export report.
   */
  public static final String IMPORTED_ROWS = "1";
  /**
   * String of '/00/00/00'.
   */
  public static final String STR_00_00_00 = "/00/00/00";
  /**
   * Report list of rows mapped to country code.
   */
  private final Map<String, List<ReportRow>> contryRowMap = new HashMap<>();

  /**
   * Add data.
   *
   * @param reportRow row of data
   */
  protected void addRow(ReportRow reportRow) {
    String countryCode = Objects.requireNonNull(reportRow.getCountryCode());
    List<ReportRow> list = contryRowMap.computeIfAbsent(countryCode, code -> new ArrayList<>());
    Optional<ReportRow> optionalReportRow = ReportRow.findFirstEqualsInList(list, reportRow);
    ReportRow row;
    if (optionalReportRow.isPresent()) {
      row = optionalReportRow.get();
      row.setInstallCount(row.getInstallCount() + reportRow.getInstallCount());
    } else {
      list.add(reportRow);
    }
  }

  /**
   * Get size.
   *
   * @return int size
   */
  public int size() {
    return contryRowMap.values().stream().mapToInt(List::size).sum();
  }

  /**
   * Get all rows.
   *
   * @return list of rows
   */
  public List<ReportRow> getRows() {
    return contryRowMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  /**
   * Generaate CSV file.
   *
   * @return csv file
   */
  public String createTabFile() {
    StringBuilder sb = new StringBuilder();
    contryRowMap.values().stream().flatMap(Collection::stream).forEach(
        row -> sb.append(row.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_MM_DD_YYYY)))
            .append(STR_00_00_00).append(LoadReportsUtils.TAB).append(row.getShop().getName()).append(LoadReportsUtils.TAB)
            .append(row.getAppName()).append(LoadReportsUtils.TAB).append(row.getAppVersion()).append(LoadReportsUtils.TAB)
            .append(row.getCountryCode()).append(LoadReportsUtils.TAB)
            .append(row.getInstallCount()).append(LoadReportsUtils.NEWLINE));
    return sb.toString();
  }

  /**
   * Generaate CSV file.
   *
   * @return csv file
   */
  public String createTabFileForAdobeAnalytic() {
    StringBuilder sb = new StringBuilder();
    sb.append("Date\tEvar 33\tEvar 34\tEvar 35\tEvar 36\tEvent 41\tEvent 42\n");
    contryRowMap.values().stream().flatMap(Collection::stream).forEach(
        row -> sb.append(row.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_MM_DD_YYYY)))
            .append(STR_00_00_00).append(LoadReportsUtils.TAB).append(row.getShop().getName()).append(LoadReportsUtils.TAB)
            .append(row.getAppName()).append(LoadReportsUtils.TAB).append(row.getAppVersion()).append(LoadReportsUtils.TAB)
            .append(row.getCountryCode()).append(LoadReportsUtils.TAB)
            .append(row.getInstallCount()).append(LoadReportsUtils.TAB).append(IMPORTED_ROWS).append(LoadReportsUtils.NEWLINE));
    return sb.toString();
  }

  /**
   * Load report from CRX.
   *
   * @param resolver resolver
   * @param path     path
   * @return report
   * @throws IOException error
   */
  public static Report loadFromCRX(ResourceResolver resolver, String path) throws IOException {
    AssetManager assetManager = Objects.requireNonNull(resolver.adaptTo(AssetManager.class));
    if (assetManager.assetExists(path)) {
      Asset asset = assetManager.getAsset(path);
      Iterator<? extends Rendition> it = asset.listRenditions();
      while (it.hasNext()) {
        Rendition rendition = it.next();
        if (DamConstants.ORIGINAL_FILE.equals(rendition.getName())) {
          Report report = new Report();
          try (BufferedReader br = new BufferedReader(
              new InputStreamReader(rendition.getStream()))) {
            String line;
            Objects.requireNonNull(report);
            while ((line = br.readLine()) != null) {
              String[] values = line.split(LoadReportsUtils.TAB);
              String dt = values[0].substring(0, values[0].indexOf('/', LoadReportsUtils.SIX));
              report.addRow(ReportRow.builder()
                  .date(LocalDate.parse(dt, DateTimeFormatter.ofPattern(DATE_FORMAT_MM_DD_YYYY)))
                  .appName(values[LoadReportsUtils.TWO]).shop(ReportShop.getByName(values[LoadReportsUtils.ONE]))
                  .appVersion(values[LoadReportsUtils.THREE]).countryCode(values[LoadReportsUtils.FOUR])
                  .installCount(Integer.parseInt(values[LoadReportsUtils.FIVE])).build());
            }
            return report;
          }
        }
      }
    }
    return null;
  }

  /**
   * Calculate difference between current report and another.
   * @param report2 another report
   * @return report of difference
   */
  public Report diff(Report report2) {
    List<ReportRow> rowList = new ArrayList<>();
    List<ReportRow> rowList1 = getRows();
    List<ReportRow> rowList2 = report2.getRows();

    rowList1.forEach(row -> {
      List<ReportRow> list = report2.contryRowMap.get(row.getCountryCode());
      Optional<ReportRow> optionalReportRow = ReportRow.findFirstEqualsInList(list, row);
      if (optionalReportRow.isPresent()) {
        ReportRow rr = optionalReportRow.get();
        if (row.getInstallCount() != rr.getInstallCount()) {
          ReportRow copy = row.copy();
          copy.setInstallCount(copy.getInstallCount() - rr.getInstallCount());
          rowList.add(copy);
        }
        rowList2.remove(rr);
      } else {
        rowList.add(row.copy());
      }
    });
    rowList2.forEach(reportRow -> rowList.add(reportRow.copy()));

    if (rowList.isEmpty()) {
      return null;
    }

    Report report3 = new Report();
    rowList.forEach(report3::addRow);
    return report3;
  }
}
