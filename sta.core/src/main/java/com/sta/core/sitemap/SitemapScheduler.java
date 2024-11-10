package com.sta.core.sitemap;

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
@Designate(ocd = SitemapScheduler.Config.class)
@Component(service = Runnable.class)
@Slf4j
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SitemapScheduler implements Runnable {

  /**
   * The interface Config.
   */
  @ObjectClassDefinition(name = "Sitemap scheduler",
                         description = "A scheduler responsible for trigerring sitemap generation")
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
     * My parameter string.
     *
     * @return the string
     */
    @AttributeDefinition(name = "A parameter",
                         description = "Can be configured in /system/console/configMgr")
    String myParameter() default "";
  }


  /**
   * The Enabled.
   */
  private boolean enabled;

  /**
   * The sitemap service used to generate the sitemaps.
   */
  @Reference
  private SitemapService sitemapService;

  /**
   * The sitemap service used to generate the sitemaps.
   */
  @Reference
  private UmrahSitemapService umrahSitemapService;

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
      LOGGER.info("Sitemap generation started for website");
      try {
        sitemapService.createSitemaps();
        umrahSitemapService.createSitemaps();
      } catch (ServiceUserException e) {
        LOGGER.error("Please configure a sitemapUser for sitemap to generate");
      }
      LOGGER.info("Sitemap generation ended");
    }
  }

}
