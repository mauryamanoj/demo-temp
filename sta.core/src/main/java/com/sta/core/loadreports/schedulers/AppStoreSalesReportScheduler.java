package com.sta.core.loadreports.schedulers;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sta.core.loadreports.LoadReportsUtils;
import com.sta.core.loadreports.beans.AppStoreSalesDailyReport;
import com.sta.core.loadreports.services.AppStoreConnectService;
import com.sta.core.loadreports.services.UserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.sta.core.loadreports.LoadReportsUtils.CONTENT_TYPE_CSV;
import static com.sta.core.loadreports.LoadReportsUtils.CRX_DATETIME_FORMATER;
import static com.sta.core.loadreports.LoadReportsUtils.EXPORT_REPORT_NAME_PREFIX;
import static com.sta.core.loadreports.LoadReportsUtils.EXT_CSV;
import static com.sta.core.loadreports.LoadReportsUtils.STATREPORT_APPSTORE_PATH;
import static com.sta.core.loadreports.LoadReportsUtils.STATREPORT_DOWNLOAD_PATH;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_ORDERED_FOLDER;

/**
 * The App Store sales report scheduler responsible for load reports periodically.
 */
@Designate(ocd = AppStoreSalesReportScheduler.Config.class)
@Component(service = Runnable.class)
@Slf4j
public class AppStoreSalesReportScheduler implements Runnable {

  /**
   * The interface Config.
   */
  @ObjectClassDefinition(name = "MM Mobile App Reports - Scheduler config of report loader from "
      + "App Store Connect",
                         description = "A scheduler responsible for loading reports from App Store"
                             + " Connect")
  @SuppressWarnings("java:S100")
  public static @interface Config {

    /**
     * Scheduler expression string.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Cron-job expression")
    String scheduler_expression() default "0 0 4 * * ? *";

    /**
     * Scheduler concurrent boolean.
     *
     * @return the boolean
     */
    @AttributeDefinition(name = "Concurrent task",
                         description = "Whether or not to schedule this task concurrently")
    boolean scheduler_concurrent() default false;

    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    @AttributeDefinition(name = "Enabled",
                         description = "Whether scheduler to run or not")
    boolean enabled() default false;

    /**
     * Vendor number.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Vendor number",
                         description = "Vendor number. Ex. '55555555'")
    String vendorNumber() default StringUtils.EMPTY;

    /**
     * Key ID.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Key Identifier",
                         description = "An account private key ID from App Store Connect (Ex: "
                             + "'2X9R4HXF34')") String keyId() default StringUtils.EMPTY;

    /**
     * Issuer.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Issuer Identifier",
                         description = "An account issuer ID from the API Keys page in App Store "
                             + "Connect (Ex: 57246542-96fe-1a63-e053-0824d011072a)")
    String issuer() default StringUtils.EMPTY;

    /**
     * Private key.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Private key",
                         description = "An account private key from App Store Connect "
                             + " in base64 format.") String privateKey() default StringUtils.EMPTY;

    /**
     * SKU.
     *
     * @return the string
     */
    @AttributeDefinition(name = "SKU",
                         description = "A product identifier provided by you during app setup.")
    String sku() default StringUtils.EMPTY;
  }

  /**
   * App Store Connect report date param name.
   */
  public static final String REQUEST_REPORT_DATE_PARAM = "filter[reportDate]";
  /**
   * App Store Connect report date param name.
   */
  public static final String REQUEST_REPORT_VENDOR_NUMBER_PARAM = "filter[vendorNumber]";
  /**
   * Request path.
   */
  public static final String REQUEST_PATH = "/v1/salesReports";
  /**
   * The Enabled.
   */
  private boolean enabled;

  /**
   * Params.
   */
  private Map<String, String> paramMap;

  /**
   * Key ID.
   */
  private String keyId;

  /**
   * Issuer.
   */
  private String issuer;

  /**
   * private key.
   */
  private PrivateKey privateKey;

  /**
   * SKU.
   */
  private String sku;

  /**
   * The User service.
   */
  @Reference
  private UserService userService;
  /**
   * ExportReportsScheduler.
   */
  @Reference
  private ExportReportsScheduler exportReportsScheduler;

