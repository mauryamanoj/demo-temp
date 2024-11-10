package com.sta.core.vendors.service;

import com.sta.core.vendors.models.EventEntry;
import com.sta.core.vendors.models.EventListEntry;
import lombok.NonNull;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;

/**
 * Event entry processing service.
 */
public interface EventEntryService {

  /**
   * Save event node.
   * @param requestParameterMap parameters map
   * @param dmc vendor name
   * @return path to the created node
   * @throws PersistenceException exception during the save phase
   */
  String saveEntry(@NonNull Map<String, Object> requestParameterMap, @NonNull String dmc)
      throws PersistenceException;

  /**
   * Save event node.
   * @param requestParameterMap parameters map
   * @param dmc vendor name
   * @param resourceResolver resource resolver
   * @return path to the created node
   * @throws PersistenceException exception during the save phase
   */
  String saveEntry(@NonNull Map<String, Object> requestParameterMap, @NonNull String dmc,
      ResourceResolver resourceResolver)
      throws PersistenceException;

  /**
   * Gets event entry by node name.
   * @param dmc vendor name
   * @param name entry node name
   * @return instance of {@link EventEntry}
   */
  EventEntry getEntry(@NonNull String dmc, @NonNull String name);

  /**
   * Get event entries for user.
   * @param dmc vendor name
   * @return list of instance of {@link EventEntry}
   */
  List<EventListEntry> getEntries(@NonNull String dmc);

  /**
   * Delete entry.
   * @param dmc vendor name
   * @param nodeName node name
   * @throws PersistenceException exception
   */
  void deleteEntry(@NonNull String dmc, @NonNull String nodeName) throws PersistenceException;

  /**
   * Update entry.
   * @param dmc vendor name
   * @param name node name
   * @param requestParameterMap parameter map
   * @throws PersistenceException persistence exception
   */
  void updateEntry(@NonNull String dmc, @NonNull String name,
      @NonNull Map<String, Object> requestParameterMap) throws PersistenceException;

}
