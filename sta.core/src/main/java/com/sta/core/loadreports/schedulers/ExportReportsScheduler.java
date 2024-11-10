package com.sta.core.loadreports.schedulers;

import com.sta.core.loadreports.LoadReportsUtils;
import com.sta.core.loadreports.beans.Report;
import com.sta.core.loadreports.beans.ReportShop;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Objects;

import com.day.cq.dam.commons.util.DamUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sta.core.loadreports.services.UserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;

/**
 * The scheduler responsible for export reports to Adobe Analytics periodically.
 */
@Designate(ocd = ExportReportsScheduler.Config.class)
@Component(service = {Runnable.class, ExportReportsScheduler.class})
@Slf4j
public class ExportReportsScheduler implements Runnable {

  /**
   * The interface Config.
   */
  @ObjectClassDefinition(name = "MM Mobile App Reports - Scheduler config of report exporter to "
      + "Adobe Analytics and clean them",
                         description =
                             "A scheduler responsible for export reports to Adobe Analytics and clean"
                                 + " them")
  public static @interface Config {

    /**
     * Scheduler expression string.
     *
     * @return the string
     */
    @SuppressWarnings("java:S100") @AttributeDefinition(name = "Cron-job expression") String scheduler_expression() default "0 0 4 * * ? *";

    /**
     * Scheduler concurrent boolean.
     *
     * @return the boolean
     */
    @SuppressWarnings("java:S100") @AttributeDefinition(name = "Concurrent task",
                                                        description = "Whether or not to schedule this task concurrently") boolean scheduler_concurrent() default false;

    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    @AttributeDefinition(name = "Enabled",
                         description = "Whether scheduler to run or not") boolean enabled() default false;

    /**
     * username.
     *
     * @return username
     */
    @AttributeDefinition(name = "FTP user",
                         description = "username for Adobe FTP",
                         type = AttributeType.STRING) String username() default StringUtils.EMPTY;

    /**
     * username.
     *
     * @return username
     */
    @AttributeDefinition(name = "FTP password",
                         description = "password for Adobe FTP",
                         type = AttributeType.STRING) String password() default StringUtils.EMPTY;

    /**
     * remoteHost.
     *
     * @return remoteHost
     */
    @AttributeDefinition(name = "FTP hostname",
                         description = "hostname of Adobe FTP",
                         type = AttributeType.STRING) String remoteHost() default StringUtils.EMPTY;

    /**
     * remoteHost.
     *
     * @return remoteHost
     */
    @AttributeDefinition(name = "FTP port",
                         description = "port of Adobe FTP",
                         type = AttributeType.STRING) int remotePort() default LoadReportsUtils.SFTP_DEFAULT_PORT;

    /**
     * remotePath.
     *
     * @return remotePath
     */
    @AttributeDefinition(name = "FTP folder",
                         description = "folder of Adobe FTP",
                         type = AttributeType.STRING) String remotePath() default StringUtils.EMPTY;

    /**
     * appName.
     *
     * @return appName
     */
    @AttributeDefinition(name = "App name",
                         description = "Application name that set in export report",
                         type = AttributeType.STRING) String appName() default StringUtils.EMPTY;

    /**
     * Count of Day for download reports.
     *
     * @return remoteHost
     */
    @AttributeDefinition(name = "Download days",
                         description = "Days count for downloading reports. Default value: 7",
                         type = AttributeType.STRING) int dayCount() default LoadReportsUtils.SEVEN;

    /**
     * Day Count for clean export reports.
     *
     * @return remoteHost
     */
    @AttributeDefinition(name = "Actual days for exported reports",
                         description = "After that days them will be removed. default: 90. It "
                             + "have to great that dayCount!",
                         type = AttributeType.STRING) int dayCountExport() default LoadReportsUtils.CLEAN_REPORT_DAYS;

    /**
     * Day Count for clean.
     *
     * @return remoteHost
     */
    @AttributeDefinition(name = "Actual days for original reports",
                         description = "Actual days for original reports (checking the download "
                             + "date). After that days them will be cleaned. default: 90",
                         type = AttributeType.STRING) int dayCountDonwload() default LoadReportsUtils.CLEAN_REPORT_DAYS;
  }


