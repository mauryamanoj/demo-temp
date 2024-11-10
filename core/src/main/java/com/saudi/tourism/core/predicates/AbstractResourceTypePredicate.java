package com.saudi.tourism.core.predicates;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.predicate.AbstractNodePredicate;
import com.saudi.tourism.core.utils.ResourceUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * Abstract predicate for searching nodes by resource type. Can be used in pathfield and queries.
 * Returns the whole tree where searching resource types exist.
 */
public abstract class AbstractResourceTypePredicate extends AbstractNodePredicate
    implements Predicate {

  /**
   * Resource types that shouldn't be traversed.
   */
  private static final String[] SKIPPED_RESOURCE_TYPES =
      new String[] {"sauditourism/components/structure/configuration-page"};

  /**
   * Set this {@code true} to search components, not pages.
   */
  @Getter
  @Setter
  private boolean searchComponents = false;

  /**
   * The predicate will look for these resource types.
   *
   * @return list of resource types to search
   */
  abstract String[] getSearchingResourceTypes();

  /**
   * The predicate will skip those resource types (won't search inside).
   *
   * @return list of skipped resource types
   */
  protected String[] getSkippedResourceTypes() {
    return SKIPPED_RESOURCE_TYPES;
  }

  /**
   * The main evaluate method for the current predicate.
   *
   * @param node node to check
   * @return true if fits predicate rules
   * @throws RepositoryException if couldn't read node data from CRX
   */
  @Override
  public boolean evaluate(final Node node) throws RepositoryException {
    if (searchComponents) {
      return hasChildrenNodesOfSearchingType(node);
    }

    return hasChildrenPagesOfSearchingType(node);
  }

  /**
   * Check if the specified page is or has a child of searching resource type.
   *
   * @param node page node to traverse
   * @return {@code true} if the specified page contains children pages of searching resource type
   * @throws RepositoryException if couldn't read node data from CRX
   */
  boolean hasChildrenPagesOfSearchingType(final Node node) throws RepositoryException {
    // Don't skip folder nodes (as currently /Configs is a folder)
    if (!ResourceUtil.isFolder(node)) {
      // Skip nodes that don't have jcr:content and jcr:content nodes by themselves
      if (!node.hasNode(JcrConstants.JCR_CONTENT) || JcrConstants.JCR_CONTENT
          .equals(node.getName())) {
        return false;
      }

      final String contentNodeResourceType =
          getNodeResourceType(node.getNode(JcrConstants.JCR_CONTENT));

      // Check if the node is the page we're looking for
      if (ArrayUtils.contains(getSearchingResourceTypes(), contentNodeResourceType)) {
        return true;
      }

      // Skip other nodes, such as configuration pages
      if (ArrayUtils.contains(getSkippedResourceTypes(), contentNodeResourceType)) {
        return false;
      }
    }

    // Iterate children pages (nodes except jcr:content)
    final NodeIterator nodes = node.getNodes();
    while (nodes.hasNext()) {
      final Node childNode = (Node) nodes.next();
      if (JcrConstants.JCR_CONTENT.equals(childNode.getName())) {
        continue;
      }

      if (hasChildrenPagesOfSearchingType(childNode)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if the specified node is or has a child node (component) of searching resource type.
   *
   * @param node node to traverse
   * @return {@code true} if the specified node contains children of searching resource type
   * @throws RepositoryException if couldn't read node data from CRX
   */
  boolean hasChildrenNodesOfSearchingType(final Node node) throws RepositoryException {
    final String nodeResourceType = getNodeResourceType(node);

    // Check if the node is the node we're looking for
    if (ArrayUtils.contains(getSearchingResourceTypes(), nodeResourceType)) {
      return true;
    }

    // Skip other nodes, such as configuration pages
    if (ArrayUtils.contains(getSkippedResourceTypes(), nodeResourceType)) {
      return false;
    }

    // Iterate children nodes
    final NodeIterator nodes = node.getNodes();
    while (nodes.hasNext()) {
      if (hasChildrenNodesOfSearchingType((Node) nodes.next())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the resource type of specified node.
   *
   * @param currentNode node to check
   * @return resource type of the node
   * @throws RepositoryException if couldn't read node in CRX
   */
  String getNodeResourceType(Node currentNode) throws RepositoryException {
    if (!currentNode.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)) {
      return null;
    }

    return currentNode.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getString();
  }

}
