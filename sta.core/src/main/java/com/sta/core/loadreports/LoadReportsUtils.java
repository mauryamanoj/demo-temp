package com.sta.core.loadreports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Objects;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.RenditionHandler;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.google.common.collect.ImmutableMap;
import com.sta.core.MmCoreException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Constants.
 */
public class LoadReportsUtils {

  /**
   * Private constructor.
   */
  private LoadReportsUtils() {
  }

  /**
   * The constant unknown.
   */
  public static final String UNKNOWN = "unknown";

  /**
   * The constant ZERO.
   */
  public static final int ZERO = 0;
  /**
   * The constant ONE.
   */
  public static final int ONE = 1;
  /**
   * The constant TWO.
   */
  public static final int TWO = 2;
  /**
   * The constant THREE.
   */
  public static final int THREE = 3;
  /**
   * The constant FOUR.
   */
  public static final int FOUR = 4;
  /**
   * The constant FIVE.
   */
  public static final int FIVE = 5;
  /**
   * The constant SIX.
   */
  public static final int SIX = 6;
  /**
   * The constant SEVEN.
   */
  public static final int SEVEN = 7;

  /**
   * Forwaard slash.
   */
  public static final String FORWARD_SLASH = "/";
  /**
   * The constant EQUAL.
   */
  public static final String EQUAL = "=";
  public static final String STRING_ONE = "1";
  /**
   * The constant EQUAL.
   */
  public static final String TAB = "\t";
  /**
   * The constant EQUAL.
   */
  public static final String NEWLINE = "\n";
  /**
   * The constant COMMA.
   */
  public static final String COMMA = ",";
  /**
   * Default port of sftp.
   */
  public static final int SFTP_DEFAULT_PORT = 22;
  /**
   * Default port of sftp.
   */
  public static final int CLEAN_REPORT_DAYS = 90;
  /**
   * The constant SERVICE_DESCRIPTION.
   */
  public static final String SERVICE_DESCRIPTION = org.osgi.framework.Constants.SERVICE_DESCRIPTION;
  /**
   * The constant WRITE_SERVICE.
   */
  public static final String WRITE_SERVICE = "writeservice";

  /**
   * Root path of saudi tourism stat reports.
   */
  public static final String SAUDITOURISM_STAT_REPORT_PATH = "/var/install-stats-reports";
  /**
   * Root path of app store stat reports.
   */
  public static final String STATREPORT_APPSTORE_PATH =
      SAUDITOURISM_STAT_REPORT_PATH + FORWARD_SLASH + "app-store";
  /**
   * Root path of google play stat reports.
   */
  public static final String STATREPORT_GOOGLEPLAY_PATH =
      SAUDITOURISM_STAT_REPORT_PATH + FORWARD_SLASH + "google-play";
  /**
   * Root path of google play stat reports.
   */
  public static final String STATREPORT_HUAWEI_PATH =
      SAUDITOURISM_STAT_REPORT_PATH + FORWARD_SLASH + "huawei";
  /**
   * Root path of download stat reports.
   */
  public static final String STATREPORT_DOWNLOAD_PATH =
      SAUDITOURISM_STAT_REPORT_PATH + FORWARD_SLASH + "downloads";
  /**
   * the text 'Couldn't found the column.
   */
  public static final String COULDN_T_FOUND_THE_COLUMN = "Couldn't found the column ";
  /**
   * Root app's sales report update days.
   */
  public static final int REPORT_LOADING_DAYS_COUNT = 7;

  /**
   * DateTimeFormatter for crx node names.
   */
  public static final DateTimeFormatter CRX_DATETIME_FORMATER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
  /**
   * DateTimeFormatter for 'yyyyMMdd'.
   */
  public static final DateTimeFormatter DATETIME_FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern(
      "yyyyMMdd");
  /**
   * Export reports prefix of name.
   */
  public static final String EXPORT_REPORT_NAME_PREFIX = "report-export-";
  /**
   * CVS file extention.
   */
  public static final String EXT_CSV = ".csv";
  /**
   * txt file extention.
   */
  public static final String EXT_TXT = ".txt";
  /**
   * txt file extention.
   */
  public static final String EXT_FIN = ".fin";
  /**
   * Content type text/csv.
   */
  public static final String CONTENT_TYPE_CSV = "text/csv";
  /**
   * Property name 'sent'.
   */
  public static final String PROP_NAME_SENT = "sent";

  /**
   * Save in DAM.
   *
   * @param resolver  - resource resolver
   * @param path  - path
   * @param bytes - file bytes
   * @throws IOException - error
   */
  public static void saveInDam(ResourceResolver resolver, String path, String mimeType,
      byte[] bytes)
      throws IOException {
    final AssetManager assetManager = resolver.adaptTo(AssetManager.class);
    if (assetManager != null) {
      Asset asset;
      if (assetManager.assetExists(path)) {
        asset = assetManager.getAsset(path);
        asset.listRenditions()
            .forEachRemaining(rendition -> asset.removeRendition(rendition.getName()));
      } else {
        asset = assetManager.createAsset(path);
      }
      try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
        if (StringUtils.isNotBlank(mimeType)) {
          asset.setRendition(DamConstants.ORIGINAL_FILE, in,
              ImmutableMap.of(RenditionHandler.PROPERTY_RENDITION_MIME_TYPE, mimeType));
        }
        DamUtil.setModified(asset.adaptTo(com.day.cq.dam.api.Asset.class), resolver.getUserID(),
            new GregorianCalendar());
      }
    }
  }

  /**
   * Check path.
   * @param resolver - ResourceResolver
   * @param path path
   * @throws MmCoreException - if doesn't exist
   */
  public static void checkReportFolder(ResourceResolver resolver, String path) throws
      MmCoreException {
    try {
      Objects.requireNonNull(resolver.getResource(path));
    } catch (Exception e) {
      throw new MmCoreException("The report path [" + path + "] is null. "
          + "The path is not exist or not accessible", e);
    }
  }

  /**
   * Check on not negative or throw exception.
   *
   * @param value      checked value
   * @param columnName - column name
   */
  public static int requireNotNegative(int value, String columnName) {
    if (value < 0) {
      throw new IllegalArgumentException(COULDN_T_FOUND_THE_COLUMN + columnName);
    }
    return value;
  }
}
