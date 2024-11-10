package com.saudi.tourism.core.predicates;

import com.day.crx.JcrConstants;
import lombok.Getter;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.jupiter.api.Test;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit Test for AbstractResourceTypePredicate.
 */
@Getter
class AbstractResourceTypePredicateTest {

  private static final String SOME_RESOURCE_TYPE = "some-resource-type";

  final AbstractResourceTypePredicate testPredicate;

  private Node emptyNode;

  private Node jcrContentNode;

  private Node hasJcrContentNode;

  public AbstractResourceTypePredicateTest() throws RepositoryException {
    final String[] someResourceType = {SOME_RESOURCE_TYPE};
    testPredicate = mock(AbstractResourceTypePredicate.class, CALLS_REAL_METHODS);
    doReturn(someResourceType).when(testPredicate).getSearchingResourceTypes();

    final NodeType someGoodNodeType = mock(NodeType.class);
    doReturn(JcrConstants.NT_UNSTRUCTURED).when(someGoodNodeType).getName();

    emptyNode = mock(Node.class);
    doReturn(someGoodNodeType).when(emptyNode).getPrimaryNodeType();

    final NodeIterator noChildrenIterator = mock(NodeIterator.class);
    doReturn(noChildrenIterator).when(emptyNode).getNodes();

    jcrContentNode = mock(Node.class);
    doReturn(someGoodNodeType).when(jcrContentNode).getPrimaryNodeType();
    doReturn(JcrConstants.JCR_CONTENT).when(jcrContentNode).getName();
    doReturn(true).when(getJcrContentNode())
        .hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);

    final Property resourceTypeProperty = mock(Property.class);
    doReturn(SOME_RESOURCE_TYPE).when(resourceTypeProperty).getString();

    doReturn(resourceTypeProperty).when(jcrContentNode)
        .getProperty(eq(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY));

    hasJcrContentNode = mock(Node.class);
    doReturn(someGoodNodeType).when(hasJcrContentNode).getPrimaryNodeType();
    doReturn(jcrContentNode).when(hasJcrContentNode).getNode(eq(JcrConstants.JCR_CONTENT));
    doReturn(true).when(hasJcrContentNode).hasNode(eq(JcrConstants.JCR_CONTENT));
  }

  // TODO Add test: hasChildrenNodesOfFolderNode (for testing /Configs node)

  @Test
  void evaluate() throws RepositoryException {
    // Search components
    testPredicate.setSearchComponents(true);
    doReturn(true).when(testPredicate).hasChildrenNodesOfSearchingType(any(Node.class));
    assertTrue(testPredicate.evaluate(emptyNode));
    verify(testPredicate, times(1)).hasChildrenNodesOfSearchingType(any(Node.class));
    verify(testPredicate, times(0)).hasChildrenPagesOfSearchingType(any(Node.class));

    testPredicate.setSearchComponents(false);
    assertFalse(testPredicate.evaluate(emptyNode));
    reset(testPredicate);

    // Search pages
    testPredicate.setSearchComponents(false);
    doReturn(true).when(testPredicate).hasChildrenPagesOfSearchingType(any(Node.class));
    assertTrue(testPredicate.evaluate(emptyNode));
    verify(testPredicate, times(0)).hasChildrenNodesOfSearchingType(any(Node.class));
    verify(testPredicate, times(1)).hasChildrenPagesOfSearchingType(any(Node.class));

    testPredicate.setSearchComponents(true);
    assertFalse(testPredicate.evaluate(emptyNode));
    reset(testPredicate);
  }

  @Test
  void hasChildrenPagesOfSearchingType() throws RepositoryException {
    assertFalse(testPredicate.hasChildrenPagesOfSearchingType(emptyNode));
    assertTrue(testPredicate.hasChildrenPagesOfSearchingType(hasJcrContentNode));
  }

  @Test
  void hasChildrenNodesOfSearchingType() throws RepositoryException {
    assertFalse(testPredicate.hasChildrenNodesOfSearchingType(emptyNode));
  }

  @Test
  void getNodeResourceType() throws RepositoryException {
    assertNull(testPredicate.getNodeResourceType(emptyNode));
    assertEquals(SOME_RESOURCE_TYPE, testPredicate.getNodeResourceType(jcrContentNode));
  }
}
