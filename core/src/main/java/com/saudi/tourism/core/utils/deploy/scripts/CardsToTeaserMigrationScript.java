package com.saudi.tourism.core.utils.deploy.scripts;

import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.saudi.tourism.core.utils.Constants.ROOT_CONTENT_PATH;

/**
 * Script to migrate Text Cards, City Cards to correspondent types.
 */
public class CardsToTeaserMigrationScript extends PagePropertyMigrationScript {

  /**
   * Where to search: /content/sauditourism.
   */
  private static final String PATH_TO_SEARCH = ROOT_CONTENT_PATH;

  /**
   * Property: sling:resourceType.
   */
  private static final String SLING_RESOURCE_TYPE =
      JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

  /**
   * Property to hold "Teaser with cards" resource type.
   */
  private static final String TEASER_RES_TYPE =
      "sauditourism/components/content/c27-teaser-with-cards/v1/c27-teaser-with-cards";

  /**
   * Property to hold "Text Cards" resource type.
   */
  private static final String TEXT_CARDS_RES_TYPE = "sauditourism/components/content/mod-te-02";

  /**
   * Property to hold "City Cards" resource type.
   */
  private static final String CITY_CARDS_RES_TYPE = "sauditourism/components/content/mod-te-10";

  /**
   * Property to hold "Destinations and map" resource type.
   */
  private static final String DESTINATIONS_RES_TYPE = "sauditourism/components/content/c28"
      + "-destinations-and-map/v1/c28-destinations-and-map";

  /**
   * Property componentType.
   */
  private static final String COMPONENT_TYPE = "componentType";


  /**
   * Transfer resource types and inner component types.
   *
   * @param foundNode    node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node foundNode, int pageNum) throws RepositoryException {
    logger.info("CardsToTeaser Script - execute started");

    boolean hasComponentType = foundNode.hasProperty(COMPONENT_TYPE);

    String oldResourceType = getStrProperty(foundNode, SLING_RESOURCE_TYPE, null);

    if (!hasComponentType) {
      if (oldResourceType.equals(TEASER_RES_TYPE)) {
        this.logger.info("Set componentType to a-general-cards of {}", foundNode.getPath());
        foundNode.setProperty(COMPONENT_TYPE, "a-general-cards");
      } else if (oldResourceType.equals(TEXT_CARDS_RES_TYPE)) {
        this.logger.info("Updating node at {} to resource type: {}.Set componentType to "
                + "b-text-cards.", foundNode.getPath(), TEASER_RES_TYPE);
        foundNode.setProperty(SLING_RESOURCE_TYPE, TEASER_RES_TYPE);
        foundNode.setProperty(COMPONENT_TYPE, "b-text-cards");
      } else if (oldResourceType.equals(CITY_CARDS_RES_TYPE)) {
        this.logger.info("Updating node at {} to resource type: {}.Set type to list.",
            foundNode.getPath(), DESTINATIONS_RES_TYPE);
        foundNode.setProperty(SLING_RESOURCE_TYPE, DESTINATIONS_RES_TYPE);
        foundNode.setProperty("type", "list");
      }
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

    queryMap.put("1_group.1_property", SLING_RESOURCE_TYPE);
    queryMap.put("1_group.1_property.value", TEASER_RES_TYPE);
    queryMap.put("1_group.2_property", SLING_RESOURCE_TYPE);
    queryMap.put("1_group.2_property.value", TEXT_CARDS_RES_TYPE);
    queryMap.put("1_group.3_property", SLING_RESOURCE_TYPE);
    queryMap.put("1_group.3_property.value", CITY_CARDS_RES_TYPE);
    queryMap.put("1_group.p.or", "true");
    return queryMap;
  }

}
