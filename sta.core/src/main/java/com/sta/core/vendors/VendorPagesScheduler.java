package com.sta.core.vendors;

import lombok.extern.slf4j.Slf4j;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The sitemap scheduler responsible for regenerating the sitemap periodically.
 */
@Designate(ocd = VendorPagesScheduler.Config.class)
@Component(service = Runnable.class)
@Slf4j
public class VendorPagesScheduler implements Runnable {

  /**
   * The interface Config.
   */
  @ObjectClassDefinition(name = "Vendor Pages scheduler",
                         description = "A scheduler responsible for trigerring Google Sheet generation")
  @SuppressWarnings("java:S100")
  public static @interface Config {

    /**
     * Scheduler expression string.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Cron-job expression")
    String scheduler_expression() default "0 0 0 * 2000 ? *";

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
    boolean enabled() default true;

  }


  /**
   * The Enabled.
   */
  private boolean enabled;


  /**
   * The vendorsService.
   */
  @Reference
  private VendorsService vendorsService;

  /**
   * Sets up the service upon activation.
   *
   * @param config the OSGi component context
   */
  @Activate protected void activate(final Config config) {
    enabled = config.enabled();
  }

  @Override public void run() {
    if (enabled) {
      LOGGER.info("Vendor Page generation started");
      try {
        vendorsService.createVendorPages();
      } catch (Exception e) {
        LOGGER.error("Please configure a sitemapUser for Google Sheet to generate");
      }
      LOGGER.info("Vendor Page generation ended");
    }
  }

}
