package com.saudi.tourism.core.utils.deploy.scripts;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Packages content page migration script.
 */
@Generated
public class PackagesContentPageMigrationScript extends PagePropertyMigrationScript {
  /**
   * Where to search: /content/sauditourism/app.
   */
  private static final String PATH_TO_SEARCH = Constants.ROOT_CONTENT_PATH;

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
  private static final String RESOURCE_TYPE =
      JcrConstants.JCR_CONTENT + SLASH + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

  /**
   * Property 1 value: sauditourism/components/structure/app-location-page.
   */
  private static final String PAGE = Constants.PAGE_RESOURCE_TYPE;

  /**
   * Content Page Template.
   */
  private static final String APP_PACKAGE_PAGE_TEMPLATE =
      "/conf/sauditourism/settings/wcm/templates/app-package-page";

  /**
   * Packages Content Page Template.
   */
  private static final String PACKAGES_CONTENT_PAGE_TEMPLATE =
      "/conf/sauditourism/settings/wcm/templates/packages-content-page";

  /**
   * Process properties assignment.
   *
   * @param node       current node
   * @param pageNumber current page index
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node node, int pageNumber) throws RepositoryException {
    Node contentNode = node.getNode(JcrConstants.JCR_CONTENT);
    String template = getStrProperty(contentNode, NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
    logger.debug("Processing page ({}): [{}], Template: {}", pageNumber, node.getPath(), template);

    if (!StringUtils.isEmpty(template) && template.equals(APP_PACKAGE_PAGE_TEMPLATE)) {
      contentNode.setProperty(NameConstants.NN_TEMPLATE, PACKAGES_CONTENT_PAGE_TEMPLATE);
    }
  }

  /**
   * Prepares predicates map for searching pages query.
   *
   * @return predicates map for com.day.cq.search.QueryBuilder
   */
  @Override
  protected Map<String, String> prepareQueryMap() {
    final Map<String, String> queryMap = new HashMap<>();

    queryMap.put("p.limit", "-1");

    queryMap.put("path", PATH_TO_SEARCH);
    queryMap.put("type", RESOURCES_TO_SEARCH);

    queryMap.put("1_group.1_property", RESOURCE_TYPE);
    queryMap.put("1_group.1_property.value", PAGE);

    return queryMap;
  }

}
