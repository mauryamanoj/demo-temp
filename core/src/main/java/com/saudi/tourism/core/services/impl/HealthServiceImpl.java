package com.saudi.tourism.core.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.HealthService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Health Service.
 */
@Component(service = HealthService.class)
public class HealthServiceImpl implements HealthService {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(HealthServiceImpl.class);

  /**
   * Health Method.
   */
  @Override
  public JsonObject health(SlingHttpServletRequest request, SlingHttpServletResponse response, String address) {
    ResourceResolver resourceResolver = request.getResourceResolver();
    Session session = resourceResolver.adaptTo(Session.class);
    String source = request.getParameter("source");
    if (source != null && source.equals("web"))  {
      response.setContentType("application/json");
    } else {
      response.setContentType("text/html");
    }
    Node node = null;
    JsonObject requirement = new JsonObject();
    try {
      if (session != null) {
        node = session.getNode(address);
      }

      if (!node.getProperty("notification").getString().isEmpty()) {
        requirement.addProperty("notification", node.getProperty("notification").getString());
      }
      if (node.hasNode("section")) {
        Node section = node.getNode("section");
        NodeIterator children = section.getNodes();
        JsonArray sectionArray = new JsonArray();
        while (children.hasNext()) {
          Node child = children.nextNode();
          JsonObject sectionEntry = new JsonObject();
          JsonObject contentSection = new JsonObject();
          if (child.hasProperty("sectionheading")) {
            sectionEntry.addProperty("heading", child.getProperty("sectionheading").getString());
          }
          if (child.hasProperty("sectiondescription")) {
            contentSection.addProperty("text", child.getProperty("sectiondescription").getString());
            sectionEntry.add("content", contentSection);
          }
          if (child.hasNode("subsection")) {
            Node subSection = child.getNode("subsection");
            NodeIterator subsectionChildren = subSection.getNodes();
            JsonArray subsectionArray = new JsonArray();
            while (subsectionChildren.hasNext()) {
              Node subsectionChild = subsectionChildren.nextNode();
              JsonObject subsectionEntry = new JsonObject();
              JsonObject appEntry = new JsonObject();
              if (subsectionChild.hasProperty("subheading")) {
                subsectionEntry.addProperty("heading", subsectionChild.getProperty("subheading").getString());
              }
              if (subsectionChild.hasProperty("subheadingdescription")) {
                subsectionEntry.addProperty("content",
                    subsectionChild.getProperty("subheadingdescription").getString());
              }
              if (subsectionChild.hasProperty("subHeadingImageRef")) {
                subsectionEntry.addProperty("image",
                    subsectionChild.getProperty("subHeadingImageRef").getString());
              }
              if (subsectionChild.hasProperty("downloadtext")) {
                appEntry.addProperty("text", subsectionChild.getProperty("downloadtext").getString());

              }
              if (subsectionChild.hasProperty("googlePlayImageRef")) {
                appEntry.addProperty("play_store_image",
                    subsectionChild.getProperty("googlePlayImageRef").getString());
              }
              if (subsectionChild.hasProperty("googlePlayLink")) {
                appEntry.addProperty("play_store_link",
                    subsectionChild.getProperty("googlePlayLink").getString());
              }
              if (subsectionChild.hasProperty("appStoreImageRef")) {
                appEntry.addProperty("app_store_image",
                    subsectionChild.getProperty("appStoreImageRef").getString());
              }
              if (subsectionChild.hasProperty("appStoreLink")) {
                appEntry.addProperty("app_store_link",
                    subsectionChild.getProperty("appStoreLink").getString());
              }
              Boolean status = appEntry.has("text");
              if (status) {
                subsectionEntry.add("app", appEntry);
              }
              subsectionArray.add(subsectionEntry);
            }
            contentSection.add("sub_item", subsectionArray);
            sectionEntry.add("content", contentSection);
          }
          sectionArray.add(sectionEntry);
        }
        requirement.add("section_data", sectionArray);
      }
    } catch (RepositoryException e) {
      logger.info(e.getMessage());
    }
    return requirement;
  }
}
