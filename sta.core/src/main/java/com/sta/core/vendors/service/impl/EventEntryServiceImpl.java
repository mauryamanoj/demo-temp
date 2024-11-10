package com.sta.core.vendors.service.impl;

import com.sta.core.vendors.PackageEntryUtils;
import com.sta.core.vendors.VendorsServiceImpl;
import com.sta.core.vendors.models.EventEntry;
import com.sta.core.vendors.models.EventListEntry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.day.cq.commons.jcr.JcrConstants;
import com.sta.core.vendors.service.EventEntryService;
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
import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static com.saudi.tourism.core.utils.VendorEntryConstants.EVENT_DATA_PATH_PREFIX;

/**
 * Service class for event entry processing.
 */
@Component(service = EventEntryService.class)
@Slf4j
public class EventEntryServiceImpl implements EventEntryService {

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
   *
   * @return resource resolver
   */
  private ResourceResolver getResourceResolver() {
    return PackageEntryUtils.getResourceResolver(userService);
  }

  /**
   * Save event node under
   * {@link com.saudi.tourism.core.utils.VendorEntryConstants.EVENT_DATA_PATH_PREFIX} path.
   *
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
        VendorsServiceImpl.getIdFromString(requestParameterMap.get(PN_TITLE).toString());
    String folderPath = PathUtils.concat(EVENT_DATA_PATH_PREFIX, dmc);
    Resource folderResource = ResourceUtil
        .getOrCreateResource(resourceResolver, folderPath, requestParameterMap,
            JcrConstants.NT_FOLDER, false);
    nodeName = ResourceUtil.createUniqueChildName(folderResource, nodeName);
    String nodePath = PathUtils.concat(folderPath, nodeName);
    requestParameterMap.putIfAbsent(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);

    try {
      requestParameterMap.computeIfPresent("region", LIST_TO_ARRAY);
      requestParameterMap.computeIfPresent("duration", LIST_TO_ARRAY);
      requestParameterMap.computeIfPresent("category", LIST_TO_ARRAY);
      requestParameterMap.computeIfPresent("target", LIST_TO_ARRAY);
    } catch (Exception e) {
      LOGGER.error("Error in list forming {}", e);
    }

    Resource resource = ResourceUtil
        .getOrCreateResource(resourceResolver, nodePath, requestParameterMap,
            JcrConstants.NT_UNSTRUCTURED, false);

    resourceResolver.commit();
    return resource.getPath();
  }

  /**
   * Gets event entry by node name.
   *
   * @param dmc  vendor name
   * @param name entry node name
   * @return instance of {@link EventEntry}
   */
  @Override public EventEntry getEntry(@NonNull final String dmc, @NonNull final String name) {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(EVENT_DATA_PATH_PREFIX, dmc, name);
      return Optional.ofNullable(resourceResolver.getResource(path))
          .map(resource -> resource.adaptTo(EventEntry.class))
          .orElseThrow(() -> new IllegalArgumentException("Unable to find event."));
    }
  }

  /**
   * Get event entries for user.
   *
   * @param dmc vendor name
   * @return list of instance of {@link EventEntry}
   */
  @Override public List<EventListEntry> getEntries(@NonNull final String dmc) {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(EVENT_DATA_PATH_PREFIX, dmc);
      Iterator<Resource> resources = resourceResolver.resolve(path).listChildren();
      return CommonUtils.iteratorToStream(resources)
          .map(resource -> resource.adaptTo(EventListEntry.class))
          .sorted((event1, event2) -> compareModifiedDate(event1.getModifiedDate(),
              event2.getModifiedDate()))
          .filter(entry -> StringUtils.isNotEmpty(entry.getTitle())).collect(Collectors.toList());
    }
  }

  /**
   * Compare modified date int.
   *
   * @param date1 the first event
   * @param date2 the second event
   * @return the compare value
   */
  public static int compareModifiedDate(final Calendar date1, final Calendar date2) {
    if (Objects.isNull(date1) && Objects.isNull(date2)) {
      return 0;
    }
    if (Objects.isNull(date1)) {
      return 1;
    }
    if (Objects.isNull(date2)) {
      return -1;
    }

    return date2.compareTo(date1);
  }

  /**
   * Delete entry.
   *
   * @param dmc      vendor name
   * @param nodeName node name
   * @throws PersistenceException exception
   */
  public void deleteEntry(@NonNull final String dmc, @NonNull final String nodeName)
      throws PersistenceException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      String path = PathUtils.concat(EVENT_DATA_PATH_PREFIX, dmc, nodeName);
      Resource resource = resourceResolver.getResource(path);
      if (Objects.nonNull(resource)) {
        resourceResolver.delete(resource);
        resourceResolver.commit();
      }
    }
  }

  /**
   * Update entry.
   *
   * @param dmc                 vendor name
   * @param name                node name
   * @param requestParameterMap parameter map
   * @throws PersistenceException persistence exception
   */
  public void updateEntry(@NonNull final String dmc, final String name,
      final @NonNull Map<String, Object> requestParameterMap) throws PersistenceException {
    try (ResourceResolver resourceResolver = getResourceResolver()) {
      if (Objects.isNull(name) || "null".equals(name)) {
        saveEntry(requestParameterMap, dmc, resourceResolver);
      }
      String path = PathUtils.concat(EVENT_DATA_PATH_PREFIX, dmc, name);
      Resource resource = resourceResolver.getResource(path);
      ValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
      requestParameterMap.entrySet().stream()
          .filter(entry -> !entry.getKey().equals(PN_ITINERARY_DETAILS)).forEach(entry -> {
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
