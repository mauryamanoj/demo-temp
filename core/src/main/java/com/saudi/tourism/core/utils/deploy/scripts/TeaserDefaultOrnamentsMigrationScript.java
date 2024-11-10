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
public class TeaserDefaultOrnamentsMigrationScript extends PagePropertyMigrationScript {

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
   * Teaser's boolean field.
   */
  private static final String IS_COLOR_VARIANT = "isColorVariant";

  /**
   * Teaser's pattern for Text Cards.
   */
  private static final String PATTERN = "pattern";

  /**
   * Teaser's backgroundOrnamentId field for General and Article Cards.
   */
  private static final String BACKGROUND_ORNAMENT_ID = "backgroundOrnamentId";

  /**
   * TEXT_CARDS_DEFAULT_ORNAMENT.
   */
  public static final String TEXT_CARDS_DEFAULT_ORNAMENT = "03";

  /**
   * GENERAL_AND_ARTICLE_CARDS_DEFAULT_ORNAMENT.
   */
  public static final String GENERAL_AND_ARTICLE_CARDS_DEFAULT_ORNAMENT = "06";


  /**
   * Transfer resource types and inner component types.
   *
   * @param contendNode    node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node contendNode, int pageNum) throws RepositoryException {
    logger.info("TeaserDefaultOrnamentsMigrationScript - execute started");

    if (!contendNode.hasProperty(IS_COLOR_VARIANT)) {
      contendNode.setProperty(IS_COLOR_VARIANT, true);
    }

    if (!contendNode.hasProperty(PATTERN)) {
      contendNode.setProperty(PATTERN, TEXT_CARDS_DEFAULT_ORNAMENT);
    }

    if (!contendNode.hasProperty(BACKGROUND_ORNAMENT_ID)) {
      contendNode.setProperty(BACKGROUND_ORNAMENT_ID, GENERAL_AND_ARTICLE_CARDS_DEFAULT_ORNAMENT);
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
    return queryMap;
  }

}
