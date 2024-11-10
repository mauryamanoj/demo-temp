package com.saudi.tourism.core.utils.deploy.scripts;

import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Iterator;
import java.util.Map;

/**
 * Parent class for OnDeployScripts that fetches pages with QueryBuilder and modify its' properties.
 */
public abstract class PagePropertyMigrationScript extends OnDeployScriptBase  {

  /**
   * Fill query parameters.
   * @return query parameters map
   */
  protected abstract Map<String, String> prepareQueryMap();

  /**
   * Process properties assignment.
   * @param node current node
   * @param pageNumber current page index
   * @throws RepositoryException if node reading error
   */
  protected abstract void transferProperties(Node node, int pageNumber) throws RepositoryException;

  /**
   * Script execution.
   * @throws Exception exception
   */
  @Override protected void execute() {
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

      final Iterator<Node> nodes = result.getNodes();

      if (!nodes.hasNext()) {
        logger.error("Couldn't find nodes to transfer properties. Check access rules.");
        return;
      }

      int pageNum = 0;
      while (nodes.hasNext()) {
        transferProperties(nodes.next(), pageNum++);
      }

      logger.info("Properties transfer finished successfully. Processed {} pages", pageNum);

    } catch (Exception e) {
      logger.error("Properties transfer failed", e);
    }
  }

  /**
   * Returns string value of the property of node.
   *
   * @param node         node to get property
   * @param propertyName name of property to get
   * @param defaultValue default value to be returned if property doesn't exist
   * @return value of property
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