  /**
   * The App Store Connect service used to generate the sitemaps.
   */
  @Reference
  private AppStoreConnectService appStoreConnectService;

  /**
   * Sets up the service upon activation.
   *
   * @param config the OSGi component context
   */
  @Activate protected void activate(final Config config) {
    this.enabled = config.enabled();
    this.keyId = config.keyId();
    this.issuer = config.issuer();

    this.paramMap = new HashMap<>();
    this.paramMap.put("filter[frequency]", "DAILY");
    this.paramMap.put("filter[reportType]", "SALES");
    this.paramMap.put("filter[reportSubType]", "SUMMARY");
    this.paramMap.put(REQUEST_REPORT_VENDOR_NUMBER_PARAM, config.vendorNumber());

    this.privateKey = null;
    if (!config.privateKey().isEmpty()) {
      try {
        KeyFactory factory = KeyFactory.getInstance("EC");
        byte[] bytes = Base64.getDecoder().decode(config.privateKey());
        this.privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        LOGGER.error("Error during initialization", e);
      }
    }
    this.sku = config.sku();
  }

  @Override public void run() {
    if (!enabled || this.sku.isEmpty()) {
      return;
    }
    LocalDate current = LocalDate.now();
    String currentFormat = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
    String currStrWithSec = LocalDateTime.now()
        .format(CRX_DATETIME_FORMATER);
    LocalDate dt = LocalDate.now();
    dt = dt.minusDays(exportReportsScheduler.getDayCount());
    try (ResourceResolver resolver = userService.getWritableResourceResolver()) {
      LOGGER.info("App Store load report scheduler started");
      LoadReportsUtils.checkReportFolder(resolver, STATREPORT_APPSTORE_PATH);

      for (int i = 0; i < exportReportsScheduler.getDayCount(); i++, dt = dt.plusDays(1)) {
        doLoadingReportForDate(resolver, dt, currStrWithSec, currentFormat);
      }
    } catch (Exception e) {
      LOGGER.error("Please configure App store report loader", e);
    } finally {
      LOGGER.info("App Store load report scheduler ended");
    }
  }

  private void doLoadingReportForDate(ResourceResolver resolver, LocalDate dt,
      final String loadId, String currentFormat) {
    String dtformat = dt.format(DateTimeFormatter.ISO_LOCAL_DATE);
    LOGGER.info("Report loading started for date [" + dtformat + "]");
    this.paramMap.put(REQUEST_REPORT_DATE_PARAM, dtformat);
    try {
      byte[] bytes = appStoreConnectService
          .loadReport(REQUEST_PATH, this.paramMap, this.keyId, this.issuer, this.privateKey);

      ResourceUtil.getOrCreateResource(resolver,
          PathUtils.concat(STATREPORT_DOWNLOAD_PATH, currentFormat), Collections.emptyMap(),
          NT_SLING_ORDERED_FOLDER, true);
      LoadReportsUtils.saveInDam(resolver,
          PathUtils.concat(STATREPORT_DOWNLOAD_PATH, currentFormat, "apple-"
              + dtformat + "-" + loadId + EXT_CSV),CONTENT_TYPE_CSV, bytes);

      AppStoreSalesDailyReport report = AppStoreSalesDailyReport.parseReport(bytes, this.sku,
          exportReportsScheduler.getAppName());
      String csv = report.createTabFile();
      ResourceUtil.getOrCreateResource(resolver, PathUtils.concat(STATREPORT_APPSTORE_PATH,
          dtformat), Collections.emptyMap(), NT_SLING_ORDERED_FOLDER, true);
      LoadReportsUtils.saveInDam(resolver, PathUtils.concat(STATREPORT_APPSTORE_PATH,
               dtformat, EXPORT_REPORT_NAME_PREFIX + loadId
              + EXT_CSV), CONTENT_TYPE_CSV, csv.getBytes(StandardCharsets.UTF_8));
      resolver.commit();
      LOGGER.info("Report loaded and saved successfully for date [" + dtformat + "]");
    } catch (Exception e) {
      LOGGER.error("Error during load report for date [" + dtformat + "]", e);
    }
  }
}
