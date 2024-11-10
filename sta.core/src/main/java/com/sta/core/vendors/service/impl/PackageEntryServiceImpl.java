package com.sta.core.vendors.service.impl;

import com.sta.core.vendors.PackageEntryUtils;
import com.sta.core.vendors.VendorsServiceImpl;
import com.sta.core.vendors.models.PackageEntry;
import com.sta.core.vendors.models.PackageListEntry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.day.cq.commons.jcr.JcrConstants;
import com.sta.core.vendors.service.PackageEntryService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.utils.Constants.PN_ITINERARY_DETAILS;
import static com.saudi.tourism.core.utils.VendorEntryConstants.PACKAGE_DATA_PATH_PREFIX;

/**
 * Service class for package entry processing.
 */
@Component(service = PackageEntryService.class)
@Slf4j
public class PackageEntryServiceImpl implements PackageEntryService {

  /**
   * List to array converter.
   */
  public static final BiFunction<String, Object, String[]> LIST_TO_ARRAY = (key, value) -> {
    List<String> list = (List<String>) value;
    return list.toArray(new String[list.size()]);
  };

  /**
   * User service.
   */
  @Reference
  private UserService userService;

  /**
   * Get service user's resource resolver.
   * @return resource resolver
   */
  private ResourceResolver getResourceResolver() {
    return PackageEntryUtils.getResourceResolver(userService);
  }

  /**
   * Save package node under
   * @param dmc vendor name
   * @return path to the created node
   * @throws PersistenceException exception during the save phase
   */
  @Override public String saveEntry(@NonNull final Map<String, Object> reqMap,
      @NonNull final String dmc) throws PersistenceException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      return saveEntry(reqMap, dmc, resourceResolver);
    }
  }

  @Override public String saveEntry(final @NonNull Map<String, Object> requestParameterMap,
      @NonNull final String dmc, final @NonNull ResourceResolver resourceResolver)
      throws PersistenceException {
    String nodeName =
        VendorsServiceImpl.getIdFromString(requestParameterMap.get("name").toString());
    String folderPath = PathUtils.concat(PACKAGE_DATA_PATH_PREFIX, dmc);
    Resource folderResource = ResourceUtil.getOrCreateResource(resourceResolver, folderPath,
        requestParameterMap, JcrConstants.NT_FOLDER, false);
    nodeName = ResourceUtil.createUniqueChildName(folderResource, nodeName);
    String nodePath = PathUtils.concat(folderPath, nodeName);
    requestParameterMap.putIfAbsent(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);

    requestParameterMap.computeIfPresent("region", LIST_TO_ARRAY);
    requestParameterMap.computeIfPresent("city", LIST_TO_ARRAY);
    requestParameterMap.computeIfPresent("duration", LIST_TO_ARRAY);
    requestParameterMap.computeIfPresent("category", LIST_TO_ARRAY);
    requestParameterMap.computeIfPresent("target", LIST_TO_ARRAY);
    List<Map<String, Object>> itinerary =
        (List<Map<String, Object>>) requestParameterMap.get(PN_ITINERARY_DETAILS);
    requestParameterMap.remove(PN_ITINERARY_DETAILS);

    Resource resource = ResourceUtil.getOrCreateResource(resourceResolver, nodePath,
        requestParameterMap, JcrConstants.NT_UNSTRUCTURED, false);

    if (CollectionUtils.isNotEmpty(itinerary)) {
      Resource parent = resourceResolver.create(resource, PN_ITINERARY_DETAILS, new HashMap<>());
      int i = 0;
      for (Map<String, Object> map : itinerary) {
        map.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        resourceResolver.create(parent, "item" + i++, map);
      }
    }

    resourceResolver.commit();
    return resource.getPath();
  }

  /**
   * Gets package entry by node name.
   * @param dmc vendor name
   * @param name entry node name
   * @return instance of {@link PackageEntry}
   */
  @Override public PackageEntry getEntry(@NonNull final String dmc, @NonNull final String name) {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(PACKAGE_DATA_PATH_PREFIX, dmc, name);
      return Optional.ofNullable(resourceResolver.getResource(path))
          .map(resource -> resource.adaptTo(PackageEntry.class))
          .orElseThrow(() -> new IllegalArgumentException("Unable to find package."));
    }
  }

  /**
   * Get package entries for user.
   * @param dmc vendor name
   * @return list of instance of {@link PackageEntry}
   */
  @Override public List<PackageListEntry> getEntries(@NonNull final String dmc) {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(PACKAGE_DATA_PATH_PREFIX, dmc);
      Iterator<Resource> resources = resourceResolver.resolve(path).listChildren();
      return CommonUtils.iteratorToStream(resources)
          .map(resource -> resource.adaptTo(PackageListEntry.class))
          .sorted((p1, p2) -> EventEntryServiceImpl.compareModifiedDate(p1.getModifiedDate(),
              p2.getModifiedDate()))
          .filter(entry -> StringUtils.isNotEmpty(entry.getTitle()))
          .collect(Collectors.toList());
    }
  }

  /**
   * Delete entry.
   * @param dmc vendor name
   * @param nodeName node name
   * @throws PersistenceException exception
   */
  public void deleteEntry(@NonNull final String dmc,
      @NonNull final String nodeName) throws PersistenceException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(PACKAGE_DATA_PATH_PREFIX, dmc, nodeName);
      Resource resource = resourceResolver.getResource(path);
      if (Objects.nonNull(resource)) {
        resourceResolver.delete(resource);
        resourceResolver.commit();
      }
    }
  }

  /**
   * Update entry.
   * @param dmc vendor name
   * @param name node name
   * @param requestParameterMap parameter map
   * @throws PersistenceException persistence exception
   */
  public void updateEntry(@NonNull final String dmc, @NonNull final String name,
      final @NonNull Map<String, Object> requestParameterMap) throws PersistenceException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      if ("null".equals(name)) {
        saveEntry(requestParameterMap, dmc, resourceResolver);
      }
      String path = PathUtils.concat(PACKAGE_DATA_PATH_PREFIX, dmc, name);
      Resource resource = resourceResolver.getResource(path);
      ValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
      requestParameterMap.entrySet().stream()
          .filter(entry -> !entry.getKey().equals(PN_ITINERARY_DETAILS))
          .forEach(entry -> {
            Object value = entry.getValue();
            if (value instanceof List) {
              String[] property = LIST_TO_ARRAY.apply(entry.getKey(), entry.getValue());
              valueMap.put(entry.getKey(), property);
            } else {
              valueMap.put(entry.getKey(), value);
            }
          });
      requestParameterMap.get(PN_ITINERARY_DETAILS);
      List<Map<String, Object>> itinerary =
          (List<Map<String, Object>>) requestParameterMap.get(PN_ITINERARY_DETAILS);
      Resource itineraryNode = resource.getChild(PN_ITINERARY_DETAILS);
      resourceResolver.delete(itineraryNode);

      if (CollectionUtils.isNotEmpty(itinerary)) {
        Resource parent = resourceResolver.create(resource, PN_ITINERARY_DETAILS, new HashMap<>());
        int i = 0;
        for (Map<String, Object> map : itinerary) {
          map.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
          resourceResolver.create(parent, "item" + i++, map);
        }
      }

      resourceResolver.commit();
    }
  }

}
