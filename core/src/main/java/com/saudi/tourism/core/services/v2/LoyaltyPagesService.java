package com.saudi.tourism.core.services.v2;

import com.saudi.tourism.core.models.app.page.PageInfo;

import javax.jcr.Node;
import java.util.List;

/**
 * This Service is defined for list of pages for Loyalty program .
 */
public interface LoyaltyPagesService {
  /**
   * Retrieves the list of pages.
   *
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @return list of pages
   */
  List<PageInfo> searchRecursivelyPropPres(Node node, List<PageInfo> searchResults);
}


