package com.saudi.tourism.core.utils.deploy.scripts;

import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

/**
 * Script to set as season property if it's absent for event pages.
 */
@Generated
public class EventsAllSeasonsOnDeployScript extends PagePropertyMigrationScript {

  /**
   * Where to search: /content/sauditourism.
   */
  private static final String PATH_TO_SEARCH = Constants.SITE_ROOT;

  /**
   * Property name to set.
   */
  private static final String SEASON = "season";

  /**
   * Property value to set.
   */
  private static final String ALL_SEASONS = "all-seasons";

  /**
   * Set all-seasons value for null season.
   * @param contentNode node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node contentNode, int pageNum) throws RepositoryException {

    logger.info("Processing page ({}): [{}]", pageNum, contentNode.getPath());

    if (!contentNode.hasProperty(SEASON)) {
      contentNode.setProperty(SEASON, ALL_SEASONS);
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
    queryMap.put("type", TYPE_PAGE_CONTENT);

    // Resource type must be event-detail-page
    queryMap.put("1_property", SLING_RESOURCE_TYPE_PROPERTY);
    queryMap.put("1_property.value", "sauditourism/components/structure/event-detail-page");
    // By default operation is equals



    return queryMap;
  }

}
