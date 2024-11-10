package com.sta.core.sitemap;


/**
 * Service responsible for generating sitemaps and sitemap indexes. Every
 * sitemap indexed is scoped to certain locale.
 */
public interface SitemapService {

  /**
   * Creates sitemaps for all rootpaths in given sector.
   *
   * @throws ServiceUserException the service user exception
   */
  void createSitemaps() throws ServiceUserException;

  /**
   * Creates sitemaps for specified sector/locale.
   *
   * @param locale the locale code
   * @throws ServiceUserException the service user exception
   */
  void createSitemap(String locale) throws ServiceUserException;
}
