package com.saudi.tourism.core.utils.deploy.scripts;

import com.google.common.collect.ImmutableMap;
import com.saudi.tourism.core.utils.Constants;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.Objects;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

/**
 * Separate field value 'No Cache' on cdn and dispatcher.
 */
public class NoCacheChangeScript extends PagePropertyMigrationScript {

  @Override protected Map<String, String> prepareQueryMap() {
    return ImmutableMap.<String, String>builder()
        .put("p.limit", "-1")
        .put("path", Constants.ROOT_CONTENT_PATH)
        .put("type", NT_PAGE)
        .put("property", "./jcr:content/noCacheHeaders")
        .put("property.operation", "exists")
        .build();
  }

  @Override protected void transferProperties(final Node node, final int pageNumber)
      throws RepositoryException {
    Node jcrNode = node.getNode(JCR_CONTENT);
    Property prop = jcrNode.getProperty("noCacheHeaders");
    if (Objects.nonNull(prop.getValue()) && prop.getValue().getBoolean()) {
      jcrNode.setProperty("noCacheHeaderCdn", true);
      jcrNode.setProperty("noCacheHeaderDispatcher", true);
    }
    prop.remove();
  }
}
