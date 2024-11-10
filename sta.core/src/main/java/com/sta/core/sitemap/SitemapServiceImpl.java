package com.sta.core.sitemap;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.sta.core.sitemap.model.LinkEntry;
import com.sta.core.sitemap.model.SitemapEntry;
import com.sta.core.sitemap.model.XmlEntry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * The service responsible for generating sitemaps. The services uses
 * to generate actual sitemap files.
 */
@Component(service = SitemapService.class,
           immediate = true)
@Slf4j
public class SitemapServiceImpl implements SitemapService {

  /**
   * The Sitemap Configuration.
   */
  @ObjectClassDefinition(name = "Sitemap Configuration")
  @SuppressWarnings({"common-java:DuplicatedBlocks"})
  @interface Configuration {

    /**
     * Retrieve the Sitemap domain.
     *
     * @return String Sitemap domain
     */
    @AttributeDefinition(name = "Sitemap domain",
                         type = AttributeType.STRING) String domain() default StringUtils.EMPTY;

    /**
     * Retrieve the Root Path.
     *
     * @return String Root Path
     */
    @AttributeDefinition(name = "Root Path",
                         type = AttributeType.STRING) String rootPath() default StringUtils.EMPTY;

    /**
     * Retrieve the Include Templates.
     *
     * @return String Include Templates
     */
    @AttributeDefinition(name = "Include Templates",
                         type = AttributeType.STRING) String[] includeTemplates() default {
        "/conf/sauditourism/settings/wcm/templates/content-page"};

    /**
     * Exclude Paths.
     *
     * @return String Exclude Paths
     */
    @AttributeDefinition(name = "Exclude Paths",
      type = AttributeType.STRING) String[] excludePaths() default {
      "/content/sauditourism/en/Configs", "/content/sauditourism/es/Configs", "/content/sauditourism/ru/Configs",
      "/content/sauditourism/ja/Configs", "/content/sauditourism/de/Configs", "/content/sauditourism/fr/Configs",
      "/content/sauditourism/ar/Configs", "/content/sauditourism/zh/Configs"};

  }


  /**
   * The resource resolver factory.
   */
  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  /**
   * The Locales.
   */
  private Set<String> locales;
  /**
   * The Domain.
   */
  private String domain;
  /**
   * The rootPath.
   */
  private String rootPath;

  /**
   * The include templates.
   */
  private List<String> includeTemplates;