  /**
   * The Enabled.
   */
  private boolean enabled;
  /**
   * username.
   */
  private String username;

  /**
   * password.
   */
  private String password;

  /**
   * remoteHost.
   */
  private String remoteHost;

  /**
   * remotePort.
   */
  private int remotePort;

  /**
   * remotePath.
   */
  private String remotePath;
  /**
   * appName.
   */
  @Getter
  private String appName;
  /**
   * dayCount.
   */
  @Getter
  private int dayCount;
  /**
   * dayCountExport.
   */
  private int dayCountExport;
  /**
   * dayCountDonwload.
   */
  private int dayCountDownload;

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * Sets up the service upon activation.
   *
   * @param config the OSGi component context
   */
  @Activate protected void activate(final Config config) {
    this.enabled = config.enabled();
    this.username = config.username();
    this.password = config.password();
    this.remoteHost = config.remoteHost();
    this.remotePort = config.remotePort();
    this.remotePath = config.remotePath();
    this.appName = config.appName();
    this.dayCountExport = config.dayCountExport();
    this.dayCountDownload = config.dayCountDonwload();
    this.dayCount = config.dayCount();
    if (dayCountExport < dayCount) {
      dayCountExport = dayCount;
    }
  }

  @Override public void run() {
    if (!this.enabled) {
      return;
    }
    LocalDate current = LocalDate.now();
    ChannelSftp channelSftp = null;
    try (ResourceResolver resolver = userService.getWritableResourceResolver()) {
      channelSftp = setupJsch(remoteHost, remotePort, username, password);
      channelSftp.connect();
      for (ReportShop shop : ReportShop.values()) {
        if (Objects.isNull(resolver.getResource(shop.getPath()))) {
          continue;
        }
        LocalDate dt = current.minusDays(dayCount);
        for (int i = 0; i < dayCount; i++, dt = dt.plusDays(1)) {
          doSend(dt, channelSftp, resolver, shop);
        }
      }
    } catch (JSchException e) {
      LOGGER.error("Error during connect to sftp ", e);
    } finally {
      if (channelSftp != null && channelSftp.isConnected()) {
        try {
          channelSftp.exit();
        } catch (Exception e) {
          LOGGER.warn(e.getMessage(), e);
        }
      }
    }
    try (ResourceResolver resolver = userService.getWritableResourceResolver()) {
      cleanExportReports(resolver, LocalDate.now().minusDays(dayCountExport));
      cleanDateFolderFrom(resolver, LoadReportsUtils.STATREPORT_DOWNLOAD_PATH,
          LocalDate.now().minusDays(dayCountDownload));
    } catch (Exception e) {
      LOGGER.error("Error during clean reports", e);
    }
  }

