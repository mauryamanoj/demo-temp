package com.sta.core.vendors.service;

import com.sta.core.vendors.models.PackageEntry;
import com.sta.core.vendors.models.PackageListEntry;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Package entry processing service.
 */
public interface PackageEntryService {

  /**
   * Save package node.
   * @param requestParameterMap parameters map
   * @param dmc vendor name
   * @return path to the created node
   * @throws PersistenceException exception during the save phase
   */
  String saveEntry(@NonNull Map<String, Object> requestParameterMap, @NonNull String dmc)
      throws PersistenceException;

  /**
   * Save package node.
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
   * Gets package entry by node name.
   * @param dmc vendor name
   * @param name entry node name
   * @return instance of {@link PackageEntry}
   */
  PackageEntry getEntry(@NonNull String dmc, @NonNull String name);

  /**
   * Get package entries for user.
   * @param dmc vendor name
   * @return list of instance of {@link PackageEntry}
   */
  List<PackageListEntry> getEntries(@NonNull String dmc);

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
