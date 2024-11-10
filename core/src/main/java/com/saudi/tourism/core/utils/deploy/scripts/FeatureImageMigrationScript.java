package com.saudi.tourism.core.utils.deploy.scripts;

import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;
import static com.saudi.tourism.core.utils.Constants.FEATURE_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_FEATURE_EVENT_IMAGE;

/**
 * Script to add field 'variant' for old/existing component 'Banner Link'.
 */
@Generated
@Slf4j
public class FeatureImageMigrationScript extends PagePropertyMigrationScript {

  /**
   * Where to search: /content/sauditourism.
   */
  private static final String PATH_TO_SEARCH = Constants.SITE_ROOT;

  /**
   * Transfer latitude, longitude properties to locations node.
   *
   * @param contentNode node to get property
   * @param pageNum     to show in log
   * @throws RepositoryException if node reading error
   */
  protected void transferProperties(Node contentNode, int pageNum) throws RepositoryException {
    Node jcrContentNode = contentNode.getNode("jcr:content");
    if (Objects.isNull(jcrContentNode) || !jcrContentNode.hasProperty("sling:resourceType")) {
      return;
    }
    String resourceType = jcrContentNode.getProperty("sling:resourceType").getString();
    Node node;
    switch (resourceType) {
      case "sauditourism/components/structure/event-detail-page":
        node = getChildNode(jcrContentNode, "detail-hero");
        processImageToFeatureImage(jcrContentNode, node, PN_FEATURE_EVENT_IMAGE);
        break;
      case "sauditourism/components/structure/package-detail-page":
        break;
      default:
        Node responsivegrid = Optional.ofNullable(getChildNode(jcrContentNode, "root"))
            .map(n -> getChildNode(n, "responsivegrid")).orElse(null);
        if (Objects.nonNull(responsivegrid)) {
          NodeIterator it = responsivegrid.getNodes();
          if (it.hasNext()) {
            node = it.nextNode();
            processImageToFeatureImage(jcrContentNode, node, FEATURE_IMAGE);
          }
        }
    }
  }

  /**
   * processImageToFeatureImage.
   *
   * @param jcrContentNode   node
   * @param node             node
   * @param destinationField destinationField
   */
  private static void processImageToFeatureImage(Node jcrContentNode, Node node,
      String destinationField) {
    Optional.ofNullable(node).map(n -> getChildNode(n, "image")).ifPresent(image -> {
      try {
        if (!image.hasProperty("fileReference")) {
          return;
        }
        String fileReference = image.getProperty("fileReference").getString();
        if (StringUtils.isNotBlank(fileReference)) {
          jcrContentNode.setProperty(destinationField, fileReference);
          if (image.hasProperty("s7fileReference")) {
            String s7fileReference = image.getProperty("s7fileReference").getString();
            if (StringUtils.isNotBlank(s7fileReference)) {
              jcrContentNode.setProperty("s7" + destinationField, s7fileReference);
            }
          }
        }
      } catch (RepositoryException e) {
        LOGGER.error("error", e);
      }
    });
  }

  /**
   * Get child node without exception.
   *
   * @param node      node
   * @param childName childName
   * @return child node
   */
  private static Node getChildNode(Node node, String childName) {
    try {
      return node.getNode(childName);
    } catch (RepositoryException e) {
      return null;
    }
  }

  /**
   * Prepares predicates map for searching pages query.
   *
   * @return predicates map for com.day.cq.search.QueryBuilder
   */
  protected Map<String, String> prepareQueryMap() {
    final Map<String, String> queryMap = new HashMap<>();
    queryMap.put("p.limit", "-1");
    queryMap.put("path", PATH_TO_SEARCH);
    queryMap.put("type", NT_PAGE);
    return queryMap;
  }
}