  /**
   * Exclude Paths.
   */
  private List<String> excludePaths;

  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param config Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration config) {
    LOGGER.debug("Saudi Configurations Activate/Modified");

    this.domain = config.domain();
    this.rootPath = config.rootPath();
    this.includeTemplates = Arrays.asList(config.includeTemplates());
    this.excludePaths = Arrays.asList(config.excludePaths());
  }

  @Override public void createSitemaps() throws ServiceUserException {
    long startTime = System.currentTimeMillis();
    ResourceResolver resolver = null;
    try {
      resolver = getWriteResolver();
      locales = getListOfLocales(resolver);

      for (String locale : locales) {
        createSitemap(locale, resolver);
      }
      LOGGER.info("Sitemap generation for all locales took {} ms",
          System.currentTimeMillis() - startTime);
    } finally {
      if (resolver.isLive()) {
        resolver.close();
      }
    }
  }

  @Override public void createSitemap(String locale) throws ServiceUserException {
    ResourceResolver resolver = null;
    try {
      resolver = getWriteResolver();
      createSitemap(locale, resolver);
    } finally {
      if (resolver.isLive()) {
        resolver.close();
      }
    }
  }

  /**
   * Creates sitemaps for specified sector/locale.
   *
   * @param locale   the locale code
   * @param resolver ResourceResolver
   * @throws ServiceUserException the service user exception
   */
  private void createSitemap(String locale, ResourceResolver resolver) throws ServiceUserException {
    long startTime = System.currentTimeMillis();
    try {
      final SitemapWriter writer = new SitemapWriter(rootPath + "/" + locale, resolver);

      PageManager pageManager = resolver.adaptTo(PageManager.class);

      @SuppressWarnings("squid:S2259")
      Page page = pageManager.getPage(rootPath + "/" + locale);
      createEntries(page, writer, resolver);

      writer.flushSitemap(true);
      LOGGER.info("Sitemap generation for {} took {} ms",
          new Object[] {locale, System.currentTimeMillis() - startTime});
    } catch (NullPointerException | SlingException e) {
      LOGGER
          .error("Sitemap generation for {} {} got error: {}", new Object[] {rootPath, locale, e});
    }

  }

  /**
   * Creates sitemap entries for given page. Based on the page template the method
   * may return more than 1 entry (if template supports selectors).
   *
   * @param page     the page
   * @param writer   the writer
   * @param resolver ResourceResolver
   */
  protected List<XmlEntry> createEntries(Page page, SitemapWriter writer, ResourceResolver resolver) {
    Iterator<Page> pages = page.listChildren();
    while (pages.hasNext()) {
      Page childPage = pages.next();

      if (Objects.nonNull(childPage) && !childPage.isHideInNav() && checkTemplate(childPage) && checkPath(childPage.getPath())) {
        writer.addEntries(createEntry(childPage, null, resolver));
      }
      createEntries(childPage, writer, resolver);
    }

    return writer.getEntries();
  }

  /**
   * Check if the path is under an excluded parent.
   * @param path Current page path
   * @return True if the current path is not under an excluded parent, False otherwise
   */
  private boolean checkPath(@NonNull final String path) {
    return excludePaths.stream()
      .noneMatch(p -> StringUtils.startsWith(path, p));
  }

  /**
   * check if its not a placeholder templates.
   *
   * @param childPage the child page
   * @return boolean
   */
  private boolean checkTemplate(Page childPage) {
    try {
      if (Objects.nonNull(childPage.getProperties().get(NameConstants.NN_TEMPLATE).toString())) {
        return includeTemplates
            .contains(childPage.getProperties().get(NameConstants.NN_TEMPLATE).toString());
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Creates sitemap entry for given page/selector.
   *
   * @param page      the page to generate entry for
   * @param baseEntry the base entry (selector-less), null for actual base entries the
   *                  below methods create a list of pages in sitemap .it also check if
   *                  the pages is part of msm and also it check if the page present in
   *                  other locale then it get added as alternate page. it will consist
   *                  all the locale present under country admin which has site type
   *                  internal and generic and show site yes.
   * @param resolver  ResourceResolver
   * @return the sitemap entry
   */
  private SitemapEntry createEntry(Page page, SitemapEntry baseEntry, ResourceResolver resolver) {
    SitemapEntry entry = new SitemapEntry();
    entry.setLoc(rewriteUrl(page, resolver));
    entry.setLastModified(page.getLastModified());

    if (baseEntry != null) {
      entry.setLastModified(baseEntry.getLastModified());
      entry.setCreated(baseEntry.getCreated());
    }

    String currentLocale = StringUtils.split(page.getPath(), '/')[2];

    if (locales == null) {
      //for servlet built
      locales = getListOfLocales(resolver);
    }
    // logic to add alternate link for different lang pages
    for (String alternateLocale : locales) {
      String alternatePagePath = getAlternatePath(page, currentLocale, alternateLocale);
      Page alternatePage = page.getPageManager().getPage(alternatePagePath);
      if (alternatePage != null && !alternateLocale.equals(currentLocale)) {
        boolean hideInAltLocales = alternatePage.isHideInNav();
        if (!hideInAltLocales) {
          addAlternateUrl(entry, alternateLocale, rewriteUrl(alternatePage, resolver));
        }
      }
    }
    return entry;
  }

  /**
   * Creates externalizes url for the page.
   *
   * @param page     the page to create external url for
   * @param resolver ResourceResolver
   * @return the externalized url
   */
  private String rewriteUrl(Page page, ResourceResolver resolver) {
    // We are using /etc/map mapping
    // The domain will be added when calling resolver.map
    return resolver.map(page.getPath());
  }

  /**
   * Gets the set of available locales configured under /content/{website}.
   *
   * @param resolver ResourceResolver
   * @return the set of locale codes
   */
  private Set<String> getListOfLocales(ResourceResolver resolver) {
    Set<String> siteLocales = new TreeSet<>();

    try {
      if (Objects.isNull(resolver)) {
        return siteLocales;
      }
      Resource rootRespurce = resolver.getResource(rootPath);
      if (rootRespurce != null) {
        Iterator<Resource> rootLocales = rootRespurce.listChildren();
        while (rootLocales.hasNext()) {
          Resource localeResource = rootLocales.next();
          String localeName = localeResource.getName();
          if (!JcrConstants.JCR_CONTENT.equals(localeName)) {
            siteLocales.add(localeName);
          }
        }
      }
      LOGGER.debug("Retrieved {} locales", siteLocales.size());
    } catch (NullPointerException e) {
      LOGGER.error("Unable to get ResourceResolver for retrieving list of locales", e);
    }
    return siteLocales;
  }

  /**
   * Creates a path to the page in the alternate locale.
   *
   * @param page            the current page
   * @param currentLocale   the current locale
   * @param alternateLocale the alternate locale to create the page path for
   * @return the path to the same page in alternate locale
   */
  private String getAlternatePath(Page page, String currentLocale, String alternateLocale) {
    return StringUtils.replaceOnce(page.getPath(), currentLocale, alternateLocale);
  }

  /**
   * Adds and alternate locale to the sitemap entry. In case of regional locales,
   * a set of links will be added (1 for each country in the region).
   *
   * @param entry  the sitemap entry to add alternate locales to
   * @param locale the CQ locale code of the alternate page
   * @param url    the url of the alternate page
   */
  private void addAlternateUrl(SitemapEntry entry, String locale, String url) {
    LinkEntry link = new LinkEntry();
    link.setHreflang(locale);
    link.setHref(url);
    entry.getLinks().add(link);
  }

  /**
   * Get the Write resolver.
   *
   * @return write resolver
   * @throws ServiceUserException the service user exception
   */
  private ResourceResolver getWriteResolver() throws ServiceUserException {
    ResourceResolver resolver = null;
    try {
      Map<String, Object> writeMap = new HashMap<>();
      writeMap.put(ResourceResolverFactory.SUBSERVICE, "sitemapUser");
      resolver = resourceResolverFactory.getServiceResourceResolver(writeMap);
    } catch (LoginException e) {
      LOGGER.error("Error in getting getServiceResourceResolver {}", e);
    }

    if (Objects.isNull(resolver)) {
      throw new ServiceUserException("sitemapUser cannot be found");
    }
    return resolver;
  }
}
