package com.saudi.tourism.core.utils.deploy.scripts;

import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;
import static org.apache.sling.jcr.resource.api.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

/**
 * Script to move latitude/longitude properties into locations node (multifield) for
 * App Location pages (sauditourism/components/structure/app-location-page).
 */
@Generated
public class PackagesCityToAreaTagScript extends PagePropertyMigrationScript {

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
    String areaTag = getStrProperty(contentNode, "areaTag", null);

    logger.debug("Processing page ({}): [{}], Area Tag: {}", pageNum,
        contentNode.getPath(), areaTag);

    if (areaTag != null) {
      List<String> updatedvals = new ArrayList<>();
      updatedvals.add("sauditourism:city/" + getIdFromString(areaTag));
      contentNode.setProperty(Constants.PN_PACKAGE_AREA_TAGS,
          updatedvals.toArray(new String[updatedvals.size()]));
    }

  }

  /**
   * Gets id from string.
   *
   * @param text the text
   * @return the id from string
   */
  public String getIdFromString(final String text) {
    if (text == null) {
      return null;
    }
    return text.trim().replace(" ", "-");
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

    // Resource type must be app-location-page
    queryMap.put("1_property", SLING_RESOURCE_TYPE_PROPERTY);
    queryMap.put("1_property.value", "sauditourism/components/structure/package-detail-page");
    // By default operation is equals



    return queryMap;
  }

}
