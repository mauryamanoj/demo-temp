package com.sta.core.loadreports.schedulers;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sta.core.loadreports.LoadReportsUtils;
import com.sta.core.loadreports.beans.GooglePlayInstallReport;
import com.sta.core.loadreports.services.GoogleStorageService;
import com.sta.core.loadreports.services.UserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.sta.core.loadreports.LoadReportsUtils.CONTENT_TYPE_CSV;
import static com.sta.core.loadreports.LoadReportsUtils.CRX_DATETIME_FORMATER;
import static com.sta.core.loadreports.LoadReportsUtils.EXPORT_REPORT_NAME_PREFIX;
import static com.sta.core.loadreports.LoadReportsUtils.EXT_CSV;
import static com.sta.core.loadreports.LoadReportsUtils.STATREPORT_DOWNLOAD_PATH;
import static com.sta.core.loadreports.LoadReportsUtils.STATREPORT_GOOGLEPLAY_PATH;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_ORDERED_FOLDER;

/**
 * The Google Play report scheduler responsible for load reports periodically.
 */
@Designate(ocd = GooglePlayReportScheduler.Config.class)
@Component(service = Runnable.class)
@Slf4j
public class GooglePlayReportScheduler implements Runnable {

  /**
   * Google stat report path format.
   */
  private static final String PATH_FORMAT =
      "/stats/installs/installs_%s_%d%02d_country.csv";
  /**
   * CSV filename format.
   */
  public static final String GOOGLE_CSV_FORMAT = "google-%d%02d-%s.csv";

  /**
   * The interface Config.
   */
  @ObjectClassDefinition(name = "MM Mobile App Reports - Scheduler config of report loader from "
      + "Google Play",
                         description = "A scheduler responsible for load reports from Google Play")
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
     * Bucket name.
     *
     * @return String bucket name
     */
    @AttributeDefinition(name = "Bucket name",
                         description = "The value like a 'pubsite_prod_rev_0178873...'",
                         type = AttributeType.STRING) String bucketName() default StringUtils.EMPTY;
    /**
     * Package name.
     *
     * @return String package name
     */
    @AttributeDefinition(name = "Package name",
                         description = "The value like a 'sa.gov.apps.sauditourism'",
                         type = AttributeType.STRING) String packageName() default StringUtils.EMPTY;

    /**
     * Service account .
     *
     * @return String E-mail address of the service account
     */
    @AttributeDefinition(name = "Service account",
                         description = "E-mail address of the service account",
                         type = AttributeType.STRING)
    String serviceAccount() default StringUtils.EMPTY;

    /**
     * Service account private key.
     *
     * @return String private key
     */
    @AttributeDefinition(name = "Service account private key",
                         description = "Service account private key in base64",
                         type = AttributeType.STRING) String privateKey() default StringUtils.EMPTY;
  }

  /**
   * The Enabled.
   */
  private boolean enabled;
  /**
   * Bucket name.
   */
  private String bucketName;
  /**
   * Package name.
   */
  private String packageName;

  /**
   * Service account.
   */
  private String serviceAccount;

  /**
   * Service account private key.
   */
  private PrivateKey privateKey;
  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * Google Storage Service.
   */
  @Reference
  private GoogleStorageService googleStorageService;
  /**
   * ExportReportsScheduler.
   */
  @Reference
  private ExportReportsScheduler exportReportsScheduler;

  /**
   * Sets up the service upon activation.
   *
   * @param config the OSGi component context
   */
  @Activate protected void activate(final Config config) {
    this.enabled = config.enabled();
    this.bucketName = config.bucketName();
    this.serviceAccount = config.serviceAccount();
    this.packageName = config.packageName();

    if (StringUtils.isNotBlank(config.privateKey())) {
      try {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        byte[] bytes = Base64.getDecoder().decode(config.privateKey());
        this.privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
      } catch (Exception e) {
        this.privateKey = null;
        LOGGER.error("Error during initialization of private key", e);
      }
    } else {
      this.privateKey = null;
    }
  }

  @Override public void run() {
    if (!enabled) {
      return;
    }
    String currStr = LocalDateTime.now().format(CRX_DATETIME_FORMATER);
    try (ResourceResolver resolver = userService.getWritableResourceResolver()) {
      LOGGER.info("Google Play load report scheduler started");
      LoadReportsUtils.checkReportFolder(resolver, STATREPORT_GOOGLEPLAY_PATH);

      LocalDate current = LocalDate.now();
      String currentFormat = current.format(DateTimeFormatter.ISO_LOCAL_DATE);
      LocalDate dt = LocalDate.now();
      dt = dt.minusDays(exportReportsScheduler.getDayCount());

      while ((dt.getYear() < current.getYear()) ||
          (dt.getYear() == current.getYear() && dt.getMonthValue() <= current.getMonthValue())) {
        String path = String.format(PATH_FORMAT, this.packageName, dt.getYear(),
            dt.getMonthValue());
        byte[] bytes = googleStorageService.download(bucketName, serviceAccount, privateKey, path);
        ResourceUtil.getOrCreateResource(resolver, PathUtils.concat(STATREPORT_DOWNLOAD_PATH,
            currentFormat), Collections.emptyMap(), NT_SLING_ORDERED_FOLDER, true);
        LoadReportsUtils.saveInDam(resolver,
            PathUtils.concat(STATREPORT_DOWNLOAD_PATH, currentFormat,
                String.format(GOOGLE_CSV_FORMAT, dt.getYear(), dt.getMonthValue(), currStr)),
            CONTENT_TYPE_CSV, bytes);

        Map<LocalDate, GooglePlayInstallReport> reportMap = GooglePlayInstallReport.parseReport(bytes,
            LocalDate.now().minusDays(exportReportsScheduler.getDayCount()),
            exportReportsScheduler.getAppName());
        List<Map.Entry<LocalDate, GooglePlayInstallReport>> reports =
            new ArrayList<>(reportMap.entrySet());
        reports.sort((e1, e2) -> e1.getKey().compareTo(e2.getKey()));
        for (final Map.Entry<LocalDate, GooglePlayInstallReport> entry: reports) {
          String dtformat = entry.getKey().format(DateTimeFormatter.ISO_LOCAL_DATE);
          ResourceUtil.getOrCreateResource(resolver, PathUtils.concat(STATREPORT_GOOGLEPLAY_PATH,
              dtformat), Collections.emptyMap(), NT_SLING_ORDERED_FOLDER, true);
          String csv = entry.getValue().createTabFile();
          LoadReportsUtils.saveInDam(resolver,PathUtils.concat(STATREPORT_GOOGLEPLAY_PATH,
                  dtformat, EXPORT_REPORT_NAME_PREFIX + currStr
                  + EXT_CSV), CONTENT_TYPE_CSV, csv.getBytes(StandardCharsets.UTF_8));
        }
        resolver.commit();
        dt = dt.plusMonths(1);
      }
      LOGGER.info("Report loaded and saved successfully.");
    } catch (Exception e) {
      LOGGER.error("Please configure Google Play report loader", e);
    } finally {
      LOGGER.info("Google Play load report scheduler ended");
    }
  }
}
