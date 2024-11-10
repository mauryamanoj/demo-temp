package com.saudi.tourism.core.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static org.apache.sling.jcr.resource.api.JcrResourceConstants.NT_SLING_FOLDER;

/**
 * This class is used as a utility for resource related functions.
 */
@Slf4j
public final class ResourceUtil {

  /**
   * List of folders primary types.
   */
  private static final List<String> NODE_TYPES_FOLDERS =
      Arrays.asList(JcrConstants.NT_FOLDER, NT_SLING_FOLDER, DamConstants.NT_SLING_ORDEREDFOLDER);

  /**
   * private constructor because it's a utility class.
   */
  private ResourceUtil() {

  }

  /**
   * This method is used to get externalized URL.
   *
   * @param externalizer         Externalizer
   * @param resourceResolver     ResourceResolver
   * @param searchResultsPageUrl String
   * @param isAuthor             boolean
   * @return String
   */
  public static String getExternalizedUrl(final Externalizer externalizer,
      final ResourceResolver resourceResolver, final String searchResultsPageUrl,
      final boolean isAuthor) {
    String externalizeUrl = searchResultsPageUrl;
    if (!isAuthor) {
      externalizeUrl = externalizer.publishLink(resourceResolver, searchResultsPageUrl);
    }

    if (StringUtils.isNotEmpty(externalizeUrl)) {
      externalizeUrl = LinkUtils.getUrlWithExtension(externalizeUrl);
    }
    return externalizeUrl;
  }

  /**
   * Iterates children to find resource of specified type.
   *
   * @param resource     resource to start search from
   * @param resourceType resource type to search
   * @return found resource
   * <p>
   */
  public static Resource findChildResourceByType(final Resource resource,
      final String resourceType) {

    if (resource.isResourceType(resourceType)) {
      return resource;
    }

    for (Resource child : resource.getChildren()) {
      final Resource foundResource = findChildResourceByType(child, resourceType);
      if (foundResource != null) {
        return foundResource;
      }
    }

    return null;
  }

  /**
   * Get all children of the specified resource which are components. Is used for obtaining child
   * components of WCM Core Components' containers.
   *
   * @param containerResource wcm core components' container component resource
   * @return list of child components' resources
   */
  public static List<Resource> getContainerChildComponents(final Resource containerResource) {
    final ComponentManager componentManager =
        containerResource.getResourceResolver().adaptTo(ComponentManager.class);
    assert componentManager != null;

    final List<Resource> resultList = new LinkedList<>();

    for (Resource childResource : containerResource.getChildren()) {
      // Check if the resource is an existing component
      final Component component = componentManager.getComponentOfResource(childResource);
      if (component == null) {
        LOGGER.debug("Skipping child {} as it's not component", childResource);
        continue;
      }
      resultList.add(childResource);
    }

    return resultList;
  }

  /**
   * Get all children of the specified resource which are components, adapted to the needed model.
   * It is used for obtaining child components of WCM Core Components' containers with adapting.
   *
   * @param <T>               adaptable type
   * @param containerResource wcm core components' container component resource
   * @param typeClass         adaptable class for adapting child component resources
   * @return list of child components' models
   */
  public static <T> List<T> getContainerChildComponents(@NotNull final Resource containerResource,
      @NotNull Class<T> typeClass) {
    final ComponentManager componentManager =
        containerResource.getResourceResolver().adaptTo(ComponentManager.class);
    assert componentManager != null;

    final List<T> resultList = new LinkedList<>();

    for (Resource childResource : containerResource.getChildren()) {
      // Check if the resource is an existing component
      final Component component = componentManager.getComponentOfResource(childResource);
      if (component == null) {
        LOGGER.debug("Skipping child {} as it's not component", childResource);
        continue;
      }

      final T adapted = childResource.adaptTo(typeClass);
      if (adapted == null) {
        LOGGER.error("Couldn't adapt child resource {} to type {}", childResource, typeClass);
      } else {
        resultList.add(adapted);
      }
    }

    return resultList;
  }


  /**
   * Returns date when the resource was published.
   *
   * @param resource resource to check
   * @return publish date of the resource
   */
  public static Calendar getPublishDate(final Resource resource) {
    final ReplicationStatus replicationStatus = resource.adaptTo(ReplicationStatus.class);
    if (replicationStatus != null && !replicationStatus.isDeactivated()) {
      return replicationStatus.getLastPublished();
    }

    return null;
  }

  /**
   * Checks if the node is folder.
   *
   * @param node node to che3ck
   * @return {@code true} if the specified node is folder, not a page, otherwise {@code false}
   * @throws RepositoryException if couldn't read from jcr
   */
  public static boolean isFolder(@NotNull final Node node) throws RepositoryException {
    return NODE_TYPES_FOLDERS.contains(node.getPrimaryNodeType().getName());
  }
}
