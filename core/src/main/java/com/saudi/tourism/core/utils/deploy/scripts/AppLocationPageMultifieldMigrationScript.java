package com.saudi.tourism.core.utils.deploy.scripts;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * Script to move latitude/longitude properties into locations node (multifield) for
 * App Location pages (sauditourism/components/structure/app-location-page).
 */
public class AppLocationPageMultifieldMigrationScript extends PagePropertyMigrationScript {

  /**
   * Where to search: /content/sauditourism/app.
   */
  private static final String PATH_TO_SEARCH = Constants.APP_ROOT;

  /**
   * Look for all pages.
   */
  private static final String RESOURCES_TO_SEARCH = NameConstants.NT_PAGE;

  /**
   * Just local shortening for a slash constant.
   */
  private static final String SLASH = Constants.FORWARD_SLASH_CHARACTER;

  /**
   * Property 1: jcr:content/sling:resourceType.
   */
  private static final String GR_1_PROP_1_NAME = JcrConstants.JCR_CONTENT
      + SLASH + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

  /**
   * Property 1 value: sauditourism/components/structure/app-location-page.
   */
  private static final String GR_1_PROP_1_VALUE = Constants.APP_LOCATION_RESOURCE_TYPE;

  /**
   * Group 2 Prop 1 is jcr:content/latitude (should exist).
   */
  private static final String GR_2_PROP_1_NAME = JcrConstants.JCR_CONTENT
      + SLASH + Constants.LATITUDE;

  /**
   * Group 2 Prop 2 is jcr:content/longitude (should exist).
   */
  private static final String GR_2_PROP_2_NAME = JcrConstants.JCR_CONTENT
      + SLASH + Constants.LONGITUDE;

  /**
   * Node name "locations".
   */
  private static final String NN_LOCATIONS = "locations";

  /**
   * One location item node name (without a number).
   */
  private static final String NN_ITEM = "item";

  /**
   * Transfer latitude, longitude properties to locations node.
   *
   * @param foundNode    node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node foundNode, int pageNum) throws RepositoryException {
    final Node contentNode = foundNode.getNode(JcrConstants.JCR_CONTENT);

    if (contentNode == null) {
      return;
    }

    String latitude = getStrProperty(contentNode, Constants.LATITUDE, null);
    String longitude = getStrProperty(contentNode, Constants.LONGITUDE, null);

    logger.debug("Processing page ({}): [{}], Latitude: {}, Longitude: {}", pageNum,
        foundNode.getPath(), latitude, longitude);

    String newItemNodePath = StringUtils.EMPTY;
    Node locationItemNode = null;
    boolean skipPage = false;
    int itemNum = 0;

    // Look for proper place where to put new properties (locationItemNode or itemNum)
    if (contentNode.hasNode(NN_LOCATIONS)) {
      final NodeIterator itemsIterator = contentNode.getNode(NN_LOCATIONS).getNodes();

      while (itemsIterator.hasNext()) {
        itemNum++;

        final Node itemNode = (Node) itemsIterator.next();
        final String itemLat = getStrProperty(itemNode, Constants.LATITUDE, null);
        final String itemLon = getStrProperty(itemNode, Constants.LONGITUDE, null);

        if (StringUtils.equals(latitude, itemLat) && StringUtils.equals(longitude, itemLon)) {
          // This page is already ok, going to next
          skipPage = true;
          break;
        } else if (null == itemLat && null == itemLon && null == locationItemNode) {
          // Item's properties are empty, we can use it
          locationItemNode = itemNode;
        }
      }
    }

    if (skipPage) {
      logger.debug("Skipping page as already processed");
      return;
    }

    if (null == locationItemNode) {
      if (StringUtils.isEmpty(newItemNodePath)) {
        newItemNodePath = contentNode.getPath() + SLASH + NN_LOCATIONS + SLASH + NN_ITEM
            + itemNum;
      }

      locationItemNode = getOrCreateNode(newItemNodePath, JcrConstants.NT_UNSTRUCTURED);
    }

    locationItemNode.setProperty(Constants.LATITUDE, latitude);
    locationItemNode.setProperty(Constants.LONGITUDE, longitude);

    logger.debug("Properties have been successfully copied to: {}", locationItemNode.getPath());
  }

  /**
   * Prepares predicates map for searching pages query.
   *
   * @return predicates map for com.day.cq.search.QueryBuilder
   */
  protected Map<String, String> prepareQueryMap() {
    final Map<String, String> queryMap = new HashMap<>();

    // XPath Query:
    // /jcr:root/content/sauditourism/app//element(*, cq:Page)
    // [
    //   (jcr:content/@sling:resourceType = 'sauditourism/components/structure/app-location-page')
    //   and (jcr:content/@latitude or jcr:content/@longitude)
    // ]

    // Don't limit the search
    queryMap.put("p.limit", "-1");

    queryMap.put("path", PATH_TO_SEARCH);
    queryMap.put("type", RESOURCES_TO_SEARCH);

    // Resource type must be app-location-page
    queryMap.put("1_group.1_property", GR_1_PROP_1_NAME);
    queryMap.put("1_group.1_property.value", GR_1_PROP_1_VALUE);
    // By default operation is equals

    // latitude or longitude should exist among the page properties
    queryMap.put("2_group.1_property", GR_2_PROP_1_NAME);
    queryMap.put("2_group.1_property.operation", "exists");
    queryMap.put("2_group.1_property.value", "true");
    queryMap.put("2_group.2_property", GR_2_PROP_2_NAME);
    queryMap.put("2_group.2_property.operation", "exists");
    queryMap.put("2_group.2_property.value", "true");
    queryMap.put("2_group.p.or", "true");

    return queryMap;
  }

}
