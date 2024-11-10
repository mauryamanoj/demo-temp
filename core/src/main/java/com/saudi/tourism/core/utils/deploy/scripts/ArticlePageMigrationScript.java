package com.saudi.tourism.core.utils.deploy.scripts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static com.saudi.tourism.core.utils.PrimConstants.ROOT_CONTENT_PATH;

/**
 * Migrate content-page template to article-page template
 * based on c12-article-detail or c13-article-card existence.
 */
@Generated
public class ArticlePageMigrationScript extends OnDeployScriptBase {
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
   * Content Page Template.
   */
  private static final String CONTENT_PAGE_TEMPLATE =
      "/conf/sauditourism/settings/wcm/templates/content-page";

  /**
   * Article Page Template.
   */
  private static final String ARTICLE_PAGE_TEMPLATE =
      "/conf/sauditourism/settings/wcm/templates/article-page";

  /**
   * "C-12 Article Detail Component" resource type.
   */
  private static final String ARTICLE_DETAIL_RES_TYPE =
      "sauditourism/components/content/c12-article-detail/v1/c12-article-detail";

  /**
   * "C-13 Article Card Component" resource type.
   */
  private static final String ARTICLE_CARD_RES_TYPE =
      "sauditourism/components/content/c13-article-card/v1/c13-article-card";

  @Override
  protected void execute() throws Exception {
    logger.info("OnDeploy Script - execute started");

    try {
      final QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
      if (queryBuilder == null) {
        logger.error("Couldn't get QueryBuilder. Processing failed");
        return;
      }

      final Map<String, String> queryMap = prepareQueryMap();
      final Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), getSession());
      final SearchResult result = query.getResult();

      final Iterator<Resource> it = result.getResources();

      if (!it.hasNext()) {
        logger.error("Couldn't find nodes. Check access rules.");
        return;
      }

      int pageNum = 0;
      final Set<String> processedPages = new HashSet<>();
      while (it.hasNext()) {
        Resource resource = it.next();
        Page page = getPageManager().getContainingPage(resource);
        String pagePath = page.getPath();
        if (processedPages.contains(pagePath)) {
          logger.debug("Skipping page : [{}], already processed", pagePath);
          continue;
        }
        processedPages.add(pagePath);
        updatePageTemplate(page.adaptTo(Node.class), pageNum++);
      }

      logger.info("Template update finished successfully. Processed {} pages", pageNum);

    } catch (Exception e) {
      logger.error("Template update failed", e);
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
    queryMap.put("1_group.1_property.value", ARTICLE_DETAIL_RES_TYPE);
    queryMap.put("1_group.2_property", SLING_RESOURCE_TYPE);
    queryMap.put("1_group.2_property.value", ARTICLE_CARD_RES_TYPE);
    queryMap.put("1_group.p.or", "true");
    return queryMap;
  }

  /**
   * Updates page template if needed.
   *
   * @param node       current page node
   * @param pageNumber current page index
   *
   * @throws RepositoryException if node reading error
   */
  protected void updatePageTemplate(Node node, int pageNumber) throws RepositoryException {
    Node contentNode = node.getNode(JcrConstants.JCR_CONTENT);
    if (contentNode == null) {
      logger.debug("Skipping page ({}): [{}], no content node", pageNumber, node.getPath());
      return;
    }
    String template = getStrProperty(contentNode, NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
    if (template.equals(CONTENT_PAGE_TEMPLATE)) {
      logger.debug("Processing page ({}): [{}], Template: {}", pageNumber, node.getPath(), template);
      contentNode.setProperty(NameConstants.NN_TEMPLATE, ARTICLE_PAGE_TEMPLATE);
    } else {
      logger.debug("Skipping page ({}): [{}], Template: {}", pageNumber, node.getPath(), template);
    }
  }

  /**
   * Returns string property value or default value if property doesn't exist.
   *
   * @param node          node to get property from
   * @param propertyName  property name
   * @param defaultValue  default value
   *
   * @return property value or default value if property doesn't exist
   *
   * @throws RepositoryException if node reading error
   */
  protected static String getStrProperty(Node node, String propertyName, String defaultValue)
      throws RepositoryException {
    if (node == null || !node.hasProperty(propertyName)) {
      return defaultValue;
    }
    return node.getProperty(propertyName).getString();
  }
}
