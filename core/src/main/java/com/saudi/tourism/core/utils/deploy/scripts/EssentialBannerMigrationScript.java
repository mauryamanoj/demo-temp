package com.saudi.tourism.core.utils.deploy.scripts;

import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.saudi.tourism.core.utils.Constants.PN_VARIANT;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

/**
 * Script to add field 'variant' for old/existing component 'Banner Link'.
 */
@Generated
public class EssentialBannerMigrationScript extends PagePropertyMigrationScript {

  /**
   * Where to search: /content/sauditourism.
   */
  private static final String PATH_TO_SEARCH = Constants.SITE_ROOT;

  /**
   * Transfer latitude, longitude properties to locations node.
   * @param contentNode node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node contentNode, int pageNum) throws RepositoryException {
    boolean isFullWidth = Optional.ofNullable(contentNode.getProperty("isFullWidth"))
        .map(p -> {
          try {
            return p.getBoolean();
          } catch (RepositoryException e) {
            logger.error("Error during migration of Banner Link ", e);
            return false;
          }
        }).orElse(false);
    if (isFullWidth) {
      contentNode.setProperty(PN_VARIANT, "full-width");
    } else {
      contentNode.setProperty(PN_VARIANT, "grid-small");
    }
  }

  /**
   * Prepares predicates map for searching pages query.
   *
   * @return predicates map for com.day.cq.search.QueryBuilder
   */
  protected Map<String, String> prepareQueryMap() {
    final Map<String, String> queryMap = new HashMap<>();


    // Don't limit the search
    queryMap.put("p.limit", "-1");

    queryMap.put("path", PATH_TO_SEARCH);
    queryMap.put("type", NT_UNSTRUCTURED);

    // Resource type must be essential-links
    queryMap.put("1_property", SLING_RESOURCE_TYPE_PROPERTY);
    queryMap.put("1_property.value", "sauditourism/components/content/essential-links");
    // Resource type must be essential-links
    queryMap.put("2_property", PN_VARIANT);
    queryMap.put("2_property.operation", "exists");
    queryMap.put("2_property.value", "false");

    return queryMap;
  }
}