  /**
   * Do send reports to SFTP.
   *
   * @param dt          date
   * @param channelSftp sftp connection
   * @param resolver    resolver
   * @param shop        shop
   */
  private void doSend(LocalDate dt, ChannelSftp channelSftp, ResourceResolver resolver,
      ReportShop shop) {
    String dtformat = dt.format(DateTimeFormatter.ISO_LOCAL_DATE);
    LOGGER.info("Report exporting started for date [" + dtformat + "]");
    try {
      Resource dtFolderRes = resolver.getResource(PathUtils.concat(shop.getPath(), dtformat));
      if (Objects.isNull(dtFolderRes)) {
        LOGGER.info("No reports for date [" + dtformat + "]");
        return;
      }

      String lastGenReportPath = null;
      String lastSentReportPath = null;
      for (final Resource rr : dtFolderRes.getChildren()) {
        if (rr.getName().startsWith(LoadReportsUtils.EXPORT_REPORT_NAME_PREFIX) && rr.getName().endsWith(LoadReportsUtils.EXT_CSV)) {
          lastGenReportPath = rr.getPath();
          ValueMap valueMap = rr.getChild(JCR_CONTENT).getValueMap();
          boolean sync = valueMap.get(LoadReportsUtils.PROP_NAME_SENT, Boolean.FALSE);
          if (sync) {
            lastSentReportPath = rr.getPath();
          }
        }
      }
      if (Objects.isNull(lastGenReportPath) || StringUtils
          .equals(lastGenReportPath, lastSentReportPath)) {
        LOGGER.info("No export-reports for date [" + dtformat + "]");
        return;
      }

      Report reportLastGen = Report.loadFromCRX(resolver, lastGenReportPath);
      Report reportForSend = chooseReportForSend(
          resolver, reportLastGen, lastSentReportPath, dtformat);

      if (Objects.nonNull(reportForSend)) {
        String filename =
            "appstore_" + shop.getSource() + "_" + dt.format(LoadReportsUtils.DATETIME_FORMAT_YYYYMMDD) + "_"
                + System.currentTimeMillis();
        if (!remotePath.endsWith(LoadReportsUtils.FORWARD_SLASH)) {
          filename = LoadReportsUtils.FORWARD_SLASH + filename;
        }
        LOGGER.info("Uploading export-reports to ftp for date [" + dtformat + "]");
        try (InputStream in = new ByteArrayInputStream(
            reportForSend.createTabFileForAdobeAnalytic().getBytes(StandardCharsets.UTF_8))) {
          channelSftp.put(in, remotePath + filename + LoadReportsUtils.EXT_TXT);
        }
        try (InputStream in = new ByteArrayInputStream(new byte[0])) {
          channelSftp.put(in, remotePath + filename + LoadReportsUtils.EXT_FIN);
        }
      } else {
        LOGGER.info("No export-reports to ftp for date [" + dtformat + "]");
      }
      Resource res = resolver.getResource(lastGenReportPath);
      ModifiableValueMap valueMap = res.getChild(JCR_CONTENT).adaptTo(ModifiableValueMap.class);
      valueMap.put(LoadReportsUtils.PROP_NAME_SENT, true);
      DamUtil.setModified(res.adaptTo(com.day.cq.dam.api.Asset.class), resolver.getUserID(),
          new GregorianCalendar());
      resolver.commit();
      LOGGER.info("Report exported successfully for date [" + dtformat + "]");
    } catch (Exception e) {
      LOGGER.error("Error during export report for date [" + dtformat + "]", e);
    }
  }

  /**
   * Choose report for send
   * @param resolver resolver
   * @param reportLastGen reportLastGen
   * @param lastSentReportPath lastSentReportPath
   * @param dtformat dtformat
   * @return report
   * @throws IOException error
   */
  private Report chooseReportForSend(ResourceResolver resolver, Report reportLastGen,
      String lastSentReportPath, String dtformat) throws IOException {
    if (Objects.isNull(lastSentReportPath)) {
      return reportLastGen;
    }
    LOGGER.info("Calculating export-reports diff for date [" + dtformat + "]");
    Report reportLastSent = Report.loadFromCRX(resolver, lastSentReportPath);
    return reportLastGen.diff(reportLastSent);
  }

  /**
   * Setup connection.
   *
   * @return connection
   * @throws JSchException error
   */
  private ChannelSftp setupJsch(String remoteHost, int remotePort, String username, String password)
      throws JSchException {
    JSch jsch = new JSch();
    Session jschSession = jsch.getSession(username, remoteHost, remotePort);
    jschSession.setPassword(password);
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    jschSession.setConfig(config);
    jschSession.connect();
    return (ChannelSftp) jschSession.openChannel("sftp");
  }

  /**
   * Clean export reports.
   *
   * @param resolver resolver
   * @param dtRemove dates before that should be removed
   */
  private void cleanExportReports(ResourceResolver resolver, LocalDate dtRemove) {
    for (ReportShop shop : ReportShop.values()) {
      cleanDateFolderFrom(resolver, shop.getPath(), dtRemove);
    }
  }

  private void cleanDateFolderFrom(ResourceResolver resolver, String rootPath, LocalDate dtRemove) {
    Resource shopReportFolder = resolver.getResource(rootPath);
    if (Objects.isNull(shopReportFolder)) {
      return;
    }
    for (final Resource dtFolder : shopReportFolder.getChildren()) {
      try {
        LocalDate dt = LocalDate.parse(dtFolder.getName(), DateTimeFormatter.ISO_DATE);
        if (dt.isBefore(dtRemove)) {
          resolver.delete(dtFolder);
          resolver.commit();
        }
      } catch (Exception e) {
        LOGGER.error("during parse date from folder name", e);
      }
    }
  }
}
