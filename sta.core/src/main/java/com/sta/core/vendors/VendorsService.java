package com.sta.core.vendors;


import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service responsible for generating sitemaps and sitemap indexes. Every
 * sitemap indexed is scoped to certain locale.
 */
public interface VendorsService {

  /**
   * Creates sitemaps for all rootpaths in given sector.
   *
   */
  void createVendorNodes(Map<String, Object> requestParameterMap,
      final ResourceResolver resourceResolver, String type);

  /**
   * Creates sitemaps for all rootpaths in given sector.
   *
   */
  void createVendorPages();
  /**
   * Creates sitemaps for all rootpaths in given sector.
   *
   */
  String writeToDam(String imageAsset, String dmcName);


}
